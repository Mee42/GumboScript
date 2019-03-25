package systems.carson.compilers.comp030

import systems.carson.GsCompiler
import systems.carson.Logger
import java.util.regex.Pattern


val KEYWORDS = arrayOf("if","when","equals","var")





class RawTextStatement(val raw :String, val line :Int) :RawStatement{
    override fun toString(): String {
        return "RawStatement(raw=\"$raw\",line=$line)"
    }

    override fun compile(variables: Map<String, Type>): CompiledStatement {
        error("Unimplemented method on purpose - this should not be called.")
    }
}

interface RawStatement{
    fun compile(variables: Map<String, Type>) :CompiledStatement
}


class RawBracketedStatement(private val lines :List<RawStatement>) :RawStatement{
    override fun toString(): String {
        return "RawBracketedStatement(lines=$lines)"
    }

    override fun compile(variables :Map<String,Type>) :BracketedStatement{
        val newLines = mutableListOf(*lines.toTypedArray())

        val compiled = mutableListOf<CompiledStatement>()

        val localVariables = mutableMapOf<String,Type>()

        while(newLines.isNotEmpty()) {
            val item = newLines.removeAt(0)
            if(item is RawTextStatement &&
                newLines.size >= 1 &&
                newLines[0] is RawBracketedStatement){
                Logger.verbose("looking for if/while")
                val next = newLines[0] as RawBracketedStatement
                var continu = false
                when{
                    item.raw.startsWith("if") -> {
                        val exp = item.raw.substring(item.raw.trim().indexOf(" ")).trim()
                        val statement = IfStatement(item.line,compileExpression(exp,variables + localVariables,item.line),next.compile((variables + localVariables)))
                        compiled.add(statement)
                        newLines.removeAt(0)
                        continu = true
                        Logger.verbose("added if statement")
                    }
                    item.raw.startsWith("while") -> {
                        val exp = item.raw.substring(item.raw.trim().indexOf(" ")).trim()
                        val statement = WhileStatement(item.line,compileExpression(exp,variables + localVariables,item.line),next.compile((variables + localVariables)))
                        compiled.add(statement)
                        newLines.removeAt(0)
                        continu = true
                        Logger.verbose("added while statement")

                    }
                }
                if(continu) continue
            }
            if(item is RawTextStatement){
                val content = item.raw.trim()
                if(content.isBlank()){
                    compiled.add(EmptyStatement(item.line))
                    Logger.verbose("added empty statement")

                    continue
                }
                val maybe = when(content){
                    "hello" -> HelloStatement(item.line)
                    "world" -> WorldStatement(item.line)
                    "vars" -> PrintVarsStatement(item.line)
                    else -> null
                }
                if(maybe != null){
                    compiled.add(maybe)
                    Logger.verbose("added [hello/world/vars] statement")
                    continue
                }
                if(content.startsWith("exit")){
                    //exit expression
                    // 0    1
                    val split = content.split(Pattern.compile("""[\s]+"""),2)
                    val exp = compileExpression(split[1],variables + localVariables,item.line)
                    compiled.add(ExitStatement(item.line,exp))
                    Logger.verbose("added exit statement")
                    continue
                }
                if(content.startsWith("var")){
                    //var name equals expression
                    // 0    1    2         3
                    val split = content.split(Pattern.compile("""[\s]+"""),4)
                    val name = split[1]
                    val exp = compileExpression(split[3],variables + localVariables,item.line)
                    val type = exp.type
                    if(KEYWORDS.contains(name)) error("Variable name can not be a keyword. Line: ${item.line} ")
                    if(localVariables.containsKey(name) || variables.containsKey(name)) error("variable $name already declared in scope. Line: ${item.line} ")

                    compiled.add(InitializeVarStatement(item.line,name,exp))

                    //add to local variables
                    localVariables[name] = type
                    Logger.verbose("added var statement")
                    continue
                }
                if(content.startsWith("print")){
                    //print expression
                    //  0       1
                    val split = content.split(Pattern.compile("""[\s]+"""),2)
                    val exp = compileExpression(split[1],variables + localVariables,item.line)
                    compiled.add(PrintStatement(item.line,exp))
                    Logger.verbose("added print statement")
                    continue
                }
                if(content == "wait"){
                    compiled.add(WaitStatement(item.line))
                    Logger.verbose("added wait statement")
                    continue
                }

                if(content.contains(" ")) {
                    val firstWord = content.substring(0, content.indexOf(" "))
                    if (localVariables.containsKey(firstWord) || variables.containsKey(firstWord)) {
                        // name equals expression
                        //  0     1        2
                        val split = content.split(Pattern.compile("""[\s]+"""), 3)
                        if (split.size == 3) {
                            val equals = split[1]
                            if (equals == "equals") {
                                val name = split[0]
                                val existingType = localVariables.getOrElse(name) { variables.getOrDefault(name, null) }
                                    ?: error("Variable $name does not exist. Line: ${item.line} ")
                                val expression = compileExpression(split[2], localVariables + variables,item.line)
                                val newType = expression.type
                                if (existingType != newType)
                                    error("Attempting to reassign variable $name to a new type. Line: ${item.line} Existing type:$existingType, new type: $newType on line ")
                                compiled.add(ReassignVarStatement(item.line, name, expression))
                                Logger.verbose("added reassign statement")
                                continue
                            }
                        }
                    }
                }

                error("can't compile plaintext statement on line ${item.line} \"$content\" (newLines: $newLines)")
            }
            if(item is RawBracketedStatement){
                //if it's just a block
                //then just add the compiled block
                compiled.add(item.compile(localVariables + variables))
                Logger.verbose("added headless block")
                continue
            }

        }
        return BracketedStatement(compiled)
    }
}

class Compiler030 : GsCompiler {
    override val version: String = "0.3.0"


    fun compile(input :String, conf :Map<String,List<String>>): CompiledProgram{
        val rawLines = input.split("\n")
            .mapIndexed { index, s -> Pair(s,index + conf.size) }
            .flatMap { first -> first.first.split(";").map { Pair(it,first.second) } }
            .map { Pair(it.first.trim(),it.second) }
            .map { RawTextStatement(it.first,it.second) }

        val nestedLines = nest(rawLines)


        val mainMethod = RawBracketedStatement(nestedLines).compile(mapOf())

        return CompiledProgram(mainMethod)
    }

    override fun run(input: String, conf: Map<String, List<String>>): Int {


        //rawLines -> lines
        val program = compile(input,conf)
        Logger.normal("    --- compiled ----    ")
        return program.execute()
    }

    private fun nest(lines: List<RawTextStatement>): List<RawStatement> {
        Logger.verbose("nesting $lines")
        if(lines.isEmpty() || lines.size == 1)return lines
        var index = 0
        var nested = 0
        while(index < lines.size && lines[index].raw != "{"){
            index++
        }
        if(index == lines.size)
            return lines

        val startIndex = index
        index++
        while(index < lines.size){
            if(lines[index].raw == "{"){
                nested++
            }
            if(lines[index].raw == "}"){
                if(nested == 0){
                    return nest(lines.subList(0,startIndex)) +
                            RawBracketedStatement(nest(lines.subList(startIndex + 1,index))) +
                            nest(lines.subList(index + 1,lines.size))
                }else{
                    nested--
                }
            }
            index++
        }
        error("Unable to find closing {")
    }
}
