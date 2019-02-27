package systems.carson.compilers

import systems.carson.GsCompiler
import java.lang.StringBuilder
import java.util.*

class MinecraftCompiler : GsCompiler {
    override val version: String = "mc.0.1"

    override fun run(input: String, conf: Map<String, List<String>>): Int {
        val lines = input.split("\n")
        var currentLine = 0
        var forward = true
        var fi = false
        var cache = false
        val arr = Array(10) { Array(10) { false } }
        var pointer1 = 0
        var pointer2 = 0
        fun get(i :Int = pointer1,ii :Int = pointer2):Boolean{
            try{ return arr[i][ii] }catch(e :ArrayIndexOutOfBoundsException){
                error("out of bounds on line ${currentLine+2} ")
            }
        }
        fun set(i :Int = pointer1,ii :Int = pointer2,value :Boolean) {
            try{ arr[i][ii] = value }catch(e :ArrayIndexOutOfBoundsException){
                error("out of bounds on line ${currentLine+2}")
            }
        }

        while(true){
            val line = lines[currentLine]
            if(!line.isBlank()) {

                if (fi && line == "fi") {
                    fi = false
                } else if (!fi && !forward && line == "startloop") {
                    forward = true
                } else if (!fi && forward) {
                    when (line) {
                        "up" -> pointer1--
                        "down" -> pointer1++
                        "left" -> pointer2--
                        "right" -> pointer2++
                        "on" -> set(value = true)
                        "off" -> set(value = false)
                        "toggle" -> set(value = get())
                        "home" -> {
                            pointer1 = 0
                            pointer2 = 0
                        }
                        "print" -> {
                            var s = ""
                            for(r in arr.withIndex()){
                                for(c in r.value.withIndex()){
                                    s += (if(r.index == pointer1 && c.index == pointer2) " P" else "  ") +
                                            (if(c.value) "1" else "0")
                                }
                                s+="\n"
                            }
                            println(s)
                        }
                        "exit" -> return 0
                        "read" -> cache = get()
                        "inbounds" -> cache = pointer1 in 0 until 10 && pointer2 in 0 until 10
                        "if" -> {
                            if (!cache) {
                                fi = true
                            }
                        }
                        "stoploop" -> {
                            if(cache){
                                forward = false
                            }
                        }
                        "flipcache" -> {cache = !cache}
                        "startloop" -> {}
                        "fi" -> {}
                        "gotostart" -> {}
                        else -> error("unknown command $line")
                    }
                }
            }
            if(line == "gotostart")
                currentLine = 0
            else
                currentLine += if(forward) 1 else -1
        }

    }
}