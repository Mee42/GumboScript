---
layout: page
title: Tutorial
permalink: /tutorial/
---

Gumboscript is a simi-compiled, multiplatform programming language.
It is extremely simple, so learning it is very easy, 
even for people completely new to programming.

Lets start with a simple project, and then talk about how it works.

### Your first project: Hello World

*I recommend making a folder to keep your Gumboscript files*

Make a file called `HelloWorld.gs`. 
The `.gs` ending signifies that it is a Gumboscript file, 
and the `HelloWorld` is just the name.

Open it in your favorite text editor and add the following text:

```
#compiler 0.3.0
var hello equals "Hello, World!"
print hello
exit 0
```

Open command prompt and navigate to the directory with your file (`cd`,remember).
```
$ gumbo -f HelloWorld.gs
```
*The `$` signifies that this is in a command prompt.
You should type everything after it, and then press `Enter`.
Everything else is the output from the compiler/program.
I'll use this sintax later in this tutorial, so remember that.*

This gives this output:
```
Hello, World!
```

Congrats, you've just run your first Gumboscript project!


#### But what did I just do?

Running Gumboscript projects has two parts - compilation and running.
Compilation is basiclly reading over the code and making sure it's good
This also transforms it into machine-readable, in-memory code, Which speeds up the second stage: running

Running is the time that your code actually gets *executed*, it's as simple as that.

There should be **very little** errors during runtime, because most of them will be caught by the compiler.

If you want to just compile a program, you can do this:
```
$ gumbo -c -f HelloWorld.gs
```
This should have no output, and that's a good thing.
Putting the `-c` flag means that gumbo will only compile it, and not run it.
If it did have errors, it would print them out.

#### That's great and all, but what did I write?

Lets go through this line-by-line:

```
#compiler 0.3.0
```
This specifies that the compiler is version `0.3.0`.
This is pretty useless, as the `gumbo` command line program doesn't support any other compiler,
and will default to `0.3.0`.

Any other config options can also be put here, at the top, in this format:
```
#name-of-the-option value-of-the-option
```
There aren't any options yet though, so you don't have to worry about this

<br />
```
var hello equals "Hello, World!"
```
This line sets the variable named `hello` to the value `"Hello, World!`.
The variable `hello` can be used in any subsequent code as a `string` value.
The `hello` variable can be reassigned with the following statement:
```
hello equals "Goodbye"
```
However you **must** match the type.
If `hello` is a `string` when declared, it is a `string` for the entirety of your project

*Note: There is an exception to this rule, but it's irrelevant for now*

<br />
```
print hello
```
`print` simply prints whatever value it gets.
In this case, because `hello` is a string, it will print the raw string value, which is `Hello, World!`

<br />
```
exit 0
```
`exit` exits with the code it is given.
Codes must be between 0 and 127.
0 normally means "success" where every other number means there was an error.
Use this how you will - the compiler will automatically return 0 when the end is reached,
so there is no need to include it like we did in this project. It would work the exact same
without it


### The different types of variables
* `int` - An integer value, between `-2,147,483,648` and `2,147,483,647`
Specified by a number, ie `3`

* `boolean` - A true/false value.
Specified by the word `true` or `false`

* `string` - A set of characters in a specified order.
There is limited support in Gumboscript for concatenation (combining two strings)
but no functionality for splitting or mutating them.
Specified by characters inside quotes, ie `"Hello"`

Types are **not specified** for variables, but are assumed and assigned at compile time.
This means that actions like:
```
var x equals 10
x equals true
```
Will not compile - you can't assign a variable of type `int` to a value of type `boolean`

### Code flow
Writing code that can only be executed once isn't very useful.
Conditional statements make code much more powerful.

#### If statement

If statements are what they sound like: They'll only run ***if*** something is true.

Here's a short example:
```
if true
{
    print "hello"
}

if false
{
    print "world"
}
```
Will output:
```
hello
```
The second if statement will be skipped - the value was false, after all.

#### While statement

While statements are also somewhat self-explanatory: They execute code ***while*** a specific boolean expression is true

For example, the following code will run forever and never execute:
```
while true
{
    print "hello"
}
```

*This is a good time to let you know that `Ctrl` + `C` will kill processes running in command prompt*

This will print "hello" once:
```
var one equals true
while one
{
    print "hello"
    one equals false
}
```

And this will print it ten times:
```
var x equals 0
while x < 10
{
    print "hello"
    x equals x + 1
}
```
This uses something called *expressions*, which we'll cover in the next section.

#### Variable scope

This is a relatively simple concept - variables only exist in the scope that they are declared

for example:
```
if true
{
    var one equals 1
    if true {
        //variable one exists and can be used
        var two equals one
    }
    //variable one exists, but variable two is no longer in scope
    //**the next line is invalid**
    var three equals two
}
```
You could also say that variables only exist in the block they are declared,
and also in blocks that are inside that block.
This means that types of variables can change between scope:
```
if true
{
    var one equals "string"
}
if true
{
    var one equals true
}
```
This is perfectly valid.

Make sure you don't do something like this, because it **will not work**

```
if true
{
    var one equals "one"
}
print one
```
`one` is **not in scope** on the 5th line.


### Expressions

Expressions are tiny pieces of code that evaluate to a single value.
We've actually been using them from the beginning of this tutorial!
```
var x equals 10
```
The `10` is actually a very simple expression that evaluates to....`10`!

However, this lets us do more powerful things.
```
var x equals 5 + 5
```
`5 + 5` will be executed at runtime and assign the variable `x` the value of `10`.
At compile time, however, `x` is still assigned the type of `int`.
The compiler knows that `5 + 5` is actually two values of type `int` separated by a `+`.
Then, it matches the three things together into one `int` expression - an expression that evaluates to type `int`

This can be combined, for example
```
var x equals 5 + 5 * 10
```
will execute `5 * 10` and then add `5`

The order of preference is this. Types are represented in parentheses, `[int]` means a value (or expression) of type int
- Not            `! [boolean]`
- MoD            `[int] % [int]`
- Multiplication `[int] * [int]`
- Division       `[int] / [int]`
- Addition       `[int] + [int]`
- Subtraction    `[int] - [int]`
- And            `[boolean] && [boolean]`
- Or             `[boolean] || [boolean]`
- String concatenation
  - `[string] + [string]`
  - `[string] + [int]`
  - `[string] + [boolean]`
- Equals
  - `[string] == [string]`
  - `[int] == [int]`
  - `[boolean] == [boolean]`
- Not Equals
  - `[string] != [string]`
  - `[int] != [int]`
  - `[boolean] != [boolean]`
- Comparisons
  - Less then  `[int] < [int]`
  - Less then or equal to  `[int] <= [int]`
  - Greater then  `[int] > [int]`
  - Less then or equal too`[int] >= [int]`

You should know that this is a strict ordering.
Addition will happen before subtraction, especially in cases like `1 - 2 + 3`

The names of variables can be used in expressions, as we've seen.
For example, this is perfectly valid:
```
var x equals 10
var y equals x + 10
```
The word `x` will evaluate to an int expression and be read at runtime for it's value.


