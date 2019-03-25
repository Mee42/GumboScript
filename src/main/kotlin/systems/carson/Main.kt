package systems.carson

import java.io.File
import systems.carson.compilers.FirstCompiler
import systems.carson.compilers.MinecraftCompiler
import systems.carson.compilers.comp030.Compiler030
import systems.carson.compilers.secondCompiler.SecondCompiler
import systems.carson.compilers.thirdCompiler.ThirdCompiler

val compilers = listOf({ FirstCompiler() },
                       { SecondCompiler() },
                       { MinecraftCompiler() },
                       { ThirdCompiler() },
                       { Compiler030() })

const val printStackTrace = true

class Parser{
    companion object {
        fun initalParse(file :File):Pair<List<String>,Map<String,List<String>>>{
            val input = file.readLines().toMutableList()
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
            return Pair(input,conf)
        }
    }
}


fun mainf(args :Array<String>) {
    if(args.isEmpty()){
        System.err.println("You need to have an file specified")
        System.exit(1)
    }
    val sourceCode = File(args[0])

    val pair = Parser.initalParse(sourceCode)
    val input = pair.first
    val conf = pair.second

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
        val exit = compiler!!.run(input.fold(StringBuilder()) { a, b -> a.append(b).append('\n') }.toString(), conf)
        System.exit(exit)
    }catch(e :IllegalStateException){
        System.out.flush()
        System.err.println(e.message)
        if(printStackTrace){
            System.err.println("\n\n")
            e.printStackTrace()
        }
    }
}
