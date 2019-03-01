package systems.carson.compilers.secondCompiler

import systems.carson.GsCompiler
import java.lang.IndexOutOfBoundsException
import java.util.*

class SecondCompiler : GsCompiler {
    override val version: String
        get() = "0.1.0"

    private val modules = listOf<Module>(
        BaseModule(this),
        BaseExtraModule(this),
        MathModule(this),
        DebugModule(this),
        VariableModule(this))
    private val globalModules = mutableListOf("base")

    private val commands :List<Command>
    get() = modules.flatMap { it.commands }

    lateinit var lines :List<Line>
    val stack = Stack<Int>()

    val loops = mutableListOf<Loop>()
    class Loop(val startingLine:Int,val endingLine :Int, val name :String)

    private var currentLine = 0




    val lineInfo :String
        get() = "at line ${lines[currentLine].lineNumber} (${lines[currentLine].content})"

    override fun run(input: String, conf: Map<String, List<String>>): Int {
        var offset = 0
        conf.getOrDefault("include", emptyList()).forEach {
            val module = modules.firstOrNull { w -> w.name == it } ?: error("Can not find module $it")
            globalModules.add(module.name)//make sure it is all valid modules
            offset++
        }
        conf.getOrDefault("exclude", emptyList()).forEach {
            globalModules.removeIf { s -> s == it }//make sure it is all valid modules
            offset++

        }//in case....who needs global::


        lines = input.split("\n")
        .mapIndexed {a,b -> Pair(a,b) }
        .flatMap { it.second.split(";").map { s -> Pair(s,it.first) }}
        .map { pair ->
            Line(
                content = pair.first.trim(),
                lineNumber = pair.second + 1 + offset + 1
            )
        }
        //init loops
        for(i in 0 until lines.size){
            if(lines[i].content.startsWith("startloop")){
                val name     = lines[i].content.replaceFirst("startloop","").trim()
                var endingLine = i + 1
                try {
                    while (lines[endingLine].content.trim() != "stoploop $name") endingLine++
                }catch(e :IndexOutOfBoundsException){
                    error("Reached end of file while looking for \"stoploop $name\" $lineInfo")
                }
                loops.add(Loop(i,endingLine,name))
            }

        }



        while(true){
            if(currentLine >= lines.size){
                error("Reached end of the file while running\n\tMaybe include an exit statement?")
            }
            val line = lines[currentLine]
            var contentT = line.content
            if(contentT.isEmpty()){
                currentLine++
                continue
            }
            if(contentT.startsWith("//")){
                currentLine++
                continue
            }
//            println("    - processing line \"$content\" (${line.lineNumber})")
            //replaceable :POP

            if(contentT.contains("//")){
                contentT = contentT.substring(0,contentT.indexOf("//"))
            }
            while(contentT.contains(":POP")){
                contentT = contentT.replaceFirst(":POP",stack.pop().toString())
            }
            while(contentT.contains(":PEEK")){
                contentT = contentT.replaceFirst(":PEEK",stack.peek().toString())
            }
            while(contentT.contains(":SIZE")){
                contentT = contentT.replaceFirst(":SIZE","" + stack.size)
            }

            val content = contentT

            //keep this for the functional stuff
            when{
                //raw line content
                content == "exit" -> return 0
                content.startsWith("initfun") -> {
                    val name = content.replaceFirst("initfun","").trim()
                    var startLine = 0
                    try {
                        while (lines[startLine].content != "setfun $name") startLine++
                    }catch(e :IndexOutOfBoundsException){
                        error("Reached end of file while searching for function $name start")
                    }
                    var endLine = startLine + 1
                    while(lines[endLine].content != "stopfun")endLine++
                    functions.add(
                        Function(
                            lineStart = startLine,
                            name = name
                        )
                    )
                    //don't edit the current call
                }
                content.startsWith("setfun") -> {
                    val name = content.replaceFirst("setfun","").trim()
                    if(!functions.any { it.name == name }) {
                        val startLine = currentLine
                        var endLine = currentLine + 1
                        while (lines[endLine].content != "stopfun") endLine++
                        functions.add(
                            Function(
                                lineStart = startLine,
                                name = name
                            )
                        )
                        currentLine = endLine
                    }
                }
                content.startsWith("gotofun") -> {
                    val key = content.replaceFirst("gotofun","").trim()
                    val func = functions.firstOrNull { it.name == key }
                    if(func == null){
                        println("Can't find function $key")
                        return 1
                    }
                    functionStack.push(
                        FunctionCall(
                            func,
                            currentLine
                        )
                    )
                    if(functionStack.size == 65535){
                        error("Max function size reached of 65535")
                    }
                    currentLine = func.lineStart
                }
                content == "stopfun" -> {
                    if(functionStack.isEmpty()){
                        error("Unable to find a function to pop back from")
                    }
                    currentLine = functionStack.pop().calledFrom
                }


                content.startsWith("startloop") -> {
                    val name = content.replaceFirst("startloop","").trim()
                    val loop = loops.firstOrNull { it.name == name } ?: error("unable to find loop $name $lineInfo")
                    if(pop() > 0) {
                        currentLine = loop.endingLine
                    }
                }
                content.startsWith("stoploop") -> {
                    val name = content.replaceFirst("stoploop","").trim()
                    val loop = loops.firstOrNull { it.name == name } ?: error("unable to find loop $name $lineInfo")
                    currentLine = loop.startingLine - 1
                }
                content == "if" -> {
                    if(pop() != 0){
                        var t = currentLine
                        try {
                            while (lines[++t].content != "fi");
                        }catch(e :IndexOutOfBoundsException) {
                            error("Reached end of the line while searching for fi $lineInfo")
                        }
                        currentLine  = t
                    }
                }
                content == "fi" -> { }
                else -> {
                    val split = content.split(delimiters = *arrayOf(" "),ignoreCase = false,limit = 2)
                    val key = split[0]
                    val value = if(split.size == 2) split[1] else ""
                    var flag = false
                    for(command in commands){
                        var commandName = command.name
                        if(globalModules.contains(command.parent)) {
//                            println(commandName)
                            if(key == commandName){
                                command.runner(value)
                                flag = true
                                break
                            }
                        }
                        commandName =command.parent + "::" + command.name
//                        println(commandName)

                        if(key == commandName){
                            command.runner(value)
                            flag = true
                            break
                        }
                    }
                    if(!flag){
                        for(command in commands){
                            if(key == command.name){
                                error("unknown command $key $lineInfo\n\tDid you mean ${command.parent}::${command.name}?")
                            }
                        }
                        error("unknown commands $lineInfo")
                        //fun guesswork
                    }

                }
            }


            currentLine++
        }

    }
    fun pop():Int{
        if(stack.empty()){
            error("Can not pop empty stack $lineInfo")
        }
        return stack.pop()
    }
    fun peek():Int{
        if(stack.empty()){
            error("Can not peek empty stack $lineInfo")
        }
        return stack.peek()
    }

    fun push(i: Int) {
        stack.push(i)
    }
    fun int(s :String):Int{
        try{
            return s.toInt()
        }catch(e :NumberFormatException){
            error("Unable to parse int $s")
        }
    }

}


class Line(val content :String,val lineNumber :Int)

//functions

class Function(val lineStart :Int,val name :String)

class FunctionCall(val func : Function, val calledFrom :Int)

val functions = mutableListOf<Function>()
val functionStack = Stack<FunctionCall>()
