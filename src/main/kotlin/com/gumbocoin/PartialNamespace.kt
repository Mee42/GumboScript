package com.gumbocoin

import java.util.regex.Pattern

object RegexConst{
    val WHITESPACE = Pattern.compile("""\s+""").toRegex()
}

class PartialNamespace(
    val name :String,
    val subs :List<PartialNamespace>,
    val functions :List<PlaintextFunction>){
    var parent :PartialNamespace? = null
}

class PlaintextFunction(
    val name: String,
    val returnType :Type,
    val arguments :List<Argument>,
    val lines :List<Line>,
    val startingLine :Line){

    var parent :PartialNamespace? = null
}

fun compileNamespace(namespace :PlaintextNamespace):PartialNamespace{
    var index = 0

    val functions = mutableListOf<PlaintextFunction>()

    while(index < namespace.lines.size){
        val line = namespace.lines[index]
        val content = line.content
        if(content.startsWith("fun")){
            //it's a function!
            val name = content
                .replaceFirst("fun","")
                .substring(0,content.indexOf("(") - "fun".length)
                .trim()
            checkValidIdentifier(name,line)

            val everythingAfter = content
                .replaceFirst("fun","")
                .replaceFirst(name,"")
                .trim()
                .substring(1)//remove the inital (

            val end = findEnd(everythingAfter,'(',')',line,considerStrings = true)
            val argumentList = parseArgumentList(everythingAfter.substring(0,end))


            val endStr = everythingAfter.substring(end + 1).trim()
            if(endStr.isBlank() || !endStr.contains('{')){
                compileError("Can't find starting bracket for function on line ${line.lineNumber}")
            }

            val type = if(endStr[0] == '{'){
                Type.default()
            }else{
                Type.of(endStr.substring(0,endStr.indexOf('{')).trim())
            }
            //get the last line
            val endingLine = findClosingBracket(index,namespace.lines)
                { "Reached end of namespace ${namespace.name} while looking for end of function $name" }
            verbose("Ending line:$endingLine")
            val lines = namespace.lines.subList(index + 1,endingLine)
            functions.add(PlaintextFunction(
                name = name,
                returnType = type,
                arguments = argumentList,
                lines = lines,
                startingLine = line))
            index = endingLine + 1
        }else{
            //it's a....fuck what else is there :thinking:
            //global variables?
            index++
        }

    }

    val retur =  PartialNamespace(name = namespace.name,
                            subs = namespace.subs.map { compileNamespace(it) },
                            functions = functions)

    retur.subs.forEach { it.parent = retur }
    retur.functions.forEach { it.parent = retur }
    return retur
}

/** The input should not include the () */
fun parseArgumentList(input :String):List<Argument>{
    if(input.isBlank())
        return emptyList()
    return input.split(",")
        .map { it.split(RegexConst.WHITESPACE) }
        .map { Argument(type = Type.of(it[0]),name = it[1]) }
}

fun findClosingBracket(startingLine :Int, lines :List<Line>, error :() -> String):Int{
    var endingLine = startingLine + 1
    var depth = 0
    loop@ while(endingLine < lines.size){
        var inString = false
        verbose("Testing ${lines[endingLine].content}")
        for(char in lines[endingLine].content){
            when {
                char == '"' -> inString = !inString
                !inString && char == '{' -> {
//                    println("depth++")
                    depth++
                }
                !inString && char == '}' && depth == 0 -> break@loop
                !inString && char == '}' && depth != 0 -> {
//                    println("depth--")
                    depth--
                }
                else -> {
//                    println("nothing")

                }
            }
        }
        endingLine++
    }
//    println("Depth:$depth")
    if(endingLine == lines.size){
        compileError(error())
    }
    return endingLine
}


fun findEnd(input :String,start :Char, end :Char, line :Line, considerStrings :Boolean):Int{
    var index = 0
    var depth = 0
    var inStr = false
    while(index < input.length){
        val char = input[index]
        when{
            !inStr && char == start -> depth++
            !inStr && char == end && depth > 0 -> depth--
            !inStr && char == end && depth == 0 -> {
                return index
            }
            char == '"' && considerStrings -> inStr = !inStr
        }
        index++
    }
    compileError("Reached end of \"$input\" while looking for $end on line $line")
}