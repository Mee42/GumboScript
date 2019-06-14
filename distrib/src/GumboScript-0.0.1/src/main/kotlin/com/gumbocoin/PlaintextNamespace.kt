package com.gumbocoin


class PlaintextNamespace(val name :String, val subs :List<PlaintextNamespace>, val lines :List<Line>)


fun initialNamespaceParse(lines :List<Line>, name :String) :PlaintextNamespace{

    val otherLines = mutableListOf<Line>()

    val namespaces = mutableListOf<PlaintextNamespace>()

    var index = 0
    var startIndex = -1
    var currentNamespace: String? = null


    while(index < lines.size){

        if(currentNamespace == null && lines[index].content.startsWith(">>")){
            val namespaceName = lines[index].content.replaceFirst(">>","").trim()
            checkValidIdentifier(namespaceName,lines[index])
            if(namespaces.any { it.name == namespaceName })
                compileError("Namespace $namespaceName can not be duplicated (line ${lines[index].lineNumber})")

            startIndex = index
            currentNamespace = namespaceName
        }else if(currentNamespace == null){
            otherLines.add(lines[index])
        }

        if(currentNamespace != null && lines[index].content.startsWith("<<")){
            val namespaceName = lines[index].content.replaceFirst("<<","").trim()
            if(namespaceName == currentNamespace){
                val sub = lines.subList(startIndex + 1,index)
                namespaces.add(initialNamespaceParse(sub,namespaceName))
                currentNamespace = null
            }
        }


        index++
    }

    if(currentNamespace != null){
        compileError("Reached end of namespace $name while looking the end of namespace $currentNamespace")
    }


    return PlaintextNamespace(name,namespaces,otherLines)
}
