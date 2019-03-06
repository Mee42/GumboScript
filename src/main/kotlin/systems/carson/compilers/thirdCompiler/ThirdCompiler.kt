package systems.carson.compilers.thirdCompiler

import com.google.gson.GsonBuilder
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
}


public class Compiler(private val input :String, private val conf :Map<String,List<String>>) {


    //constants
    private val pattern = Pattern.compile("""^[\w]+[\s]+equals""")


    private lateinit var lines :List<Line>


    class DefaultInterface :RuntimeInterface{
        override fun getVariable(name: String) = error("Making call to runtimeInterface before runtime has started")
    }


    private var inter :RuntimeInterface = DefaultInterface()

    //data
    private val variables :MutableMap<String,Type> = mutableMapOf()
    private var compileLineInfo :String = "error"

    fun compile() :CompiledProgram{

        lines = input.split("\n").map { it.trim() }.mapIndexed { index, s ->
            compileLineInfo = "on line ${index + conf.size} ($s)"
            val statement = when{
                s.isBlank() -> BlankStatement()

                s == "print" -> PrintStatement()

                s.startsWith("print") -> {
                    PrintStatement(compileTotalExpression(s.replaceFirst("print","").trim()))
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
        val program = CompiledProgram(lines)
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
        val list = mutableListOf<Segment>()
        var cache = ""
        var on = 0
        fun popCache(){
            if(cache.isNotBlank()){
//                println("Adding $cache to the list")
                "".trim()
                list.add(Segment(xstring = cache.trim()))
                cache = ""
            }
        }
        while(on < s.length){
            //if it be the number
            when {
                s[on] == ':' &&
                        on + 1 < s.length &&
                        s[on + 1] == '[' -> {
                    popCache()
//                    println("variable")
                    val end = s.indexOf(']',on)
                    if(end == -1)error("Unable to find closing [ of :[ $compileLineInfo")
                    val name = s.substring(on + 2,end)
                    val type = variables[name] ?: error("Unknown variable $name")
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
                //positive numbers
                //for negative numbers, use (0 - 1)
                "0123456789".contains(s[on]) -> {
                    popCache()
                    var onn = on
                    //while the next one isn't a number. Note: consecutive integers work with this :thonk:

                    while (onn + 1 < s.length && !"0123456789".contains(s[onn])) onn++
                    onn++
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
//        println("Running segments $segments")
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

fun main() {
    for (i in 0..10){
        if(i % 2 == 0)
            continue
    }
}



enum class Matchers(vararg val conditionals: Conditional){
    MULTIPLICATION(TypeConditional("int"),
                    StringEqualConditional("*"),
                    TypeConditional("int")) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) {
                Value(
                        segments[0].expression.get().value as Int *
                              segments[2].expression.get().value as Int
                )
            }
        }
    },

    SUBTRACTION(TypeConditional("int"),
        StringEqualConditional("-"),
        TypeConditional("int")) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) { Value(
                segments[0].expression.get().value as Int -
                        segments[2].expression.get().value as Int
            ) }
        }
    },

    ADDITION(TypeConditional("int"),
                 StringEqualConditional("+"),
                 TypeConditional("int")) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) { Value(
                segments[0].expression.get().value as Int +
                        segments[2].expression.get().value as Int
            ) }
        }
    };


    abstract fun process(segments :List<Segment>):Expression

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

