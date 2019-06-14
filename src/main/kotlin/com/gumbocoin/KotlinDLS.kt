package com.gumbocoin


class KotlinFunction(
    name: String,
    returnType: Type,
    arguments: List<Argument>,
    val executor :(List<Value>) -> ReturnValue) : GFunction(name,returnType,arguments, Line.kotlin(),isKotlin = true)


fun startNamespace(block :NamespaceBuilder.() -> Unit):Lazy<Namespace>{
    return lazy { NamespaceBuilder().apply(block).build() }
}

class NamespaceBuilder{
    private val functions  = mutableListOf<KotlinFunction>()
    private val subs = mutableListOf<Namespace>()
    var name :String? = null

    fun function(block :KotlinFunctionBuilder.() -> Unit){
        functions.add(KotlinFunctionBuilder().apply(block).build())
    }
    fun sub(block :NamespaceBuilder.() -> Unit){
        subs.add(NamespaceBuilder().apply(block).build())
    }
    fun sub(namespace :Namespace){
        subs.add(namespace)
    }

    fun build():Namespace{
        return Namespace(
            name = name ?: error("Name not defined"),
            subs = subs,
            functions = functions
        )
    }
}



class ArgumentBuilder{
    var name :String? = null
    var type :Type? = null
    fun build(nameProducer :() -> String):Argument{
        return Argument(
            name = name ?: nameProducer(),
            type = type ?: error("Type not defined")
        )
    }
}


class KotlinFunctionBuilder{

    var name :String? = null
    var type :Type? = null

    private val arguments = mutableListOf<Argument>()

    private var i = 0

    fun argument(block :ArgumentBuilder.() -> Unit){
        arguments.add(ArgumentBuilder().apply(block).build { "var${i++}" })
    }

    private var executor :((List<Value>) -> ReturnValue)? = null

    fun execute0(block :(List<Value>) -> ReturnValue){
        executor = block
    }
    fun execute1(block :(List<Value>) -> Value){
        executor = { ReturnValue(block.invoke(it)) }
    }
    fun execute2(block :(Value) -> Value){
        executor = { ReturnValue(block.invoke(it[0])) }
    }
    fun execute3(block :() -> Value){
        executor = { ReturnValue(block.invoke()) }
    }
    inline fun <reified A :Any> execute4(crossinline block :(A) -> A){
        execute(block)
    }
    inline fun <reified A :Any> execute5(crossinline block :(A) -> Unit){
        execute0 {
            block(it[0].value as A)
            ReturnValue.void()
        }
    }
    fun execute(block :() -> Unit){
        execute0 {
            block()
            ReturnValue.void()
        }
    }


    inline fun <reified A,reified R :Any> execute(crossinline block :(A) -> R){
        execute0 {
            @Suppress("UNCHECKED_CAST") val a = it[0].value as A
            val b = block(a)
            ReturnValue(Value(typeOf<R>(),b))
        }
    }
    inline fun <reified A,reified B,reified R:Any> execute(crossinline block:(A,B) -> R){
        execute0 {

            @Suppress("UNCHECKED_CAST") val a = it[0].value as A
            @Suppress("UNCHECKED_CAST") val b = it[1].value as B
            val r= block(a,b)
            ReturnValue(Value(typeOf<R>(),r))
        }
    }

    inline fun <reified A,reified B,reified C,reified R:Any> execute(crossinline block:(A,B,C) -> R){
        execute0 {

            @Suppress("UNCHECKED_CAST") val a = it[0].value as A
            @Suppress("UNCHECKED_CAST") val b = it[1].value as B
            @Suppress("UNCHECKED_CAST") val c = it[1].value as C
            val r= block(a,b,c)
            ReturnValue(Value(typeOf<R>(),r))
        }
    }




    fun build():KotlinFunction{
        return KotlinFunction(
            name = name ?: error("Name not defined"),
            returnType = type ?: error("Type not defined"),
            arguments = arguments,
            executor = executor ?: error("Executor not defined")
        )
    }
}