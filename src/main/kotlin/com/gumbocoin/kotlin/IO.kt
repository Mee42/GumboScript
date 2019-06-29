package com.gumbocoin.kotlin

import com.gumbocoin.OUT
import com.gumbocoin.Type
import com.gumbocoin.Value
import com.gumbocoin.startNamespace
import java.util.*


object IO{
    val scanner = Scanner(System.`in`)
}

val io by startNamespace {
    name = "io"
    sub {
        name = "out"
        function {
            name = "print"
            argument {
                name = "str"
                type = Type.string()
            }
            type = Type.void()
            execute5<String> {
                OUT.print(it)
            }
        }
        function {
            name = "println"
            argument {
                name = "str"
                type = Type.string()
            }
            type = Type.void()
            execute5<String> {
                OUT.println(it)
            }
        }

    }
    sub {
        name = "in"
        function {
            name = "line"
            type = Type.string()
            execute1 { Value(Type.string(), IO.scanner.nextLine()) }
        }
    }
}
