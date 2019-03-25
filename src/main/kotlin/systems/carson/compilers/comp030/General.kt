package systems.carson.compilers.comp030

import systems.carson.Logger


data class Value(var value :Any,val type :Type = typeForVar(value), private val verify :Boolean = true){
    init {
        //crash if invalid
        if(verify && !verify()){
            error("Value created with conflicting types, ${typeForVar(value)} and $type.  Value:$value")
        }
    }
    /** returns true if it is valid. Default values are this instances */
    fun verify(value :Any = this.value,type :Type = this.type):Boolean{
//        Logger.verbose("verify(value = $value, type = $type):")
        return typeForVar(value) == type
    }

    override fun toString(): String {
        return if(type.name == "string")
            "Value(value=\"$value\", type=$type)"
        else
            "Value(value=$value, type=$type)"
    }

}



data class Type(val name :String){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Type

//        println("${this.name} == ${other.name}: ${this.name == other.name}")
        return this.name == other.name

    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

fun typeForVar(a :Any):Type{
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    return when(a){
        is Boolean -> Type("boolean")
        is Int -> Type("int")
        is Integer -> Type("int")
        is String -> Type("string")
        is Object -> Type("object")
        else -> error("Unknown type ${a.javaClass.name} value:$a")
    }
}