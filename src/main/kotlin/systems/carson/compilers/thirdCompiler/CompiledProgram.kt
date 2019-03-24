package systems.carson.compilers.thirdCompiler


class CompiledProgram(private val lines :List<Line>,gotos :List<String>){

    val vars = mutableMapOf<String,Variable>()
    val arrays = mutableMapOf<String,ArrayImpl>()


    private val gotos = mutableMapOf<String,Int>()
    init {
        gotos.forEach { name ->
            this.gotos[name] = lines.mapIndexed { index, name2 -> Pair(index, name2) }
                .filter { it.second.statement is GotoSetStatement }
                .first { (it.second.statement as GotoSetStatement).name == name }.first
        }
    }

    lateinit var currentLineInfo :String

    var inter = RealInterface(this)
    class RealInterface(private val c :CompiledProgram) :RuntimeInterface{
        override fun getVariable(name: String): Variable? = c.vars[name]
        override fun getArray(name: String): ArrayImpl? = c.arrays[name]
        override fun getStackTrace(): String = c.currentLineInfo
    }

    fun run() :Int{

        var line = 0
        while(true){
            if(lines.size == line)error("Reached end of program while running")
            val statement = lines[line].statement
            currentLineInfo = "on line ${lines[line].lineNumber} (${lines[line].content})"
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
                is GotoStatement -> {
                    val name = statement.name
                    val result = statement.expression.get().value as Boolean
                    if(result){
                        line = gotos[name] ?: error("Can't find goto $name")
                    }
                }
                is ExitStatement -> {
                    return statement.exp.get().value as Int
                }
                is ArrayAssigmentStatement -> {
                    arrays[statement.name] = ArrayImpl(statement.name,mutableListOf(),statement.type)
                }
                is ArraySetValueStatement -> {
                    val value = arrays[statement.name]!!.value
                    val index = statement.index.get().value as Int
                    while(index >= value.size) value.add(Value(Any(),Type("void"),false))
                    value[index] = statement.value.get()
                }
            }
            line++
        }
    }
}