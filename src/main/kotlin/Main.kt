import java.io.File
import compilers.*
import compilers.secondCompiler.SecondCompiler

val sourceCode = File("res/mc.gs")
val compilers = listOf({ FirstCompiler() },{ SecondCompiler() }, { MinecraftCompiler() })

fun main() {
    val input = sourceCode.readLines().toMutableList()
    val conf = mutableMapOf<String, MutableList<String>>()
    var i = -1//start a 0
    while (++i < input.size) {
        if (input[i].startsWith("#")) {
            val key = input[i].substring(1).split(delimiters = *listOf(" ").toTypedArray(), ignoreCase = false, limit = 2)[0]
            val value = input[i].substring(1).split(delimiters = *listOf(" ").toTypedArray(), ignoreCase = false, limit = 2)[1]
            val get = conf.getOrDefault(key,null)
            if(get == null)conf[key] = mutableListOf()
            conf[key]!!.add(value)
            input.removeAt(i--)
        } else {
            break
        }
    }

    ArrayList<String>()
    if(!conf.containsKey("compiler")){
        System.err.println("Can't find compiler version")
        System.exit(1)
    }
    val compiler = compilers.firstOrNull { it().version == conf["compiler"]!![0] }?.invoke()
    if(compiler == null){
        System.err.println("Can't find compiler for version ${conf["compiler"]}")
        val s = compilers.fold(StringBuilder()) { a,b -> a.append(b().version).append(",") }.removeSuffix(",")
        System.err.println("Available versions:[$s]")
        System.exit(1)
    }
    try {
        compiler!!.run(input.fold(StringBuilder()) { a, b -> a.append(b).append('\n') }.toString(), conf)
    }catch(e :IllegalStateException){
        System.out.flush()
        System.err.println(e.message)
    }
}
