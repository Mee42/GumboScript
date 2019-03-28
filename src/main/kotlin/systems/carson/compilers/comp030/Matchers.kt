package systems.carson.compilers.comp030

interface Evaluator{
    fun evaluate(segment :Segment) :Boolean
}
class StringEvaluator(val string :String):Evaluator{
    override fun evaluate(segment: Segment): Boolean {
        return segment.isString() &&
                segment.string == string
    }
}
class TypeEvaluator(val type :Type):Evaluator{
    override fun evaluate(segment: Segment): Boolean {
        return segment.isExpression() &&
                segment.expression.type == type
    }
    constructor(type :String):this(Type(type))
}

enum class Matchers(vararg val args :Evaluator) {
    NOT(StringEvaluator("!"),TypeEvaluator("boolean")) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(!(segments[1].expression.get().value as Boolean)) }
        }
    },




    MOD(TypeEvaluator("int"),StringEvaluator("%"),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) { Value(
                segments[0].expression.get().value as Int % segments[2].expression.get().value as Int
            ) }
        }
    },
    MULT(TypeEvaluator("int"),StringEvaluator("*"),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) { Value(
                segments[0].expression.get().value as Int * segments[2].expression.get().value as Int
            ) }
        }
    },

    DIV(TypeEvaluator("int"),StringEvaluator("/"),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) { Value(
                segments[0].expression.get().value as Int / segments[2].expression.get().value as Int
            ) }
        }
    },


    PLUS(TypeEvaluator("int"),StringEvaluator("+"),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) { Value(
                segments[0].expression.get().value as Int + segments[2].expression.get().value as Int
            ) }
        }
    },
    MINUS(TypeEvaluator("int"),StringEvaluator("-"),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) { Value(
                segments[0].expression.get().value as Int - segments[2].expression.get().value as Int
            ) }
        }
    },

    AND(TypeEvaluator("boolean"),StringEvaluator("&&"),TypeEvaluator("boolean")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                segments[0].expression.get().value as Boolean and segments[2].expression.get().value as Boolean
            ) }
        }
    },
    OR(TypeEvaluator("boolean"),StringEvaluator("||"),TypeEvaluator("boolean")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                segments[0].expression.get().value as Boolean or segments[2].expression.get().value as Boolean
            ) }
        }
    },



    STRING_CONCAT0(TypeEvaluator("string"),StringEvaluator("+"),TypeEvaluator("string")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("string")) { Value(
                segments[0].expression.get().value as String + segments[2].expression.get().value as String
            ) }
        }
    },
    STRING_CONCAT1(TypeEvaluator("string"),StringEvaluator("+"),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("string")) { Value(
                segments[0].expression.get().value as String + (segments[2].expression.get().value as Int).toString()
            ) }
        }
    },

    STRING_CONCAT2(TypeEvaluator("string"),StringEvaluator("+"),TypeEvaluator("boolean")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("string")) { Value(
                segments[0].expression.get().value as String + (segments[2].expression.get().value as Boolean).toString()
            ) }
        }
    },


    EQUALS0(TypeEvaluator("string"),StringEvaluator("=="),TypeEvaluator("string")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                segments[0].expression.get().value as String == segments[2].expression.get().value as String
            ) }
        }
    },
    EQUALS1(TypeEvaluator("int"),StringEvaluator("=="),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                segments[0].expression.get().value as Int == segments[2].expression.get().value as Int
            ) }
        }
    },
    EQUALS2(TypeEvaluator("boolean"),StringEvaluator("=="),TypeEvaluator("boolean")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                segments[0].expression.get().value as Boolean == segments[2].expression.get().value as Boolean
            ) }
        }
    },


    NOT_EQUALS0(TypeEvaluator("string"),StringEvaluator("!="),TypeEvaluator("string")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                segments[0].expression.get().value as String != segments[2].expression.get().value as String
            ) }
        }
    },
    NOT_EQUALS1(TypeEvaluator("int"),StringEvaluator("!="),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                segments[0].expression.get().value as Int != segments[2].expression.get().value as Int
            ) }
        }
    },
    NOT_EQUALS2(TypeEvaluator("boolean"),StringEvaluator("!="),TypeEvaluator("boolean")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                segments[0].expression.get().value as Boolean != segments[2].expression.get().value as Boolean
            ) }
        }
    },

    LESS_THEN(TypeEvaluator("int"),StringEvaluator("<"),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                (segments[0].expression.get().value as Int) < (segments[2].expression.get().value as Int)
            ) }
        }
    },


    LESS_THEN_EQUAL_TOO(TypeEvaluator("int"),StringEvaluator("<="),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                (segments[0].expression.get().value as Int) <= (segments[2].expression.get().value as Int)
            ) }
        }
    },


    GREATER_THEN(TypeEvaluator("int"),StringEvaluator(">"),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                (segments[0].expression.get().value as Int) > (segments[2].expression.get().value as Int)
            ) }
        }
    },


    GREATER_THEN_EQUAL_TOO(TypeEvaluator("int"),StringEvaluator(">="),TypeEvaluator("int")){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) { Value(
                (segments[0].expression.get().value as Int) >= (segments[2].expression.get().value as Int)
            ) }
        }
    },






    ;

    abstract fun process(segments :List<Segment>): Expression

    fun matches(segments: List<Segment>):Boolean{
        for(i in 0 until segments.size){
            if(!args[i].evaluate(segments[i]))return false
        }
        return true
    }

}

fun main() {
    for (value in Matchers.values()) {
        println(value)
    }
}