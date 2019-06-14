package com.gumbocoin

import com.gumbocoin.Type.Companion.big
import com.gumbocoin.Type.Companion.boolean
import com.gumbocoin.Type.Companion.double
import com.gumbocoin.Type.Companion.int
import com.gumbocoin.Type.Companion.long
import com.gumbocoin.Type.Companion.string
import com.gumbocoin.Type.Companion.void
import sun.misc.GC
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.pow

private fun Namespace.mapBack():PartialNamespace{
    val functions = functions.map { it.mapBack() }
    val subs = subs.map { it.mapBack() }
    val x = PartialNamespace(
        name = name,
        functions = functions,
        subs = subs
    )
    subs.forEach { it.parent = x }
    functions.forEach { it.parent = x }
    return x
}

private fun GFunction.mapBack():PlaintextFunction{
    return PlaintextFunction(name,returnType,arguments,StupidList(),startingLine)
}

private class StupidList<T>:List<T>{
    override val size: Int get() = error("No elements in this list")
    override fun contains(element: T) = error("No elements in this list")
    override fun containsAll(elements: Collection<T>) = error("No elements in this list")
    override fun get(index: Int) = error("No elements in this list")
    override fun indexOf(element: T) = error("No elements in this list")
    override fun isEmpty() = error("No elements in this list")
    override fun iterator() = error("No elements in this list")
    override fun lastIndexOf(element: T) = error("No elements in this list")
    override fun listIterator() = error("No elements in this list")
    override fun listIterator(index: Int) = error("No elements in this list")
    override fun subList(fromIndex: Int, toIndex: Int) = error("No elements in this list")
}

fun generateKotlinNamespace():Namespace {
    return kotlin
}
fun generatePlaintextKotlinNamespace():PartialNamespace {
    return kotlin.mapBack()
}

private val kotlin by startNamespace {
    name = "kotlin"
    sub(math)
    sub(io)
    sub(string)
}

private val math by startNamespace {
    sub(int)
    sub(big)
    name = "math"

}

private val big by startNamespace {
    name = "big"
    function {
        name = "abs"
        type = big()
        argument {
            name = "i"
            type = big()
        }
        execute<BigDecimal,BigDecimal> { it.abs() }
    }
    function {
        name = "pow"
        type = big()
        argument {
            name = "a"
            type = big()
        }
        argument {
            name = "b"
            type = big()
        }
        execute<BigDecimal,BigDecimal,BigDecimal> { a,b -> a.toDouble().pow(b.toDouble()).toBigDecimal().setScale(GConstants.BIG_DECIMAL_SCALE) }
    }
    function {
        name = "div"
        type = big()
        argument {
            name = "a"
            type = big()
        }
        argument {
            name = "b"
            type = big()
        }
        execute<BigDecimal,BigDecimal,BigDecimal> {a,b ->
            a.div(b)
        }
    }
    function {
        name = "ONE"
        type = big()
        execute3 { Value(big(),BigDecimal(1).setScale(GConstants.BIG_DECIMAL_SCALE)) }
    }
    function {
        name = "ZERO"
        type = big()
        execute3 { Value(big(),BigDecimal(0).setScale(GConstants.BIG_DECIMAL_SCALE)) }
    }
    function {
        name = "TEN"
        type = big()
        execute3 { Value(big(),BigDecimal(10).setScale(GConstants.BIG_DECIMAL_SCALE)) }
    }

}


private val int by startNamespace {
    name = "int"
    function {
        name = "abs"
        type = int()
        argument {
            name = "i"
            type = int()
        }
        execute<Int,Int> { Math.abs(it) }
    }
    function {
        name = "pow"
        type = int()
        argument {
            name = "a"
            type = int()
        }
        argument {
            name = "b"
            type = int()
        }
        execute<Int,Int,Int> { a,b -> Math.pow(a.toDouble(),b.toDouble()).toInt() }
    }
    function {
        name = "asDouble"
        type = double()
        argument {
            name = "i"
            type = int()
        }
        execute<Int,Double> { it.toDouble() }
    }
    function {
        name = "asLong"
        type = long()
        argument {
            name = "i"
            type = int()
        }
        execute<Int, BigInteger> { it.toBigInteger() }
    }
    function {
        name = "asBig"
        type = big()
        argument {
            name = "i"
            type = int()
        }
        execute<Int,BigDecimal> { BigDecimal.valueOf(it.toLong()).setScale(GConstants.BIG_DECIMAL_SCALE) }
    }
    function {
        name = "ZERO"
        type = int()
        execute1 { Value(int(),0) }
    }
    function {
        type = int()
        name = "ONE"
        execute1 { Value(int(),0) }
    }
    function {
        type = int()
        name = "TEN"
        execute1 { Value(int(), 0) }
    }
}

private val string by startNamespace {
    name = "string"
    sub {
        name = "from"
        function {
            name = "int"
            argument {
                name = "i"
                type = int()
            }
            type = string()
            execute<Int,String> { it.toString() }
        }
        function {
            name = "big"
            argument {
                name = "b"
                type = big()
            }
            type = string()
            execute<BigDecimal,String> { it.toPlainString() }
        }
        function {
            name = "double"
            argument {
                name = "d"
                type = double()
            }
            type = string()
            execute<Double,String> { it.toString() }
        }
        function {
            name = "long"
            argument {
                name = "l"
                type = long()
            }
            type = string()
            execute<BigInteger,String> { it.toString() }
        }
        function {
            name = "boolean"
            argument {
                name = "l"
                type = boolean()
            }
            type = string()
            execute<Boolean,String> { it.toString() }
        }

    }
    sub {
        name = "to"
        function {
            name = "int"
            argument {
                name = "str"
                type = string()
            }
            type = int()
            execute<String,Int> { Integer.parseInt(it) }
        }
        function {
            name = "double"
            argument {
                name = "str"
                type = string()
            }
            type = double()
            execute<String,Double> { it.toDouble() }
        }
        function {
            name = "long"
            argument {
                name = "str"
                type = string()
            }
            type = long()
            execute<String,BigInteger> { BigInteger(it,10) }
        }
        function {
            name = "big"
            argument {
                name = "str"
                type = string()
            }
            type = big()
            execute<String,BigDecimal> { BigDecimal(it).setScale(GConstants.BIG_DECIMAL_SCALE) }
        }
        function {
            name = "boolean"
            argument {
                name = "str"
                type = string()
            }
            type = boolean()
            execute<String,Boolean> { it == "true" }
        }


    }
    function {
        name = "concat"
        argument {
            name = "a"
            type = string()
        }
        argument {
            name = "b"
            type = string()
        }
        type = string()
        execute<String,String,String> { a,b -> a + b }
    }
}

object IO{
    val scanner = Scanner(System.`in`)
}

private val io by startNamespace {
    name = "io"
    sub {
        name = "out"
        function {
            name = "print"
            argument {
                name = "str"
                type = string()
            }
            type = void()
            execute5<String> {
                print(it)
            }
        }
        function {
            name = "println"
            argument {
                name = "str"
                type = string()
            }
            type = void()
            execute5<String> {
                println(it)
            }
        }

    }
    sub {
        name = "in"
        function {
            name = "line"
            type = string()
            execute1 { Value(string(),IO.scanner.nextLine()) }
        }
    }
}
