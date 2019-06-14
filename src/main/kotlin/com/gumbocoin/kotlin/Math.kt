package com.gumbocoin.kotlin

import com.gumbocoin.Parsed
import com.gumbocoin.Type
import com.gumbocoin.Value
import com.gumbocoin.startNamespace
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.math.pow

val math by startNamespace {
    name = "math"
    sub(int)
    sub(big)
}

private val big by startNamespace {
    name = "big"
    function {
        name = "abs"
        type = Type.big()
        argument {
            name = "i"
            type = Type.big()
        }
        execute<BigDecimal, BigDecimal> { it.abs() }
    }//abs
    function {
        name = "pow"
        type = Type.big()
        argument {
            name = "a"
            type = Type.big()
        }
        argument {
            name = "b"
            type = Type.big()
        }
        execute<BigDecimal, BigDecimal, BigDecimal> { a, b ->
            a.toDouble().pow(b.toDouble()).toBigDecimal().setScale(Parsed.bigPrecision)
        }
    }//pow

    function {
        name = "add"
        type = Type.big()
        argument {
            name = "a"
            type = Type.big()
        }
        argument {
            name = "b"
            type = Type.big()
        }
        execute<BigDecimal, BigDecimal, BigDecimal> { a, b ->
            a.add(b)
        }
    }//add
    function {
        name = "sub"
        type = Type.big()
        argument {
            name = "a"
            type = Type.big()
        }
        argument {
            name = "b"
            type = Type.big()
        }
        execute<BigDecimal, BigDecimal, BigDecimal> { a, b ->
            a.subtract(b)
        }
    }//sub
    function {
        name = "mult"
        type = Type.big()
        argument {
            name = "a"
            type = Type.big()
        }
        argument {
            name = "b"
            type = Type.big()
        }
        execute<BigDecimal, BigDecimal, BigDecimal> { a, b ->
            a.multiply(b)
        }
    }//mult
    function {
        name = "div"
        type = Type.big()
        argument {
            name = "a"
            type = Type.big()
        }
        argument {
            name = "b"
            type = Type.big()
        }
        execute<BigDecimal, BigDecimal, BigDecimal> { a, b ->
            a.div(b)
        }
    }//div


    function {
        name = "asLong"
        argument {
            name = "i"
            type = Type.big()
        }
        type = Type.long()
        execute<BigDecimal,BigInteger> { it.toBigInteger() }
    }//asLong
    function {
        name = "asDouble"
        argument {
            name = "i"
            type = Type.big()
        }
        type = Type.double()
        execute<BigDecimal,Double> { it.toDouble() }
    }//asDouble
    function {
        name = "asInt"
        argument {
            name = "i"
            type = Type.big()
        }
        type = Type.int()
        execute<BigDecimal,Int> { it.toInt() }
    }//asInt


    function {
        name = "ZERO"
        type = Type.big()
        execute3 { Value(Type.big(), BigDecimal(0).setScale(Parsed.bigPrecision)) }
    }//ZERO
    function {
        name = "ONE"
        type = Type.big()
        execute3 { Value(Type.big(), BigDecimal(1).setScale(Parsed.bigPrecision)) }
    }//ONE
    function {
        name = "TEN"
        type = Type.big()
        execute3 { Value(Type.big(), BigDecimal(10).setScale(Parsed.bigPrecision)) }
    }//TEN

}

private val long by startNamespace {
    name = "long"
    function {
        type = Type.long()
        argument { type = Type.long() }
        execute<BigInteger,BigInteger> { it.abs() }
    }//abs
    function {
        type = Type.long()
        argument { type = Type.long() }
        argument { type = Type.long() }
        execute<BigInteger,BigInteger,BigInteger> {a,b -> a.pow(b.toInt()) }
    }//pow


    function {
        name = "add"
        argument { type = Type.long() }
        argument { type = Type.long() }
        execute<BigInteger,BigInteger,BigInteger> { a,b -> a + b }
    }//add
    function {
        name = "sub"
        argument { type = Type.long() }
        argument { type = Type.long() }
        execute<BigInteger,BigInteger,BigInteger> { a,b -> a - b }
    }//sub
    function {
        name = "mult"
        argument { type = Type.long() }
        argument { type = Type.long() }
        execute<BigInteger,BigInteger,BigInteger> { a,b -> a * b }
    }//mult
    function {
        name = "div"
        argument { type = Type.long() }
        argument { type = Type.long() }
        execute<BigInteger,BigInteger,BigInteger> { a,b -> a / b}
    }//div
    function {
        name = "mod"
        argument { type = Type.long() }
        argument { type = Type.long() }
        execute<BigInteger,BigInteger,BigInteger> { a,b -> a.rem(b) }
    }//mod

    function {
        name = "asInt"
        argument { type = Type.long() }
        execute<BigInteger,Int> { it.toInt() }
    }//asInt
    function {
        name = "asDouble"
        argument { type = Type.long() }
        execute<BigInteger,Double> { it.toDouble() }
    }//asDouble
    function {
        name = "asBig"
        argument { type = Type.long() }
        execute<BigInteger,BigDecimal> { it.toBigDecimal().setScale(Parsed.bigPrecision) }
    }//asBig

    function {
        name = "ZERO"
        type = Type.int()
        execute1 { Value(Type.long(), BigInteger.ZERO) }
    }//ZERO
    function {
        type = Type.int()
        name = "ONE"
        execute1 { Value(Type.long(), BigInteger.ONE) }
    }//ONE
    function {
        type = Type.int()
        name = "TEN"
        execute1 { Value(Type.long(), BigInteger.TEN) }
    }//TEN

}

