package systems.carson.compilers.thirdCompiler

import systems.carson.GsCompiler
import java.util.regex.Pattern

//val gson = GsonBuilder().setPrettyPrinting().create()!!

class ThirdCompiler :GsCompiler{
    override val version: String
        get() = "0.2.0"

    override fun run(input: String, conf: Map<String, List<String>>): Int {
        val program = Compiler(input,conf)
        val compiled = program.compile()
        return compiled.run()
    }
}


interface RuntimeInterface{
    fun getVariable(name :String) :Variable?
    fun getArray(name :String) :ArrayImpl?
    fun getStackTrace(): String
}


public class Compiler(private val input :String, private val conf :Map<String,List<String>>) {


    //constants
    private val pattern = Pattern.compile("""^[\w]+[\s]+equals""")


    private lateinit var lines :List<Line>


    class DefaultInterface :RuntimeInterface{
        override fun getVariable(name: String) = error("Making call to runtimeInterface before runtime has started")
        override fun getArray(name: String) = error("Making call to runtimeInterface before runtime has started")
        override fun getStackTrace() = error("Making call to runtimeInterface before runtime has started")
    }


    private var inter :RuntimeInterface = DefaultInterface()

    //data
    private val variables :MutableMap<String,Type> = mutableMapOf()
    private val arrays :MutableMap<String,Type> = mutableMapOf()

    private var compileLineInfo :String = "error"

    fun compile() :CompiledProgram{

        val gotos = mutableListOf<String>()

        lines = input.split("\n").map { it.trim() }.mapIndexed { index, s ->
            compileLineInfo = "on line ${index + conf.size + 1} ($s)"
            val statement:Statement = when{
                s.isBlank() -> BlankStatement()
                s.startsWith("//") -> BlankStatement()
                s == "print" -> PrintStatement()

                s.startsWith("print") -> {
                    PrintStatement(compileTotalExpression(s.replaceFirst("print","").trim()))
                }

                s.startsWith("varr") -> {
                    //varr name type
                    // 0    1    2
                    val split = s.split(Pattern.compile("""[\s]+"""),3)
                    val name = split[1]
                    val type = Type(split[2])
                    if(arrays[name] != null){
                        error("Array $name already defined")
                    }
                    arrays[name] = type
                    ArrayAssigmentStatement(name,type)
                }
                s.startsWith("::[") -> {
                    //set expression
                    //::[name:index] equals [Expression]
                    //01234567          1        2
                    if(!s.contains("equals"))error("array value assignment expression doesn't contain \"equals\" $compileLineInfo")
                    val totalName = s.substring(0,s.indexOf(" equals"))
                    var end = 3
                    var nested = 0
                    var running = true
                    while(running) {
                        when{
                            end >= totalName.length -> error("Reached the end of \"$totalName\" while looking for a ']' $compileLineInfo")
                            totalName[end] == ']' && nested == 0 -> running = false
                            totalName[end] == ']' -> nested--
                            totalName[end] == '[' -> nested++
                        }
                        if(running) end++
                    }
                    val nameTwo = totalName.substring(3,end)
                    val realName = nameTwo.split(regex = Pattern.quote(":").toRegex(),limit = 2)[0]
                    if(arrays[realName] == null) error("Can't find array \"$realName\"")
                    val indexx = compileTotalExpression(nameTwo.split(regex = Pattern.quote(":").toRegex(),limit = 2)[1])
                    if(indexx.type != Type("int")){
                        error("Type of index is not int; $compileLineInfo")
                    }
                    val exp = compileTotalExpression(s.substring(s.indexOf("equals") + "equals".length))
                    if(exp.type != arrays[realName]!!){
                        error("Attempting to set value of type ${exp.type} on array of type ${arrays[realName]}")
                    }
                    ArraySetValueStatement(realName,indexx,exp)
                }


                s.startsWith("var") -> {
                    //var name equals [Expression]
                    // 0   1     2      3
                    val split = s.split(Pattern.compile("""[\s]+"""),4)
                    val name = split[1]
                    val equals = split[2]
                    if(equals != "equals"){
                        error("Can not parse variable assigment statement\nsplit:$split")
                    }
                    if(variables[name] != null){
                        error("Variable $name already defined")
                    }
                    val expression = compileTotalExpression(split[3])
                    //add it to the compiler list
                    variables[name] = expression.type
                    VariableAssigmentStatement(name, expression)
                }



                s.startsWith("goto") -> {
                    //goto name [Expression]
                    // 0     1    2
                    val split = s.split(Pattern.compile("""[\s]+"""),3)
                    val name = split[1]
                    val expression = compileTotalExpression(split[2])
                    if(expression.type != Type("boolean"))
                        error("Goto expression not of type boolean $compileLineInfo")
                    GotoStatement(name,expression)
                }
                s.startsWith("setgoto") -> {
                    //setgoto name
                    // 0       1
                    val split = s.split(Pattern.compile("""[\s]+"""),2)
                    val name = split[1]
                    gotos.add(name)
                    GotoSetStatement(name)
                }

                s.startsWith("exit") -> {
                    //exit [Expression]
                    val split = s.split(Pattern.compile("""[\s]+"""),2)
                    val expression = compileTotalExpression(split[1])
                    ExitStatement(expression)
                }

                pattern.matcher(s).find() -> {
                    //name equals [Expression]
                    //  0    1       2
                    val split = s.split(Pattern.compile("""[\s]+"""),3)
                    val name = split[0]
                    if(split[1] != "equals"){
                        error("Can not parse variable assigment statement\nsplit:$split")
                    }
                    val expression = split[2]
                    variables[name] ?: error("Unknown variable $name")
                    VariableReassignStatement(name, compileTotalExpression(expression))
                }
                else -> error("Unknown statement $s")
            }
            Line(s,index + conf.size, statement)
        }
        val program = CompiledProgram(lines,gotos)
        inter = program.inter
        println("     ----  compiled  ----    ")
        return program
    }
    /** this is the outdated one */
    @Deprecated("Use compileExpression")
    private fun compileRawValue(s :String) :Expression {
        val i = s.toIntOrNull()
        if(i != null){
//            println(i::class.java.name)
            return Expression.forValue(i)
        }
        if(s.startsWith("\"") && s.endsWith("\"")){
            //if it's a string
            val str = s.substring(1,s.length - 1)
            return Expression.forValue(str)
        }
        if(s == "true")return Expression.forValue(true)
        if(s == "false")return Expression.forValue(false)
        if(s.startsWith(":[") && s.endsWith("]")){
            //variable notation
            //i love it
            val name = s.substring(2,s.length-1)
            val type = variables[name] ?: error("Unknown variable $name")
            return Expression(type = type) { (inter.getVariable(name) ?: error("Couldn't find variable $name")).value }
        }
        error("Can not parse expression $s")
    }

