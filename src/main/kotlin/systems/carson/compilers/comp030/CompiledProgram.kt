package systems.carson.compilers.comp030

import systems.carson.Logger


class CompiledProgram(val main :BracketedStatement) {

    private class ExitTheProgramException(val exitCode :Int):RuntimeException()

    fun execute() :Int{
        try{
            execute(main,mapOf())
        }catch(e :ExitTheProgramException){
            return e.exitCode
        }
        return 0
    }
    private fun execute(bracket :BracketedStatement, variables :Map<String,Value>){
        val localVariables = mutableMapOf<String,Value>()
        bracket.lines.forEach {
            VariableRetrialSystem.update = { variables + localVariables }
            when(it){
                is HelloStatement -> Logger.output("hello")
                is WorldStatement -> Logger.output("world")
                is PrintVarsStatement -> Logger.output("variables:" + (variables + localVariables))
                is InitializeVarStatement -> localVariables[it.name] = it.expression.get()
                is ReassignVarStatement -> {
                    val value = (localVariables + variables)[it.name] ?: error("Unknown variable \"${it.name}\" on line ${it.line} ")
                    value.value = it.expression.get().value
                }
                is BracketedStatement -> execute(it,variables + localVariables)
                is IfStatement -> {
                    if(it.expression.get().value as Boolean){
                        execute(it.block,variables + localVariables)
                    }
                }
                is WhileStatement -> {
                    while(it.expression.get().value as Boolean){
                        execute(it.block,variables + localVariables)
                    }
                }
                is EmptyStatement -> {}
                is ExitStatement -> throw ExitTheProgramException(it.expression.get().value as Int)
                is PrintStatement -> Logger.output(it.exp.get().value.toString())
                is WaitStatement -> Thread.sleep(1000)
                else -> error("Unknown statement $it")
            }
        }
    }
}