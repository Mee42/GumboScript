package com.gumbocoin.kotlin

import com.gumbocoin.Parsed
import com.gumbocoin.Type
import com.gumbocoin.startNamespace
import java.math.BigDecimal
import java.math.BigInteger


val string by startNamespace {
    name = "string"
    sub {
        name = "from"
        function {
            name = "int"
            argument {
                name = "i"
                type = Type.int()
            }
            type = Type.string()
            execute<Int, String> { it.toString() }
        }
        function {
            name = "big"
            argument {
                name = "b"
                type = Type.big()
            }
            type = Type.string()
            execute<BigDecimal, String> { it.toPlainString() }
        }
        function {
            name = "double"
            argument {
                name = "d"
                type = Type.double()
            }
            type = Type.string()
            execute<Double, String> { it.toString() }
        }
        function {
            name = "long"
            argument {
                name = "l"
                type = Type.long()
            }
            type = Type.string()
            execute<BigInteger, String> { it.toString() }
        }
        function {
            name = "boolean"
            argument {
                name = "l"
                type = Type.boolean()
            }
            type = Type.string()
            execute<Boolean, String> { it.toString() }
        }

    }
    sub {
        name = "to"
        function {
            name = "int"
            argument {
                name = "str"
                type = Type.string()
            }
            type = Type.int()
            execute<String, Int> { Integer.parseInt(it) }
        }
        function {
            name = "double"
            argument {
                name = "str"
                type = Type.string()
            }
            type = Type.double()
            execute<String, Double> { it.toDouble() }
        }
        function {
            name = "long"
            argument {
                name = "str"
                type = Type.string()
            }
            type = Type.long()
            execute<String, BigInteger> { BigInteger(it, 10) }
        }
        function {
            name = "big"
            argument {
                name = "str"
                type = Type.string()
            }
            type = Type.big()
            execute<String, BigDecimal> { BigDecimal(it).setScale(Parsed.bigPrecision) }
        }
        function {
            name = "boolean"
            argument {
                name = "str"
                type = Type.string()
            }
            type = Type.boolean()
            execute<String, Boolean> { it == "true" }
        }


    }
    function {
        name = "concat"
        argument {
            name = "a"
            type = Type.string()
        }
        argument {
            name = "b"
            type = Type.string()
        }
        type = Type.string()
        execute<String, String, String> { a, b -> a + b }
    }
}