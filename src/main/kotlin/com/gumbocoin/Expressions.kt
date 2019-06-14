package com.gumbocoin

import java.lang.NumberFormatException


open class Value(val type :Type, val value :Any)

open class Expression(val returnType :Type)

class ValueExpression(val value :Value) :Expression(value.type)

class VariableExpression(val name :String, type :Type):Expression(type){
    constructor(other :CompileVariable) :this(other.name,other.type)
}

class FunctionCallExpression(val expressions :List<Expression>,//ORDER DEPENDENT
                             val function :PlaintextFunction) :Expression(function.returnType)
class CondenserExpression(
    val arguments :List<Expression>,
    val condenser :String,
    returnType :Type) :Expression(returnType)

fun compileExpression(expression :String,
                              variables :List<CompileVariable>,
                              line :Line,
                      currentNamespace: PartialNamespace,
                      allNamespaces: List<PartialNamespace>):Expression{

    val trimmed = expression.trim()

    val maybe = when {
        trimmed == "false" -> ValueExpression(Value(Type.of("boolean"),false))
        trimmed == "true" -> ValueExpression(Value(Type.of("boolean"),true))
        variables.any { it.name == trimmed } -> VariableExpression(variables.first { it.name == trimmed })
        else -> null
    }
    if(maybe != null){
        return maybe
    }
    val tokens = condenseTokenize(tokenize(trimmed, variables, line,currentNamespace,allNamespaces))
    if(tokens.isEmpty()){
        compileError("Expecting token on line ${line.lineNumber}: \"$trimmed\"")
    }
    if(tokens.size != 1){

        error("Received multiple tokens on line ${line.lineNumber}- $tokens")
    }
    if(tokens[0].isString()){
        error("Final token is str: ${tokens[0].getString()}")
    }
    return tokens[0].getExpression()
}

open class Token private constructor(private val str :String?,private val expression :Expression?){
    constructor(str :String):this(str,null)
    constructor(value :Value):this(null,ValueExpression(value))
    constructor(expression: Expression):this(null,expression)

    fun isString():Boolean = str != null
    fun isExpression():Boolean = expression != null
    fun getString():String = str!!
    fun getExpression():Expression = expression!!
    override fun toString(): String {
        return if(isString())
                "Token(str=$str)"
                else
                "Token(expression=$expression)"
    }

}


const val DIGITS_SET = "0123456789"
const val NEGATIVE_SIGN = '-'
const val DECIMAL_POINT = '.'


