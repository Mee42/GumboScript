package com.gumbocoin.kotlin

import com.gumbocoin.*
import com.gumbocoin.Type.Companion.big
import com.gumbocoin.Type.Companion.boolean
import com.gumbocoin.Type.Companion.double
import com.gumbocoin.Type.Companion.int
import com.gumbocoin.Type.Companion.long
import com.gumbocoin.Type.Companion.string
import com.gumbocoin.Type.Companion.void
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.math.pow

private fun Namespace.mapBack(): PartialNamespace {
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

private fun GFunction.mapBack(): PlaintextFunction {
    return PlaintextFunction(name, returnType, arguments, StupidList(), startingLine)
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

fun generateKotlinNamespace(): Namespace {
    return kotlin
}
fun generatePlaintextKotlinNamespace(): PartialNamespace {
    return kotlin.mapBack()
}

private val kotlin by startNamespace {
    name = "kotlin"
    sub(math)
    sub(io)
    sub(string)
}