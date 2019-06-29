package com.gumbocoin

import java.util.*
import kotlin.concurrent.thread


fun execute(namespace :Namespace, allNamespaces :List<Namespace>){
    val main = namespace.functions
        .firstOrNull { it.name == "main" && it.arguments.isEmpty() && it.returnType == Type.default() }
        ?: compileError("Can't find main method in namespace ${namespace.name}")

    val result = executeFunction(main,emptyList(),allNamespaces)
    if(result.hasError()){
        OUT.errorln("Error:" + result.getError().message + " \n type:" + result.getError().type + " \n at:" + result.getError().stacktrace.fold("") {a,b -> "$a\n\t${b.functionName}" })
        error("GError during runtime")
    }
}



fun executeFunction(gFunction: GFunction, arguments: List<Variable>, allNamespaces :List<Namespace>) :ReturnValue{

    if(!gFunction.arguments.all { arg ->
            val variable = arguments.firstOrNull { it.name == arg.name }
            variable != null && variable.type == arg.type
        }) error("Passed different arguments for function\nfunc:$gFunction\nvariables:$arguments")
    return when(gFunction){
        is GumboFunction -> executeFunction(gFunction,arguments,allNamespaces)
        is KotlinFunction -> executeFunction(gFunction,arguments)
        else -> error("Can't handle functions of type ${gFunction.javaClass.simpleName}")
    }
}
fun executeFunction(func :KotlinFunction, arguments: List<Variable>) :ReturnValue{
    return func.executor.invoke(arguments.map { it.value })
}

object FunctionDepth{
    var stackDepth = 0
}

object VoidType : Value(Type.void(),Unit)

class StackFrame(val functionName: String, val line :Line)

class GError(val message :String, val type :GErrorType,val stacktrace :List<StackFrame>){
    fun newStack(frame :StackFrame):GError{
        return GError(message,type,stacktrace + frame)
    }

    override fun toString(): String {
        return "$type:$message\nat" + stacktrace.fold(""){a,b -> "$a\n\t${b.functionName}" }
    }
}

enum class GErrorType{
    STACKOVERFLOW,
    FUNCTION_DID_NOT_RETURN_CORRECT_TYPE,
    USER_DEFINED_ERROR,

    KOTLIN_EXCEPTION_THROWN
}


class ReturnValue private constructor(
    private val value :Value?,
    private val error :GError?){
    companion object{
        fun error(error :GError): ReturnValue {
            if(Parsed.crashKotlinOnKotlin) {
                System.err.println("Error:" + error.message + " \n type:" + error.type + " \n at:" + error.stacktrace.fold("") {a,b -> "$a\n\t${b.functionName}" })
                System.err.println()
                System.err.flush()
                error("GError during runtime")
            }
            return ReturnValue(null,error)
        }
        fun void() = ReturnValue(Value(Type.void(),VoidType))
    }
    constructor(value :Value):this(value,null)

    fun value():Value{
        return value!!
    }
    fun hasError():Boolean{
        return error != null
    }
    fun getError():GError{
        return error!!
    }
}

fun executeFunction(gFunction: GumboFunction, arguments :List<Variable>, allNamespaces :List<Namespace>) :ReturnValue {

    if(FunctionDepth.stackDepth > Parsed.stackDepth){
        return ReturnValue.error(GError("Stack overflow",GErrorType.STACKOVERFLOW,listOf(StackFrame(gFunction.fullName,gFunction.startingLine))))
    }
    val result =try {
        val stack = SimpleVariableStack()
        for (arg in arguments) {
            stack.addVariable(arg)
        }
        FunctionDepth.stackDepth++
         try {
            executeBlock(gFunction.lines, stack, allNamespaces)
        } catch (e: StackOverflowError) {
            error("Stack overflow overflow - set max depth to something lower then ${FunctionDepth.stackDepth}")
        }
    }catch(e :Exception){
        thread(start = true){
            Thread.sleep(100)
            throwException(e)
        }
        return ReturnValue.error(GError(
            message = "Exception:" + e.message,
            type = GErrorType.KOTLIN_EXCEPTION_THROWN,
            stacktrace = listOf(StackFrame(
                functionName = gFunction.fullName,
                line = gFunction.startingLine
            ))
        ))
    }

    FunctionDepth.stackDepth--

    val r = result.orElseGet { ReturnValue.void() }
    if(r.hasError()){
        return ReturnValue.error(r.getError().newStack(StackFrame(gFunction.fullName,gFunction.startingLine)))
        //TODO this should be handled when the function is called and should be the line that the function is called,
        //   not the line that the function exists on
        //   duh
    }
    if(r.value().type != gFunction.returnType){
        return ReturnValue.error(GError(
            "Function did not return the correct type of value\nExpected ${gFunction.returnType} \ngot ${r.value().type}",
            GErrorType.FUNCTION_DID_NOT_RETURN_CORRECT_TYPE,listOf(StackFrame(gFunction.fullName,gFunction.startingLine))))
    }
    return r
}