    /** string -> List<Segment> */
    private fun compileString(s :String): List<Segment> {
//        println("compiling: $s")
        val list = mutableListOf<Segment>()
        var cache = ""
        var on = 0
        fun popCache(){
            if(cache.isNotBlank()){
//                println("Adding $cache to the list")
                "".trim()
                cache.trim().split(Pattern.compile("""[\s]+"""))
                    .map { it.trim() }
                    .forEach { list.add(Segment(xstring = it)) }
                cache = ""
            }
        }
        while(on < s.length){
            //if it be the number
            when {
                s[on] == 't' &&
                        on + 3 < s.length &&
                        s[on + 1] == 'r' &&
                        s[on + 2] == 'u' &&
                        s[on + 3] == 'e' -> {
                    popCache()
                    val exp = Expression(type = Type("boolean")) { Value(type = Type("boolean"),value = true) }
                    list.add(Segment(xexpression = exp))
                    on += 5
                }
                s[on] == 'f' &&
                        on + 4 < s.length &&
                        s[on + 1] == 'a' &&
                        s[on + 2] == 'l' &&
                        s[on + 3] == 's' &&
                        s[on + 4] == 'e'-> {
                    popCache()
                    val exp = Expression(type = Type("boolean")) { Value(type = Type("boolean"),value = false) }
                    list.add(Segment(xexpression = exp))
                    on += 6
                }

                s[on] == ':' &&
                        on + 1 < s.length &&
                        s[on + 1] == '[' -> {
                    popCache()
//                    println("variable")
                    val end = s.indexOf(']',on)
                    if(end == -1)error("Unable to find closing [ of :[ $compileLineInfo")
                    val name = s.substring(on + 2,end)
                    val type = variables[name] ?: error("Unknown variable $name $compileLineInfo")
                    val exp = Expression(type = type) { (inter.getVariable(name) ?: error("Couldn't find variable $name")).value }
                    list.add(Segment(xexpression = exp))
                    on = end + 1
                    // bb:[as]bb
                    // 0123456789
                    //on = 2
                    //off = 6
                    //text = "as"
                    //yay
                }
                s[on] == ':' &&
                        on + 2 < s.length &&
                        s[on + 1] == ':' &&
                        s[on + 2] == '[' -> {
                    popCache()
                    var end = on + 3
                    var nested = 0
                    while(true){
                        if(end >= s.length)
                            error("Reached end of expression while looking for ending ) $compileLineInfo")
                        if(s[end] == ']'){
                            if(nested == 0){
                                break
                            }
                            nested--
                        }
                        if(s[end] == '['){
                            nested++
                        }
                        end++
                    }
                    if(end >= s.length)error("Unable to find closing [ of ::[ $compileLineInfo")
                    val internal = s.substring(on + 3,end)
                    val split = internal.split(delimiters = *arrayOf(":"),ignoreCase = false,limit = 2)
                    val name = split[0]
                    val index = compileTotalExpression(split[1])
                    val type = arrays[name] ?: error("Unknown array name $name")
                    val exp = Expression(type = type) {
                        val va = (inter.getArray(name) ?: error("Couldn't find array $name"))
                        try {
                            va.value[index.get().value as Int]
                        }catch(e :ArrayIndexOutOfBoundsException){
                            error("Index out of bound call ${inter.getStackTrace()}")
                        }
                    }
                    list.add(Segment(xexpression = exp))
                    on = end + 1
                }
                s[on] == '"' -> {
                    popCache()
                    val end = s.indexOf("\"",on + 1)
                    if(end == -1) error("Unable to find closing \" $compileLineInfo")
//                    println("s.substring($on + 1, $end)")
                    val content = s.substring(on + 1,end)
                    val exp = Expression.forValue(content)
                    list.add(Segment(xexpression = exp))
                    on = end + 1
                }
                //positive numbers
                //for negative numbers, use (0 - 1)
                "0123456789".contains(s[on]) -> {
                    popCache()
                    var onn = on
                    //while the next one isn't a number. Note: consecutive integers work with this :thonk:

                    while (onn < s.length && "0123456789".contains(s[onn])) onn++
//                    onn--
                    val int = s.substring(on, onn).toInt()
                    on = onn
//                    println("adding int $int to the list")
                    list.add(Segment(xexpression = Expression.forValue(int)))
                }
                s[on] == '(' -> {
                    popCache()
                    //collect the string until the ) is reached
                    var nested = 0
                    var end = on + 1
                    val builder = StringBuilder()
                    while(true){
                        if(end >= s.length)
                            error("Reached end of expression while looking for ending ) $compileLineInfo")
                        if(s[end] == ')'){
                            if(nested == 0){
                                break
                            }
                            nested--
                        }
                        if(s[end] == '('){
                            nested++
                        }
                        builder.append(s[end])
                        end++
                    }
                    list.add(Segment(xexpression = compileTotalExpression(builder.toString())))
                    on = end + 1

                }
                else -> cache += s[on++]
            }
        }
        popCache()
        return list
    }

