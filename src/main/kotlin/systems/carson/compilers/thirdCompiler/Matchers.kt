package systems.carson.compilers.thirdCompiler

enum class Matchers(vararg val conditionals: Conditional){
    MULTIPLICATION(
        TypeConditional("int"),
        StringEqualConditional("*"),
        TypeConditional("int")
    ) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) {
                Value(
                    segments[0].expression.get().value as Int *
                            segments[2].expression.get().value as Int
                )
            }
        }
    },
    DIVISION(
        TypeConditional("int"),
        StringEqualConditional("/"),
        TypeConditional("int")
    ) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) {
                Value(
                    segments[0].expression.get().value as Int /
                            segments[2].expression.get().value as Int
                )
            }
        }
    },

    SUBTRACTION(
        TypeConditional("int"),
        StringEqualConditional("-"),
        TypeConditional("int")
    ) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) {
                Value(
                    segments[0].expression.get().value as Int -
                            segments[2].expression.get().value as Int
                )
            }
        }
    },

    ADDITION(
        TypeConditional("int"),
        StringEqualConditional("+"),
        TypeConditional("int")
    ) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("int")) {
                Value(
                    segments[0].expression.get().value as Int +
                            segments[2].expression.get().value as Int
                )
            }
        }
    },

    STRING_CONCAT(
        TypeConditional("string"),
        StringEqualConditional("+"),
        TypeConditional("string")
    ) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("string")) {
                Value(
                    segments[0].expression.get().value as String +
                            segments[2].expression.get().value as String
                )
            }
        }
    },

    STRING_INT_CONCAT(
        TypeConditional("string"),
        StringEqualConditional("+"),
        TypeConditional("int")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("string")) {
                Value(
                    segments[0].expression.get().value as String +
                            (segments[2].expression.get().value as Int).toString()
                )
            }
        }
    },

    INT_STRING_CONCAT(
        TypeConditional("int"),
        StringEqualConditional("+"),
        TypeConditional("string")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("string")) {
                Value(
                    (segments[0].expression.get().value as Int).toString() +
                            segments[2].expression.get().value as String
                )
            }
        }
    },

    STRING_BOOLEAN_CONCAT(
        TypeConditional("string"),
        StringEqualConditional("+"),
        TypeConditional("boolean")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("string")) {
                Value(
                    segments[0].expression.get().value as String +
                            (segments[2].expression.get().value as Boolean).toString()
                )
            }
        }
    },

    BOOLEAN_NEGATE(
        StringEqualConditional("!"),
        TypeConditional("boolean")
    ) {
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) {
                Value(!(segments[1].expression.get().value as Boolean))
            }
        }
    },

    INT_EQUALS(
        TypeConditional("int"),
        StringEqualConditional("=="),
        TypeConditional("int")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) {
                Value((segments[0].expression.get().value as Int) == (segments[2].expression.get().value as Int))
            }
        }
    },

    STRING_EQUALS(
        TypeConditional("string"),
        StringEqualConditional("=="),
        TypeConditional("string")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) {
                Value((segments[0].expression.get().value as String) == (segments[2].expression.get().value as String))
            }
        }
    },

    BOOL_AND(
        TypeConditional("boolean"),
        StringEqualConditional("&"),
        TypeConditional("boolean")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) {
                Value((segments[0].expression.get().value as Boolean) and (segments[2].expression.get().value as Boolean))
            }
        }
    },

    BOOL_OD(
        TypeConditional("boolean"),
        StringEqualConditional("|"),
        TypeConditional("boolean")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) {
                Value((segments[0].expression.get().value as Boolean) or (segments[2].expression.get().value as Boolean))
            }
        }
    },

    LESS_THEN(
        TypeConditional("int"),
        StringEqualConditional("<"),
        TypeConditional("int")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) {
                val one = segments[0].expression.get().value as Int
                val two = segments[2].expression.get().value as Int
                Value(one < two)
            }
        }
    },

    GREATER_THEN(
        TypeConditional("int"),
        StringEqualConditional(">"),
        TypeConditional("int")
    ){
        override fun process(segments: List<Segment>): Expression {
            return Expression(Type("boolean")) {
                val one = segments[0].expression.get().value as Int
                val two = segments[2].expression.get().value as Int
                Value(one > two)
            }
        }
    },

    ;

    abstract fun process(segments :List<Segment>): Expression

}