fun executeBlock(block :CompiledBlock, variableStack :VariableStack, allNamespaces :List<Namespace>): Optional<ReturnValue> {
    val localStack = SimpleVariableStack()

    for(line in block.lines){
        when(line){
            is CompiledDebugLine -> {
                debug("Debug variables:")
                (localStack + variableStack)
                    .getAllVariables()
                    .forEach {
                        debug("\t" + it.type  + ": "+
                                it.name + (if(it.isFinal) "(final)" else "       ") + " = " +
                                stringify(it.value.value) + " (" + it.value.value::class.simpleName + ")" )
                    }
            }
            is VariableDecelerationLine -> {
                val value = executeExpression(line.expression,localStack + variableStack,allNamespaces)
                if(value.hasError()){
                    return Optional.of(value)
                }
                localStack.addVariable(Variable(
                    name = line.name,
                    value = value.value(),
                    isFinal = line.isFinal))
            }
            is VariableReassignmentLine -> {
                val value = executeExpression(line.expression,localStack + variableStack,allNamespaces)
                if(value.hasError()){
                    return Optional.of(value)
                }
                val variable = localStack.getVariableForName(line.name) ?: variableStack.getVariableForName(line.name)
                ?: error("Can't find variable ${line.name}")
                if(variable.isFinal){
                    error("Setting final variable ${line.name}")
                }
                if(value.value().type != variable.type){
                    error("Got type ${value.value().type} when settings variable ${line.name} which is of type ${variable.type}")
                }
                variable.value = value.value()
            }
            is IfStatement -> {
                val value = executeExpression(line.expression,localStack + variableStack,allNamespaces)
                if(value.hasError()){
                    return Optional.of(value)
                }
                if(value.value().value as Boolean){
                    //executeExpression(line.expression,localStack + variableStack,allNamespaces)
                    val result = executeBlock(line.block,localStack + variableStack,allNamespaces)
                    if(result.isPresent)
                        return result
                }
            }
            is WhileStatement -> {
                while(executeExpression(line.expression,localStack + variableStack,allNamespaces).value().value as Boolean){
                    val result = executeBlock(line.block,localStack + variableStack,allNamespaces)
                    if(result.isPresent)
                        return result
                }
            }
            is FunctionCallLine -> {
                val function = findFunction(line.function,allNamespaces)
                val result = executeFunction(
                    gFunction = function,
                    arguments = line.expressions.mapIndexed { index, expression ->
                        val arg = function.arguments[index]
                        val value =  executeExpression(
                            expression = expression,
                            variableStack = (variableStack + localStack),
                            allNamespaces = allNamespaces)
                        if(value.hasError()){
                            return Optional.of(value)
                        }
                        Variable(
                            name = arg.name,
                            value = value.value(),
                            isFinal = true
                        )
                    },
                    allNamespaces = allNamespaces)
                if(result.hasError()){
                    return Optional.of(result)
                }
            }
            is ReturnLine -> {
                val result = executeExpression(line.expression,(localStack + variableStack),allNamespaces)
                return Optional.of(result)
            }
            is ErrorLine -> {
                return Optional.of(ReturnValue.error(GError(
                    message = line.message,
                    type = GErrorType.USER_DEFINED_ERROR,
                    stacktrace = listOf(StackFrame("line ${line.line.lineNumber}",line.line))
                )))
            }
            else -> error("Can't handle lines of type ${line.javaClass.simpleName}")
        }
    }
    return Optional.empty()
}


