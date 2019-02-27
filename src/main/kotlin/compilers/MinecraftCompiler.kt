package compilers

import GsCompiler
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
        while(true){
            val line = lines[currentLine]
            if(!line.isBlank()) {

                if (fi && line == "fi") {
                    fi = false
                } else if (!fi && !forward && line == "startloop") {
                    forward = true
                } else if (!fi && forward) {
                    when (line) {
                        "left" -> pointer1--
                        "right" -> pointer1++
                        "up" -> pointer2--
                        "down" -> pointer2++
                        "on" -> arr[pointer1][pointer2] = true
                        "off" -> arr[pointer1][pointer2] = false
                        "toggle" -> arr[pointer1][pointer2] = !arr[pointer1][pointer2]
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
                        "read" -> cache = arr[pointer1][pointer2]
                        "inbounds" -> cache = 0 <= pointer1 &&
                        "if" -> {
                            if (!cache) {
                                fi = true
                            }
                        }
                        "stoploop" -> {
                            if(!cache){
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