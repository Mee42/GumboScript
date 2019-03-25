package systems.carson.compilers.comp030


interface CompiledStatement
open class LinedCompiledStatement(val line :Int):CompiledStatement

class EmptyStatement(line :Int) :LinedCompiledStatement(line)
class HelloStatement(line :Int) :LinedCompiledStatement(line)
class WorldStatement(line :Int) :LinedCompiledStatement(line)
class PrintVarsStatement(line :Int) :LinedCompiledStatement(line)

class PrintStatement(line :Int,val exp :Expression) :LinedCompiledStatement(line)

class WaitStatement(line :Int):LinedCompiledStatement(line)

class InitializeVarStatement(line :Int,val name :String, val expression: Expression,val type :Type = expression.type) :LinedCompiledStatement(line)
class ReassignVarStatement(line :Int, val name :String, val expression :Expression, val type :Type = expression.type) :LinedCompiledStatement(line)
data class BracketedStatement(val lines :List<CompiledStatement>) :CompiledStatement {
    override fun toString() :String{
        return print("")
    }
    private fun print(tabs :String) :String{
        var b = ""
        lines.forEach {
            val str = when(it){
                is HelloStatement -> "Hello()"
                is WorldStatement -> "World()"
                is PrintVarsStatement -> "Variables()"
                is InitializeVarStatement -> "init ${it.name} ${it.type}"
                is ReassignVarStatement -> "assign ${it.name} ${it.type}"
                is BracketedStatement -> {
                    b+="$tabs{\n"
                    b+=it.print("$tabs  ")
                    b+="$tabs}\n"
                    null
                }
                is IfStatement -> {
                    b += "${tabs}If Statement {\n"
                    b += it.block.print("$tabs  ")
                    b+="$tabs}\n"
                    null
                }
                is WhileStatement -> {
                    b+="${tabs}While Statement {\n"
                    b+=it.block.print("$tabs  ")
                    b+="$tabs}\n"
                    null
                }
                is EmptyStatement -> null
                is ExitStatement -> "Exit()"
                is PrintStatement -> "Print()"
                is WaitStatement -> "Wait()"
                else -> error("Unknown statement $it")
            }
            str?.let { w -> b += "$tabs$w\n" }
        }
        return b
    }
}

class IfStatement(line :Int,val expression :Expression,val block :BracketedStatement) :LinedCompiledStatement(line)
class WhileStatement(line :Int,val expression :Expression,val block :BracketedStatement) :LinedCompiledStatement(line)

class ExitStatement(line :Int, val expression :Expression) :LinedCompiledStatement(line)
