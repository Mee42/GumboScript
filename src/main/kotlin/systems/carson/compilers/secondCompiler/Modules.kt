package systems.carson.compilers.secondCompiler

import java.lang.NumberFormatException


class Command(
    val parent :String,
    val name :String,
    val runner :(String) -> Unit)

@Suppress("LeakingThis")
abstract class Module(val name :String) {
    val commands :List<Command> = init(mutableMapOf()).map { Command(name,it.key,it.value) }
    abstract fun init(commands :MutableMap<String,(String) -> Unit>):MutableMap<String,(String) -> Unit>
}




abstract class HelperModule(private val compiler: SecondCompiler, name :String):Module(name) {

    fun pop():Int{
        if(compiler.stack.empty()){
            error("Can not pop empty stack ${compiler.lineInfo}")
        }
        return compiler.stack.pop()
    }
    fun peek():Int{
        if(compiler.stack.empty()){
            error("Can not peek empty stack ${compiler.lineInfo}")
        }
        return compiler.stack.peek()
    }

    fun push(i: Int) {
        compiler.stack.push(i)
    }
    fun int(s :String):Int{
        try{
            return s.toInt()
        }catch(e :NumberFormatException){
            error("Unable to parse int $s")
        }
    }
    fun boolToInt(s :Boolean):Int = if(s) 0 else 1
    fun intToBool(i :Int):Boolean = i == 0

    fun popBool():Boolean = intToBool(pop())
    fun peekBool():Boolean = intToBool(peek())


}

