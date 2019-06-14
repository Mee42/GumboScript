package com.gumbocoin

import com.google.gson.Gson
import com.google.gson.GsonBuilder


private val gson = Gson()
private val pretty = GsonBuilder().setPrettyPrinting().create()

fun <T> toJson(t: T):String{
    return gson.toJson(t)
}


fun <T> toPrettyJson(t: T):String{
    return pretty.toJson(t)
        .replace("\\u003c","<")
        .replace("\\u003e",">")
        .replace("\\u003d","=")

}