/**
 * Ohh god
 * what have I created
 */
private fun findFunction(plaintextFunction: PlaintextFunction, allNamespaces: List<Namespace>):GFunction{
    // gumbo:is:a:good:squid()
    // structure = [gumbo,is,a,good]
    val structure = mutableListOf<String>()
    var parent = plaintextFunction.parent ?: error("Function not declared with parent")
    while(parent.parent != null){
        structure.add(0,parent.name)
        parent = parent.parent!!
    }
    structure.add(0,parent.name)
    var retur = allNamespaces.first { it.name == structure[0] }
    for(index in 1 until structure.size){
        retur = retur.subs.first { it.name == structure[index] }
    }
    return retur.functions.first { it.name == plaintextFunction.name }
}

fun executeExpression(expression: Expression, variableStack: VariableStack,allNamespaces: List<Namespace>):ReturnValue{
    return when(expression){
        is ValueExpression -> ReturnValue(expression.value)
        is VariableExpression -> ReturnValue((variableStack.getVariableForName(expression.name)
            ?: error("Can't find variable ${expression.name} at runtime")).value)
        is FunctionCallExpression -> {
            val func = findFunction(expression.function,allNamespaces)
            val arguments = expression.expressions.mapIndexed { index, e ->
                val arg = func.arguments[index]
                val value =  executeExpression(
                    expression = e,
                    variableStack = variableStack,
                    allNamespaces = allNamespaces)
                if(value.hasError()){
                    return value
                }
                Variable(
                    name = arg.name,
                    value = value.value(),
                    isFinal = true
                )
            }
            return executeFunction(
                gFunction = func,
                arguments = arguments,
                allNamespaces = allNamespaces)
        }
        is CondenserExpression -> {
            val condenser = condensers.first { it.name == expression.condenser }
            val arguments = mutableListOf<ReturnValue>()
            for(express in expression.arguments){
                val retur =executeExpression(
                    expression = express,
                    variableStack = variableStack,
                    allNamespaces = allNamespaces)
                if(retur.hasError()){
                    return retur//TODO add this stack additions
                }
                arguments.add(retur)
            }
            condenser.condenser.condense(arguments.map { it.value() })
        }
        else -> error("Can't handle expressions of type ${expression.javaClass.simpleName}")
    }
}


class Variable(
    val name :String,
    value :Value,
    val isFinal :Boolean){

    val type = value.type

    var value = value
        set(new){
            if(isFinal)
                error("Can't set a final variable")
            field = new
        }
}

interface VariableStack{
    fun getAllVariables():List<Variable>
    fun getVariableForName(name :String):Variable?
    operator fun plus(other :VariableStack):VariableStack
}

interface MutableVariableStack :VariableStack{
    fun addVariable(variable :Variable)
    fun copy():VariableStack
}


class DualVariableStack(private val one :VariableStack, private val two: VariableStack):VariableStack{
    private val all by lazy { one.getAllVariables() + two.getAllVariables() }
    override fun getAllVariables(): List<Variable> {
        return all
    }

    override fun getVariableForName(name: String): Variable? {
        return one.getVariableForName(name) ?: two.getVariableForName(name)
    }

    override fun plus(other: VariableStack): VariableStack {
        return DualVariableStack(this,other)
    }
}

class SimpleVariableStack :MutableVariableStack {
    private val variables = mutableListOf<Variable>()

    override fun getAllVariables(): List<Variable> {
        return variables
    }

    override fun getVariableForName(name: String): Variable? {
        return variables.firstOrNull { it.name == name }
    }

    override fun plus(other: VariableStack): VariableStack {
        return DualVariableStack(this,other)
    }

    override fun addVariable(variable: Variable) {
        variables.add(variable)
    }

    override fun copy(): VariableStack {
        val s = SimpleVariableStack()
        s.variables.addAll(variables)
        return s
    }
}