private val double by startNamespace {
    name = "double"

    function {
        argument { type = Type.double() }
        name = "abs"
        execute<Double,Double> { it.absoluteValue }
    }//abs
    function {
        argument { type = Type.double() }
        argument { type = Type.double() }
        name = "pow"
        execute<Double,Double,Double> { a,b -> a.pow(b) }
    }//pow

    function {
        name = "add"
        argument { type = Type.double() }
        argument { type = Type.double() }
        execute<Double,Double,Double> { a,b -> a + b }
    }//add
    function {
        name = "sub"
        argument { type = Type.double() }
        argument { type = Type.double() }
        execute<Double,Double,Double> { a,b -> a - b }
    }//sub
    function {
        name = "mult"
        argument { type = Type.double() }
        argument { type = Type.double() }
        execute<Double,Double,Double> { a,b -> a * b }
    }//mult
    function {
        name = "div"
        argument { type = Type.double() }
        argument { type = Type.double() }
        execute<Double,Double,Double> { a,b -> a / b }
    }//div

    function {
        name = "asBig"
        argument { type = Type.double() }
        type = Type.big()
        execute<Double,BigDecimal> { it.toBigDecimal().setScale(Parsed.bigPrecision) }
    }//asBig
    function {
        name = "asInt"
        argument { type = Type.double() }
        type = Type.int()
        execute<Double,Int> { it.toInt() }
    }//asInt
    function {
        name = "asLong"
        argument { type = Type.double() }
        type = Type.long()
        execute<Double,BigInteger> { BigDecimal.valueOf(it).toBigInteger() }
    }//asLong

    function {
        name = "ZERO"
        type = Type.double()
        execute1 { Value(Type.double(),0) }
    }//ZERO
    function {
        name = "ONE"
        type = Type.double()
        execute1 { Value(Type.double(),1) }
    }//ONE
    function {
        name = "TEN"
        type = Type.double()
        execute1 { Value(Type.double(),10) }
    }//TEN

}


private val int by startNamespace {
    name = "int"
    function {
        name = "abs"
        type = Type.int()
        argument {
            name = "i"
            type = Type.int()
        }
        execute<Int, Int> { Math.abs(it) }
    }//abs
    function {
        name = "pow"
        type = Type.int()
        argument {
            name = "a"
            type = Type.int()
        }
        argument {
            name = "b"
            type = Type.int()
        }
        execute<Int, Int, Int> { a, b -> Math.pow(a.toDouble(), b.toDouble()).toInt() }
    }//pow

    function {
        name = "add"
        argument {
            name = "a"
            type = Type.int()
        }
        argument {
            name = "a"
            type = Type.int()
        }
        execute<Int,Int,Int> { a,b -> a + b }
    }//add
    function {
        name = "sub"
        argument {
            name = "a"
            type = Type.int()
        }
        argument {
            name = "a"
            type = Type.int()
        }
        execute<Int,Int,Int> { a,b -> a - b }
    }//sub
    function {
        name = "mult"
        argument {
            name = "a"
            type = Type.int()
        }
        argument {
            name = "a"
            type = Type.int()
        }
        execute<Int,Int,Int> { a,b -> a * b }
    }//mult
    function {
        name = "div"
        argument {
            name = "a"
            type = Type.int()
        }
        argument {
            name = "a"
            type = Type.int()
        }
        execute<Int,Int,Int> { a,b -> a / b }
    }//div
    function {
        name = "mod"
        argument {
            name = "a"
            type = Type.int()
        }
        argument {
            name = "a"
            type = Type.int()
        }
        execute<Int,Int,Int> { a,b -> a % b }
    }//mod

    function {
        name = "asDouble"
        type = Type.double()
        argument {
            name = "i"
            type = Type.int()
        }
        execute<Int, Double> { it.toDouble() }
    }//asDouble
    function {
        name = "asLong"
        type = Type.long()
        argument {
            name = "i"
            type = Type.int()
        }
        execute<Int, BigInteger> { it.toBigInteger() }
    }//asLong
    function {
        name = "asBig"
        type = Type.big()
        argument {
            name = "i"
            type = Type.int()
        }
        execute<Int, BigDecimal> { BigDecimal.valueOf(it.toLong()).setScale(Parsed.bigPrecision) }
    }//asBig

    function {
        name = "ZERO"
        type = Type.int()
        execute1 { Value(Type.int(), 0) }
    }//ZERO
    function {
        type = Type.int()
        name = "ONE"
        execute1 { Value(Type.int(), 1) }
    }//ONE
    function {
        type = Type.int()
        name = "TEN"
        execute1 { Value(Type.int(), 10) }
    }//TEN
}
