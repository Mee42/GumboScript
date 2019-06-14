package com.gumbocoin

import java.math.BigDecimal

fun main() {
    val a = BigDecimal.valueOf(10.toLong()).setScale(GConstants.BIG_DECIMAL_SCALE)
    val b = BigDecimal.valueOf(100.toLong()).setScale(GConstants.BIG_DECIMAL_SCALE)
    val c = a.div(b)
    println(c)
}