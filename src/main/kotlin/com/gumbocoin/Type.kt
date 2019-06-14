package com.gumbocoin


class Type private constructor(val name :String){
    companion object{
        fun of(name :String):Type{
            return Type(name)
        }
        fun default():Type = void()
        fun void():Type = of("void")
        fun boolean():Type = of("boolean")
        fun int():Type = of("int")
        fun double():Type = of("double")
        fun long():Type = of("long")
        fun big():Type = of("big")
        fun string():Type = of("string")
    }

    override fun toString(): String {
        return "Type($name)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Type

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}
