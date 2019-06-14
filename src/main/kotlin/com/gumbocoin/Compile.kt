package com.gumbocoin


fun finalCompileNamespace(partialNamespace: PartialNamespace,
                          otherNamespaces: List<PartialNamespace>):Namespace{
    return Namespace(
        name = partialNamespace.name,
        functions = partialNamespace.functions.map { compileFunction(it,partialNamespace,otherNamespaces) },
        subs = partialNamespace.subs.map { finalCompileNamespace(it,otherNamespaces) }
    )
}

open class CompiledLine(val line :Line,val desc :String)

class CompiledDebugLine(line :Line):CompiledLine(line,"A debug statement")

class VariableDecelerationLine(line :Line,
                               val name: String,
                               val type: Type,
                               val isFinal :Boolean,
                               val expression :Expression) :CompiledLine(line, "Variable deceleration")
class VariableReassignmentLine(line :Line,
                               val name: String, val expression: Expression): CompiledLine(line, "Variable reassignment")
class IfStatement(line :Line,
                  val expression: Expression,
                  val block :CompiledBlock) :CompiledLine(line, "If statement")

class WhileStatement(line :Line,
                  val expression: Expression,
                  val block :CompiledBlock) :CompiledLine(line, "While statement")

class FunctionCallLine(line :Line,
                       val expressions :List<Expression>,//ORDER DEPENDENT
                       val function :PlaintextFunction):CompiledLine(line, "Function call")
class ReturnLine(line :Line,
                 val expression :Expression) :CompiledLine(line,"Return statement")

class ErrorLine(line :Line,val message :String) :CompiledLine(line,"User-defined error to be thrown")

class CompiledBlock(val lines :List<CompiledLine>)

class CompileVariable(val name :String, val type :Type, val isFinal :Boolean)

private fun compileFunction(plaintextFunction: PlaintextFunction,
                            parent :PartialNamespace,
                            otherNamespaces: List<PartialNamespace>):GFunction{

    val variableStack = plaintextFunction.arguments.map { CompileVariable(it.name,it.type,isFinal = true) }

    val function = GumboFunction(
        name = plaintextFunction.name,
        returnType = plaintextFunction.returnType,
        arguments = plaintextFunction.arguments,
        lines = compileBlock(plaintextFunction.lines,variableStack,otherNamespaces,plaintextFunction.parent
            ?: error("Unable to get parent namespace when compiling function ${plaintextFunction.name}")),
        startingLine = plaintextFunction.startingLine)
    function.parent = parent
    return function
}



