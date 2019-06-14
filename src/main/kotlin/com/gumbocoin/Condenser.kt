package com.gumbocoin
import com.gumbocoin.Type.Companion.int
import com.gumbocoin.Type.Companion.double
import com.gumbocoin.Type.Companion.boolean
import com.gumbocoin.Type.Companion.long
import com.gumbocoin.Type.Companion.big

import com.gumbocoin.Type.Companion.void
import java.math.BigDecimal
import java.math.BigInteger
import java.util.function.Consumer
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


/*
ORDER OF OPERATIONS

the type in () are the types used, in that specific order
most cross-type functions need specific functions to transfer to a common type

multiplication:(int,double,long,big)         *
division:(int,double,long,big)               /
mod:(int,double,long,big)                    %
addition:(int,double,long,big)               +
subtraction:(int,double,long,big)            -
not :(!boolean)                              !
string concatenation: (string)               +
less then:(int,double,long,big)              <
greater then:(int,double,long,big)           >
then then or equal:(int,double,long,big)     <=
greater then or equal:(int,double,long,big)  >=
equality:(int,double,long,big)               ==
inverse equality:(int,double,long,big)       !=
bitwise AND:(boolean)                        &&
bitwise OR:(boolean)                         ||

 */

class NamedCondenser(val condenser: Condenser, val name :String){
    override fun toString(): String {
        return "NamedCondenser(name='$name')"
    }
}

infix fun String.name(condenser: Condenser):NamedCondenser = NamedCondenser(condenser,this)



