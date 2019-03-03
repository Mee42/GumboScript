package systems.carson.compilers.secondCompiler

import java.util.*


class BaseModule(private val compiler: SecondCompiler) :HelperModule(compiler,"base"){

    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
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

class IOModule(compiler: SecondCompiler) :HelperModule(compiler,"io"){

    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
        commands["print"] = { println(it) }

        commands["input"] = {
            val number = Scanner(System.`in`).nextLine()
            push(int(number))
        }
        commands["inputbool"] = {
            val number = Scanner(System.`in`).nextLine()
            push(boolToInt(number == "true"))
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
        commands["flip"] = {  push(boolToInt(!popBool()))  }
        commands["consume"] = {}
        commands["wait"] = {
            if(it.isBlank()){
                Thread.sleep(1000)
            }else{
                Thread.sleep(int(it).toLong())
            }
        }
        return commands
    }
}

class MathModule(compiler: SecondCompiler) :HelperModule(compiler,"math"){
    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
        commands["add"] = { push(pop() + pop()) }
        commands["mult"] = { push(pop() * pop()) }
        commands["sub"] =  { push(pop() - pop()) }
        commands["div"] =  { push(pop() / pop()) }

        commands["equalto"] = { push(boolToInt(pop() == pop())) }
        commands["lessthen"] = { push(boolToInt(pop() < pop())) }
        commands["greaterthen"] = { push(boolToInt(pop() > pop())) }
        commands["and"] = { push(boolToInt(popBool() and popBool())) }
        commands["or"] = { push(boolToInt(popBool() or popBool())) }
        commands["xor"] = { push(boolToInt(popBool() xor popBool())) }
        return commands
    }
}

class VariableModule(private var compiler: SecondCompiler) :HelperModule(compiler,"var"){
    private val vars = mutableMapOf<String,Int>()
    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
        //stack -> var
        commands["popvar"] = {
            val value = pop()
            vars[it] = value
            //call format var::popvar asdf
        }
        //var -> stack
        commands["pushvar"] = {
            if(!vars.containsKey(it)){
                error("Can not find variable $it when pushing var to stack ${compiler.lineInfo}")
            }
            push(vars[it]!!)
        }

        commands["delvar"] = {
            if(!vars.containsKey(it)){
                error("Can not find variable $it when trying to remove ${compiler.lineInfo}")
            }
        }

        commands["debug"] = { println(vars) }
        return commands
    }
}



/**
 * Debug modules should be pure functions and not edit the stack
 */
class DebugModule(private val compiler: SecondCompiler): HelperModule(compiler,"debug"){
    override fun init(commands: MutableMap<String, (String) -> Unit>): MutableMap<String, (String) -> Unit> {
        commands["stack"] = { println("stack:${compiler.stack}") }
        commands["asserttrue"] = {
            val peek = peek()
            val value = int(it)
            if(peek != value){
                error("assert true values different. got $peek, expected $value ${compiler.lineInfo}")
            }
        }
        commands["assertdone"] = {
            println("All tests passed successfully")
        }
        return commands
    }
}