private fun compileBlock(block :List<Line>,
                         variableStack :List<CompileVariable>,
                         allNamespaces: List<PartialNamespace>,
                         currentNamespace :PartialNamespace):CompiledBlock{
//    println("Compiling block:$block")
    val compiledLines = mutableListOf<CompiledLine>()

    val local = mutableListOf<CompileVariable>()

    var index = 0

    while(index < block.size) {
        val line = block[index]
        val content = line.content
        val number = line.lineNumber
        val split = content.split(RegexConst.WHITESPACE).map { it.trim() }
        println("Compiling line $number, \"$content\"")
        val combinedStack = variableStack + local
        if (content == "debug") {
            compiledLines.add(CompiledDebugLine(line))
            index++
        }
        else if(content.startsWith("error")){
            val message = content.replaceFirst("return", "").trim()
            compiledLines.add(ErrorLine(line, message))
            index++
        }
        else if (content.startsWith("return")) {
            //return [Expression]
            val expression = content.replaceFirst("return", "").trim()
            val compiledExpression = compileExpression(
                expression = expression,
                variables = (local + variableStack),
                line = line,
                allNamespaces = allNamespaces,
                currentNamespace = currentNamespace)
            compiledLines.add(ReturnLine(line, compiledExpression))
            index++
        }
        else if(split.size >= 3 && split[2] == "="){
        //variable init
        // val isFinal = false
        //  0   1      2   3

        if(split[0] != "var" && split[0] != "val"){
            compileError("variable decleration on line $number does not start with var or val ($content)")
        }
        val isFinal = split[0] == "val"

        val name = split[1]
        checkValidIdentifier(name,line)
        if((variableStack + local).any { it.name == name }){
            compileError("Can't redeclare variable $name on line $number")
        }
        val expression = content.split(regex = RegexConst.WHITESPACE, limit = 4)[3]
        val compiledExpression = compileExpression(expression,combinedStack,line,currentNamespace,allNamespaces)
        val type = compiledExpression.returnType
        compiledLines.add(VariableDecelerationLine(line,name,type,isFinal,compiledExpression))
        local.add(CompileVariable(name,type,isFinal))
        index++
    }
        else if(split.size >= 2 && split[1] == "=") {
        //variable reassign
        //  isFinal = false
        //     0    1   2
        val name = split[0]
        val existing = combinedStack.firstOrNull { it.name == name }
            ?: compileError("Can't find variable $name on line $number - is it declared?")

        val expression = content.split(RegexConst.WHITESPACE, limit = 3)[2]
        val compiledExpression = compileExpression(expression, combinedStack, line,currentNamespace,allNamespaces)
        if (compiledExpression.returnType != existing.type) {
            compileError("Can't assign value of type ${compiledExpression.returnType} to variable $name, which is on type ${existing.type} (line $number)")
        }
        if (existing.isFinal) {
            compileError("Can not reassign a final variable $name. This may be a function parameter. Line $number")
        }
        compiledLines.add(VariableReassignmentLine(line, name, compiledExpression))
        index++
    }
        else if(content.startsWith("if")) {

        val startExpression = content.indexOf("(")
        val endExpression = findEnd(content.substring(startExpression + 1), '(', ')', line, true)
        val expression = content.substring(startExpression + 1).substring(0, endExpression)
        val compiledExpression = compileExpression(expression, combinedStack, line,currentNamespace,allNamespaces)
        if (compiledExpression.returnType != Type.boolean()) {
            compileError("Expression inside of if statement does not return a boolean \"$expression\" on line $number")
        }
        //find the ending bracket
        val closing = findClosingBracket(
            startingLine = index,
            lines = block
        ) { "Can't find closing } for if statement starting on line $number" }
        val blockLines = block.subList(index + 1, closing)
        val compiledBlockLines = compileBlock(blockLines, combinedStack, allNamespaces,currentNamespace)
        compiledLines.add(IfStatement(line, compiledExpression, compiledBlockLines))
        index = closing + 1
    }
        else if(content.startsWith("while")) {
        val startExpression = content.indexOf("(")
        val endExpression = findEnd(content.substring(startExpression + 1), '(', ')', line, true)
        val expression = content.substring(startExpression + 1).substring(0, endExpression)
        val compiledExpression = compileExpression(expression, combinedStack, line,currentNamespace,allNamespaces)
        if (compiledExpression.returnType != Type.boolean()) {
            compileError("Expression inside of while statement does not return a boolean \"$expression\" on line $number")
        }
        //find the ending bracket
        val closing = findClosingBracket(
            startingLine = index,
            lines = block
        ) { "Can't find closing } for while statement starting on line $number" }
        val blockLines = block.subList(index + 1, closing)
        val compiledBlockLines = compileBlock(blockLines, combinedStack, allNamespaces,currentNamespace)
        compiledLines.add(WhileStatement(line, compiledExpression, compiledBlockLines))
        index = closing + 1
    }
        else if(content.contains("(")){
            //it's a function call, definitely
            //  namespace:subs:subs:subs:functionName(arg1,arg2)

            val function = getFunctionForName(
                fullName = content.substring(0,content.indexOf("(")),
                default = currentNamespace,
                allHeads = allNamespaces,
                error = { "Can't find function ${content.substring(0,content.indexOf("("))} on line $number" })

            val startIndex = content.indexOf("(")
            val notIncludingStart = content.substring(startIndex + 1)
            val endIndex = findEnd(notIncludingStart,'(',')',line,true)
            val argumentList = notIncludingStart.substring(0,endIndex)
            val arguments = mutableListOf<String>()
            println("ArgumentList:\"$argumentList\"")
            run {
                var start = 0
                var i = 0
                var depth = 0
                var inString = false
                while(i < argumentList.length){
                    val char = argumentList[i]
                    when {
                        !inString &&
                                char == ',' &&
                                depth == 0 -> {
                            arguments.add(argumentList.substring(start,i))
                            start = i + 1
                        }
                        !inString &&
                                char == '(' -> depth++
                        !inString &&
                                char == ')' -> depth--
                        char == '"' -> inString = !inString
                    }
                    i++
                }
                val extra = argumentList.substring(start)
                if(extra.isNotBlank())
                    arguments.add(extra)
            }
            val expressions = arguments.map { compileExpression(
                expression = it,
                variables = (variableStack + local),
                line = line,
                allNamespaces = allNamespaces,
                currentNamespace = currentNamespace) }
            if(expressions.size != function.arguments.size){
                compileError("Function call to $function on line $number has an incorrect number of arguments\n" +
                        "expecting ${function.arguments.size}, but got ${expressions.size}")
            }
            for(i in 0 until expressions.size){
                val type0 = expressions[i].returnType
                val type1 = function.arguments[i].type
                if(type0 != type1){
                    compileError("Function call to $function on line $number has an incorrect type for argument #$i\n" +
                            "Expected $type1 but got $type0")
                }
            }
            compiledLines.add(FunctionCallLine(
                line = line,
                expressions = expressions,
                function = function))
            index++
        }
        else {
            compileError("I don't know how to compile line $number")
        }
    }
    return CompiledBlock(compiledLines)
}


fun getFunctionForName(fullName :String,
                       default :PartialNamespace,
                       allHeads :List<PartialNamespace>,
                       error :() -> String):PlaintextFunction {

    val lastColon = fullName.lastIndexOf(":")

    val name = fullName.substring(if(lastColon == -1) 0 else (lastColon + 1))
    val namespaces = if(lastColon == -1) "" else fullName.substring(0,fullName.lastIndexOf(":"))

    val ce = { compileError(error()) }

    if(namespaces.isEmpty()){
        var head: PartialNamespace? = default
        while(head != null) {
            head.functions.firstOrNull { it.name == name }?.let { return it }
            head = head.parent
        }
        compileError(error())
    }


    //all the namespaces in order
    val split = namespaces.split(":")
    //if split[0] is a top-level namespace
    if(allHeads.any { it.name == split[0] }){
        //then grab it from the base as the root
        var base = allHeads.first { it.name == split[0] }
        for(i in 1 until split.size){
            base = base.subs.firstOrNull { it.name == split[i] } ?: ce()
        }
        return base.functions.firstOrNull { it.name == name } ?: ce()
    }
    //alright, start from the `default` and work through it's parents trying to find something that works
    var root: PartialNamespace? = default
    while(root != null){
        //attempt to climb up the namespace tree
        //if this fails, then try the next tree
        run {
            var base: PartialNamespace = root ?: error("run{} violated contract, or I screwed up")
            for (i in 0 until split.size) {
                base = base.subs.firstOrNull { it.name == split[i] } ?: return@run
            }
            return base.functions.firstOrNull { it.name == name } ?: return@run
        }
        root = root.parent
    }
    ce()
}
