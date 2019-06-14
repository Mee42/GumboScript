package com.gumbocoin.kotlin

import com.gumbocoin.GFunction
import com.gumbocoin.Namespace

fun main() {
    print(generateKotlinNamespace())
}

fun print(namespace : Namespace){
    print("",true,namespace)
}

fun GFunction.string():String{
    return "$name(" +
            arguments.fold("") { a,b -> "$a," + b.type.name + " " + b.name }
                .toString()
                .replaceFirst(",","") +
            ") " + returnType.name
}

private fun print(prefix: String, isTail: Boolean, namespace :Namespace) {
    println(prefix + (if (isTail) "└── " else "├── ") + namespace.name)

    for(i in 0 until namespace.functions.size){
        val tail = (i + 1 == namespace.functions.size) && namespace.subs.isEmpty()
        val c = if(!isTail) "|" else " "
        println(prefix + (if (tail) "$c    └── " else "$c    ├── ") + namespace.functions[i].string())
    }
    for(i in 0 until namespace.subs.size){
        print(prefix + (if (isTail) "     " else "|    "),i + 1 == namespace.subs.size,namespace.subs[i])
    }
}