val condensers = mutableListOf<NamedCondenser>().run {

    this += "INT_ADD" name "*".normal<Int> { a, b -> a * b }
    this += "DOUBLE_ADD" name "*".normal<Double> { a, b -> a * b }
    this += "LONG_ADD" name "*".normal<BigInteger> { a, b -> a * b }
    this += "BIG_ADD" name "*".normal<BigDecimal> { a, b -> a * b }


    this += "INT_DIV" name "/".normal<Int> { a,b -> a / b }
    this += "DOUBLE_DIV" name "/".normal<Double> { a,b -> a / b }
    this += "LONG_DIV" name "/".normal<BigInteger> { a,b -> a / b }
    this += "BIG_DIV" name "/".normal<BigDecimal> { a,b -> a / b }

    this += "INT_MOD" name "%".normal<Int> { a,b -> a % b }
    this += "DOUBLE_MOD" name "%".normal<Double> { a,b -> a % b }
    this += "LONG_MOD" name "%".normal<BigInteger> { a,b -> a % b }
    this += "BIG_MOD" name "%".normal<BigDecimal> { a,b -> a % b }

    this += "INT_PLUS" name "+".normal<Int> { a,b -> a + b }
    this += "DOUBLE_PLUS" name "+".normal<Double> { a,b -> a + b }
    this += "LONG_PLUS" name "+".normal<BigInteger> { a,b -> a + b }
    this += "BIG_PLUS" name "+".normal<BigDecimal> { a,b -> a + b }

    this += "INT_MINUS" name "-".normal<Int> {a,b -> a - b }
    this += "DOUBLE_MINUS" name "-".normal<Double> {a,b -> a - b }
    this += "LONG_MINUS" name "-".normal<BigInteger> {a,b -> a - b }
    this += "BIG_MINUS" name "-".normal<BigDecimal> {a,b -> a - b }

    this += "NEGATE" name Condenser(
        args = listOf(a("!"),a(boolean())),
        condense = { args -> retur(boolean(),(args[0].value as Boolean).not()) },
        returnType = boolean())

    this += "CONCAT" name "+".normal<String> {a,b -> a + b }

    this += "INT_LESS_THEN" name "<".booleanExpression<Int> { a, b ->  a < b }
    this += "DOUBLE_LESS_THEN" name "<".booleanExpression<Double> { a, b ->  a < b }
    this += "LONG_LESS_THEN" name "<".booleanExpression<BigInteger> { a, b ->  a < b }
    this += "BIG_LESS_THEN" name "<".booleanExpression<BigDecimal> { a, b ->  a < b }

    this += "INT_GREATER_THEN" name ">".booleanExpression<Int> { a, b ->  a > b }
    this += "DOUBLE_GREATER_THEN" name ">".booleanExpression<Double> { a, b ->  a > b }
    this += "LONG_GREATER_THEN" name ">".booleanExpression<BigInteger> { a, b ->  a > b }
    this += "BIG_GREATER_THEN" name ">".booleanExpression<BigDecimal> { a, b ->  a > b }

    this += "INT_GREATER_OR_LESS_THEN" name "<=".booleanExpression<Int> { a, b ->  a <= b }
    this += "DOUBLE_GREATER_OR_LESS_THEN" name "<=".booleanExpression<Double> { a, b ->  a <= b }
    this += "LONG_GREATER_OR_LESS_THEN" name "<=".booleanExpression<BigInteger> { a, b ->  a <= b }
    this += "BIG_GREATER_OR_LESS_THEN" name "<=".booleanExpression<BigDecimal> { a, b ->  a <= b }

    this += "INT_LESS_OR_LESS_THEN" name ">=".booleanExpression<Int> { a, b ->  a >= b }
    this += "DOUBLE_LESS_OR_LESS_THEN" name ">=".booleanExpression<Double> { a, b ->  a >= b }
    this += "LONG_LESS_OR_LESS_THEN" name ">=".booleanExpression<BigInteger> { a, b ->  a >= b }
    this += "BIG_LESS_OR_LESS_THEN" name ">=".booleanExpression<BigDecimal> { a, b ->  a >= b }

    this += "INT_EQUALS" name "==".booleanExpression<Int> { a,b -> a == b }
    this += "DOUBLE_EQUALS" name "==".booleanExpression<Double> { a,b -> a == b }
    this += "LONG_EQUALS" name "==".booleanExpression<BigInteger> { a,b -> a == b }
    this += "BIG_EQUALS" name "==".booleanExpression<BigDecimal> { a,b -> a == b }

    this += "INT_NOT_EQUALS" name "!=".booleanExpression<Int> { a,b -> a != b }
    this += "DOUBLE_NOT_EQUALS" name "!=".booleanExpression<Double> { a,b -> a != b }
    this += "LONG_NOT_EQUALS" name "!=".booleanExpression<BigInteger> { a,b -> a != b }
    this += "BIG_NOT_EQUALS" name "!=".booleanExpression<BigDecimal> { a,b -> a != b }




    this += "AND" name "&&".normal<Boolean> { a,b -> a and b }

    this += "OR" name "||".normal<Boolean> { a,b -> a and b }





    this
}


fun a(type :Type) = ArgumentType(type)
fun a(str :String) = ArgumentType(str)

fun retur(type :Type,value :Any)  = ReturnValue(Value(type,value))

@Suppress("UNCHECKED_CAST")
fun <T> List<Value>.i(i :Int):T = this[i].value as T

inline fun <reified T> typeOf():Type{
    return when(T::class){
        Int::class -> int()
        Boolean::class -> boolean()
        BigInteger::class -> long()
        Double::class -> double()
        BigDecimal::class -> big()
        String::class -> Type.string()
        Unit::class -> void()
        else -> error("Unknown type for class ${T::class}")
    }
}
inline fun <reified T :Any> String.normal(crossinline consumer:(T,T) -> T):Condenser{
    return Condenser(args = listOf(a(typeOf<T>()),a(this),a(typeOf<T>())),
        condense = { args -> retur(typeOf<T>(),consumer.invoke(args.i(0),args.i(1))) },
        returnType = typeOf<T>()
    )
}
inline fun <reified T:Any> String.booleanExpression(crossinline consumer: (T, T) -> Boolean):Condenser{
    return Condenser(args = listOf(a(typeOf<T>()),a(this),a(typeOf<T>())),
        condense = { args -> retur(boolean(),consumer.invoke(args.i(0),args.i(1))) },
        returnType = boolean())
}

class Condenser(val  args :List<ArgumentType>,
                     val returnType :Type,
                     val condense :(List<Value>) -> ReturnValue)


