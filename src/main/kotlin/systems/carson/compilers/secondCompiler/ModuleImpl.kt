package systems.carson.compilers.secondCompiler



class BaseModule(private val compiler: SecondCompiler) :HelperModule(compiler,"base"){

    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
        commands["print"] = { println(it) }
        commands["wait"] = {
            if (it.isBlank()) {
                Thread.sleep(1000)
            } else {
                Thread.sleep(int(it).toLong())
            }
        }
        commands["push"] = {
            val number = int(it)
            push(number)
        }

        return commands
    }

}


class BaseExtraModule(private var compiler: SecondCompiler) :HelperModule(compiler,"extra"){
    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
        commands["pop"] = { pop() }
        commands["dupl"] = { push(peek()) }
        commands["swap"] = {
            val x = pop()
            val y = pop()
            push(x)
            push(y)
        }
        commands["consume"] = {}
        return commands
    }
}

class MathModule(compiler: SecondCompiler) :HelperModule(compiler,"math"){
    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
        commands["add"] = { push(pop() + pop()) }
        commands["mult"] = { push(pop() * pop()) }
        commands["sub"] =  { push(pop() - pop()) }
        commands["div"] =  { push(pop() / pop()) }
        return commands
    }
}

class DebugModule(private val compiler: SecondCompiler): HelperModule(compiler,"debug"){
    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
        commands["stack"] = { println("stack:${compiler.stack}") }
        commands["asserttrue"] = {
            val pop = pop()
            val value = int(it)
            if(pop != value){
                error("assert true values different. got $pop, expected $value ${compiler.lineInfo}")
            }
        }
        commands["assertdone"] = {
            println("All tests passed successfully")
        }
        return commands
    }
}