//TODO do ()s
private fun tokenize(e: String,
                     variables: List<CompileVariable>,
                     line: Line,
                     currentNamespace :PartialNamespace,
                     allNamespaces :List<PartialNamespace>) :List<Token>{

    verbose("tokenizing \"$e\"")
    val tokens = mutableListOf<Token>()
    var i = 0
    indexLoop@ while(i < e.length){
        val char = e[i]
        when {
            char == ' ' -> i++

            char == '"' -> {
                val next = e.indexOf('"',i + 1)
                if(next == -1)
                    compileError("No closing \" on line ${line.lineNumber}")
                val substring = e.substring(i + 1,next)
                tokens.add(Token(Value(Type.string(),substring)))
                i = next + 1
            }

            i + 4 <= e.length &&
                    e.substring(i,i + 4) == "true" -> {
                tokens.add(Token(Value(Type.boolean(),true)))
                i+=4
            }
            i + 5 <= e.length &&
                    e.substring(i,i + 5) == "false" -> {
                tokens.add(Token(Value(Type.boolean(),false)))
                i+=5
            }
            DIGITS_SET.contains("" + char) ||
                    ((DIGITS_SET + NEGATIVE_SIGN).contains("" + char) &&
                            i + 1 < e.length && DIGITS_SET.contains(e[i + 1])) -> {

                var end = i + 1
                while(end < e.length && (DIGITS_SET + DECIMAL_POINT).contains("" + e[end])) end++

                val substring = e.substring(i,end)
                val token = try{
                    Token(Value(Type.int(),Integer.parseInt(substring)))
                }catch(e :NumberFormatException){
                    try{
                        Token(Value(Type.double(),java.lang.Double.parseDouble(substring)))
                    }catch(e :NumberFormatException){
                        compileError("Error with number \"$substring\"")
                    }
                }
                tokens.add(token)
                i = end
            }
            "+-/*%".contains(char) -> {
                tokens.add(Token(char.toString()))
                i++
            }
            i + 2 <= e.length &&
                    e.substring(i,i + 2) == "==" -> {
                tokens.add(Token("=="))
                i += 2
            }
            i + 2 <= e.length &&
                    e.substring(i,i + 2) == "!=" -> {
                tokens.add(Token("!="))
                i += 2
            }
            i + 2 <= e.length &&
                    e.substring(i,i + 2) == "&&" -> {
                tokens.add(Token("&&"))
                i += 2
            }
            i + 2 <= e.length &&
                    e.substring(i,i + 2) == "||" -> {
                tokens.add(Token("||"))
                i += 2
            }

            i + 2 <= e.length &&
                    e.substring(i,i + 2) == ">=" -> {
                tokens.add(Token(">="))
                i += 2
            }
            i + 2 <= e.length &&
                    e.substring(i,i + 2) == "<=" -> {
                tokens.add(Token("<="))
                i += 2
            }
            char == '>' -> {
                tokens.add(Token(">"))
                i ++
            }
            char == '<' -> {
                tokens.add(Token("<"))
                i ++
            }
            char == '!' -> {
                tokens.add(Token("!"))
                i++
            }




            //find the first *word*. *word*s are sets of alphanumeric characters, starting with a letter.
            (LOWERCASE + UPPERCASE).contains("" + char) -> {
                var endIndex0 = i
                while(endIndex0 < e.length && (LOWERCASE + UPPERCASE + NUMBERS + EXTRA).contains(e[endIndex0])) endIndex0++
                val word = e.substring(i,endIndex0)
                if(endIndex0 == e.length){
                    //there's on other chars, it must be a variable
                    val variable = variables.firstOrNull { it.name == word } ?: compileError("Can't find variable $word on line ${line.lineNumber}")
                    tokens.add(Token(VariableExpression(variable)))
                    i = endIndex0
                    continue@indexLoop
                }
                verbose("i=$i")
                val s = if(e.substring(i).contains(" ")) e.substring(i).substring(0,e.substring(i).indexOf(" ")) else e.substring(i)
                verbose("s=$s")
                if(s.contains("(")){
                    //it's a function call....must be?
                    verbose("Tokens:$tokens")
                    verbose("e.substring($i,${e.indexOf(char = '(',startIndex = i)}) = ${e.substring(i,e.indexOf(char = '(',startIndex = i))}")
                    val functionName = e.substring(i,e.indexOf(char = '(',startIndex = i))

                    val function = getFunctionForName(
                        fullName = functionName,
                        default = currentNamespace,
                        allHeads = allNamespaces,
                        error = { "Can't find function $functionName on line ${line.lineNumber}" })


                    val startIndex = e.indexOf(char = '(',startIndex = i)
                    val notIncludingStart = e.substring(startIndex + 1)
                    val endIndex = findEnd(notIncludingStart,'(',')',line,true)
                    val actualEndIndex = endIndex + (startIndex + 1)//for later
                    val argumentList = notIncludingStart.substring(0,endIndex)
                    val arguments = mutableListOf<String>()
                    verbose("ArgumentList:\"$argumentList\"")
                    run {
                        var start = 0
                        var ii = 0
                        var depth = 0
                        var inString = false
                        while(ii < argumentList.length){
                            val chary = argumentList[ii]
                            when {
                                !inString &&
                                        chary == ',' &&
                                        depth == 0 -> {
                                    arguments.add(argumentList.substring(start,ii))
                                    start = ii + 1
                                }
                                !inString &&
                                        chary == '(' -> depth++
                                !inString &&
                                        chary == ')' -> depth--
                                chary == '"' -> inString = !inString
                            }
                            ii++
                        }
                        val extra = argumentList.substring(start)
                        if(extra.isNotBlank())
                            arguments.add(extra)
                    }
                    val expressions = arguments.map { compileExpression(
                        expression = it,
                        variables = (variables),
                        line = line,
                        currentNamespace = currentNamespace,
                        allNamespaces = allNamespaces) }
                    if(expressions.size != function.arguments.size){
                        compileError("Function call to $function on line ${line.lineNumber} has an incorrect number of arguments\n" +
                                "expecting ${function.arguments.size}, but got ${expressions.size}")
                    }

                    for(index in 0 until expressions.size){
                        val type0 = expressions[index].returnType
                        val type1 = function.arguments[index].type
                        if(type0 != type1){
                            compileError("Function call to $function on line ${line.lineNumber} has an incorrect type for argument #$index\n" +
                                    "Expected $type1 but got $type0")
                        }
                    }
                    tokens.add(Token(FunctionCallExpression(
                        expressions = expressions,
                        function = function)))
                    i = actualEndIndex + 1
                    continue@indexLoop
                }
                else {
                    //it's def a variable
                    val variable = variables.firstOrNull { it.name == word } ?: compileError("Can't find variable $word on line ${line.lineNumber}")
                    tokens.add(Token(VariableExpression(variable)))
                    i = endIndex0
                    continue@indexLoop
                }
            }
        }
    }
    return tokens
}