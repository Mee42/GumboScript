### Compiler 0.3.0 specs


My first compiler spec! Maybe I'll write one for 0.2.0 and 0.1.0 as well - 0.3.0 isn't very functional.

Regardless, lets go over how to use it

##### Compile time

0.3.0 is **Compiled**

But let's talk about that for a sec.

> A compiler is a computer program that transforms computer code written in one programming language (the source language) into another programming language (the target language)

*Wikipedia*

Instead of producting code in another language, I generate an in-memory object called `CompiledProgram`.
This class has all the data needed to run the program, yet has none of the inital text.

In a sense, this could be considered an in-memory form of bytecode. There's one key program - it can't be serialized

The problem with serialization is that I use lambdas that access the variable memory. 
This code can't be accessed after it's compiled, ie during kotlin runtime.
It needs to be specifiably defined (ie not in a lambda) or written in another form of code.
The problem is, this "other type of code", (if it's simi human readable) Will almost definitely be longer then the inital
code.

So, we'll stick with this. It's very nice, compiling. All sorts of fancy things you can do.

#### How to actually write in it

statements are executed on a line-by-line bases. 
Expressions, which have a compile-time determined type, are appended on statements

These statements currently supported:
- print `expression`
- var `name` equals `expression`
- `name` equals `expression`

In cases of `name equals expression`, two things are required for compilation to be successfull

- `name` is already defined as a variable
- the type of `name` is the same as the type of `expression`

Breaking either of those will cause a compilation error. This way, type safety is maintained.

To get the value of a variable, use `:[name]` sintax.

Expressions are pretty simple. It's a list of values and strings. For example:
```markdown
:[i] + 4 * 6
\__/ ^ ^ ^ ^
 ||  | | | |
 ||  \---\----> string
 ||    |   |
 ||    \---\--> value
 ||
 \\===========> variable (a value)
 ```
 
 This will be compiled into something simpler, like this
 
 ` { get var "i" } + { 4 * 6 } `
 
 And then finally added
 
 ` { { get var "i" } + { 4 * 6 } }`
 
 Which can be executed at runtime.
 
 Operands are extremely easy to add. For example, here is the string concatenation one:
 ```kotlin
 
STRING_CONCAT(TypeConditional("string"),
StringEqualConditional("+"),
TypeConditional("string")) {
    override fun process(segments: List<Segment>): Expression {
        return Expression(Type("string")) { Value(
            segments[0].expression.get().value as String +
                    segments[2].expression.get().value as String
        ) }
    }
};
```

Let's break this down:

`STRING_CONCAT()`

Defined the enum STRING_CONCAT

```
STRING_CONCAT(
    TypeConditional("string"),
    StringEqualConditional("+"),
    TypeConditional("string")
)
```

Here, we specify the types needed to preform string concatenation. A `TypeConditional` only passes value values with that type.
A `StringEqualConditional` only passes string values that equal the string.

STRING_CONCAT will now only be run when there is a string, the raw string "+", and another string in that order,
somewhere in an expression.

```kotlin
{
    override fun process(segments: List<Segment>): Expression {
    return Expression(Type("string")) { Value(
    segments[0].expression.get().value as String +
    segments[2].expression.get().value as String) }
}
```

This overrides the method `process`, which takes a list of segments. They are **guarantied** to be the correct type, so casts are
okay (and needed).
 
This specific method has to return an Expression. And not just any expression, it needs to be a callback, which means it won't be executed until compile time

The amount of sketchy code to make this work is surprisingly little

it's horrible though

I hate it and love it at the same time

it's so bad but so short