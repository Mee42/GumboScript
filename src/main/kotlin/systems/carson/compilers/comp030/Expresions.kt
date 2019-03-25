package systems.carson.compilers.comp030


/** This should always be primed with the latest variable stack on request by @param update*/
object VariableRetrialSystem{
    var update :() -> (Map<String,Value>?) = { null } //will be called every time a request is made

    private val variables :Map<String,Value>
        get() = update.invoke()!!

    fun get(s :String):Value{
        return variables.getValue(s)
    }
}

open class Expression(val type : Type, private val getter :() -> Value) {

    /** Type safe */
    fun get(): Value {
        val v = getter()
        if(!v.verify(type = type)){
            error("Expression called with conflicting types, expected $type but got ${v.type}}")
        }
        return v
    }

    companion object {
        fun forValue(value :Any):Expression{
            return ExpressionValue(value)
        }
    }

    override fun toString(): String {
        return "Expression(type=${type.name})"
    }
}

class VariableGetExpression(type :Type,val name :String) :Expression(type,{ VariableRetrialSystem.get(name) })

data class ExpressionValue(val value :Any) :Expression(typeForVar(value), { Value(value) })