package com.gumbocoin

//this takes in the plaintext string and converts it to a list of lines


fun preprocess(s :String):List<Line>{
    val split = s.split("\n")
    val replaces = mutableMapOf<String,String>()
    var i = 0
    while(i < split.size){
        if(split[i].trim().startsWith("#define")){
            val subSplit = split[i]
                .trim()
                .replaceFirst("#define","")
                .trim()
                .split(regex = Regex.fromLiteral(" "),limit = 2)
            if(subSplit.size != 2){
                compileError("Can't parse #define statement on line $i")
            }
            replaces[subSplit[0]] = subSplit[1]
        }else{
            break
        }
        i++
    }

    return split.subList(i,split.size).map { it.replaceAll(replaces) }.mapIndexed { index, content -> Line(content.trim(),index + 1 + i) }
        .map {
            var inString = false
            var correct = -1
            for(index in 0 until it.content.length-1) {
                if (it.content[index] == '"'){
                    inString = !inString
            }else if(!inString &&
                    it.content[index] == '/' &&
                        it.content[index + 1] == '/'){
                    correct = index
                    break
                }
            }
            if(correct != -1)
                Line(content = it.content.substring(0,correct),
                    lineNumber = it.lineNumber)
            else
                Line(content = it.content,
                    lineNumber = it.lineNumber)
        }
        .filter { it.content.isNotEmpty() }

}

fun String.replaceAll(replaceMap :Map<String,String>):String{
    var str = this
    for((key,value) in replaceMap){
        str = str.replace(key,value)
    }
    return str
}