    /** String -> Expression */
    private fun compileTotalExpression(s :String) :Expression {
        val x = compileToShorter(compileString(s))
        return when {
            x.isEmpty() -> error("Statement is empty $compileLineInfo")
            x.size != 1 -> error("Ended expression compile with more then one element $x $compileLineInfo")
            x[0].isString() -> error("Expression compiled but returned a string (not a Value(string)) $compileLineInfo" )
            else -> x[0].expression
        }
    }

    /** List<Segment> -> List<Segment> */
    private fun compileToShorter(segments :List<Segment>):List<Segment> {
        println("Running segments $segments")
        if(segments.isEmpty() || segments.size == 1) return segments

        for(matcher in Matchers.values()) {
//            println("testing matcher $matcher")
            val size = matcher.conditionals.size
            //only loop through a valid range.
            for (i in 0 until segments.size - size + 1) {
                var flag = true
                for(ii in 0 until size){
                    if(!matcher.conditionals[ii].isCorrect(segments[ii + i])){
                        flag = false
//                        println("matcher $matcher failed at segment $i")
                        break
                    }
                }
//                println("flag: $flag")

                if(flag){
//                    println("running matcher $matcher ${segments.subList(i,size + i)}")
                    val result = matcher.process(segments.subList(i,size + i))
                    return if(segments.size == size) {
//                        println("size equal")
                        listOf(Segment(xexpression = result))
                    }else {
//                        println("size not equal")
                        compileToShorter(
                            listOf(
                                *segments.subList(0,i).toTypedArray(),
                                Segment(xexpression = result),
                                *segments.subList(i + size, segments.size).toTypedArray()
                            )
                        )
                    }
                }
            }
        }

        error("Can't compile segments on line $compileLineInfo $segments")
    }


}


class Segment(private var xstring :String? = null,private val xexpression :Expression? = null){
    val string :String
        get() = xstring!!
    val expression :Expression
        get() = xexpression!!

    fun isString() = xstring != null
    fun isExpression() = xexpression != null
    override fun toString(): String {
        return if(xstring != null)
            "Segment(\"$xstring\")"
        else
            "Segment { $xexpression }"
    }

}


interface Conditional{
    fun isCorrect(seg : Segment) :Boolean
}
interface ExpressionConditional :Conditional{
    override fun isCorrect(seg: Segment): Boolean {
        if(!seg.isExpression())return false
        return isCorrect(seg.expression)
    }
    fun isCorrect(exp :Expression) :Boolean
}
interface RawStringConditional :Conditional{
    override fun isCorrect(seg: Segment): Boolean {
        if(!seg.isString())return false
        return isCorrect(seg.string)
    }
    fun isCorrect(str :String) :Boolean
}

class TypeConditional(private val type :Type) :ExpressionConditional{
    constructor(typeStr :String): this(Type(typeStr))
    override fun isCorrect(exp: Expression) = exp.type == type
}

class StringEqualConditional(val string :String, private val trim :Boolean = true) :RawStringConditional{
    override fun isCorrect(str: String): Boolean {
        return (if(trim) string.trim() else string) == str
    }
}

