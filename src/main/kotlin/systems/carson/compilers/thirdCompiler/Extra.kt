package systems.carson.compilers.thirdCompiler


class Line(
    val content :String,
    val lineNumber :Int,
    val statement :Statement)

interface Statement

class UnknownStatement :Statement
class BlankStatement :Statement
class PrintStatement(val expression: Expression = Expression.forValue("")) :Statement

class GotoStatement(val name :String, val expression :Expression) :Statement
class GotoSetStatement(val name :String) :Statement

class VariableAssigmentStatement(val name: String,
                                 val expression: Expression) :Statement

class VariableReassignStatement(val name :String,
                                val expression: Expression) :Statement

class ArrayAssigmentStatement(val name :String, val type :Type) :Statement

class ArraySetValueStatement(val name :String, val index :Expression, val value :Expression):Statement


class ExitStatement(val exp :Expression):Statement

class ArrayImpl(val name :String, val value :MutableList<Value> = mutableListOf(), val type :Type){

    override fun toString(): String {
        return "Array(name=$name, type=$type)"
    }
}

class Variable(val name :String, value :Value, val type :Type = value.type){
    var value :Value = value
        set(value) {
            //verify the type is the same as the old type
            if(type == field.type)
                field = value
            else
                error("Can't set a variable to a new type. new type: ${field.type} existing type = $type")
        }

    override fun toString(): String {
        return "Variable(name=$name, value=$value)"
    }
}


open class Expression(val type :Type,private val getter :() -> Value) {

    /** Type safe */
    fun get(): Value{
        val v = getter()
        if(!v.verify(type = type)){
            error("Expression called with conflicting types, ${v.type} and $type.  Value:${v.value}")
        }
        return v
    }

    companion object {
        fun forValue(i :Any):Expression{
            return object: Expression(typeForVar(i), { Value(i) }) {
                override fun toString(): String {
                    return "Expression($i)"
                }
            }
        }
    }

    override fun toString(): String {
        return "Expression(type=${type.name})"
    }
}



data class Value(val value :Any,val type :Type = typeForVar(value), private val verify :Boolean = true){
    init {
        //crash if invalid
        if(verify && !verify()){
            error("Value created with conflicting types, ${typeForVar(value)} and $type.  Value:$value")
        }
    }
    /** returns true if it is valid. Default values are this instances */
    fun verify(value :Any = this.value,type :Type = this.type):Boolean{
//        print("verify(value = $value, type = $type):")
        //        println(x)
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