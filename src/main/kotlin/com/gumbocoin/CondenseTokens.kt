package com.gumbocoin


class ArgumentType private constructor(
    private val str :String?,
    private val type :Type?){
    constructor(str: String):this(str,null)
    constructor(type :Type):this(null,type)
    fun isString() = str != null
    fun isType() = type != null

    fun getType():Type = type!!
    fun getString():String = str!!
}


fun condenseTokenize(list :List<Token>):List<Token>{
    verbose("Condensing $list")

    condenserTest@ for(condenser in condensers){
        verbose("Testing condenser $condenser")
        val argsSize = condenser.condenser.args.size
        if(list.size < argsSize){
            continue@condenserTest
        }
        verbose("argsSize = $argsSize going to ${list.size - argsSize}")
        sublist@ for(index in 0 .. list.size - argsSize) {
            val sublist = list.subList(index,index + argsSize)
            verbose("Testing sublist $sublist")
            for(subindex in 0 until sublist.size){
                val token = sublist[subindex]
                val argumentType = condenser.condenser.args[subindex]
                if(token.isString()){
                    if(!argumentType.isString()){
                        continue@sublist
                    }
                    if(token.getString() != argumentType.getString()){
                        continue@sublist
                    }
                }else{
                    if(!argumentType.isType()) {
                        continue@sublist
                    }
                    if(token.getExpression().returnType != argumentType.getType()){
                        continue@sublist
                    }
                }
            }
            //if works great
            val firstSubList = list.subList(0,index)
            val secondSublist = list.subList(index,index + argsSize)
            val thirdSubList = list.subList(index + argsSize,list.size)
            val middleExpression = CondenserExpression(
                arguments = secondSublist.filter { it.isExpression() }.map { it.getExpression() },
                condenser = condenser.name,
                returnType = condenser.condenser.returnType)

            val concatenatedList = firstSubList + Token(middleExpression) + thirdSubList
            return condenseTokenize(concatenatedList)
        }
    }
    return list
}
