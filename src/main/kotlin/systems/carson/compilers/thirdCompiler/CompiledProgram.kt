package systems.carson.compilers.thirdCompiler


class CompiledProgram(private val lines :List<Line>){

    val vars = mutableMapOf<String,Variable>()

    var inter = RealInterface(this)
    class RealInterface(private val c :CompiledProgram) :RuntimeInterface{
        override fun getVariable(name: String): Variable? = c.vars[name]
    }

    fun run() :Int{

        lines.forEach {
            val statement = it.statement
            when(statement){
                is UnknownStatement, is BlankStatement -> {}
                is PrintStatement -> println(statement.expression.get().value)
                is VariableAssigmentStatement-> {
                    val name = statement.name

                    val existingValue = vars[name]

                    if(existingValue == null){
                        //add value in
                        val newVariable = Variable(name = name,value = statement.expression.get())
                        vars[name] = newVariable
                    }else{
                        error("Variable $name has already been declared")
                    }
                }
                is VariableReassignStatement -> {
                    val name = statement.name
                    val existingValue = vars[name] ?: error("Variable $name does not exist")
                    if(existingValue.type != statement.expression.type){
                        error("Type of expression ${statement.expression.type.name} does not match type of $name; ${existingValue.type}")
                    }
                    val newValue = statement.expression.get()
                    existingValue.value = newValue
                }

            }
        }
        return 0
    }
}