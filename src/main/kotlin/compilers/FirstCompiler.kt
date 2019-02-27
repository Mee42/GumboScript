package compilers

import GsCompiler
import java.util.*
import java.util.regex.Pattern


private val mapRegex :Pattern = Pattern.compile(":\\[[0-9\\-]+\\]")

class FirstCompiler : GsCompiler {
    override val version: String
        get() = "0.0.1"

    private lateinit var lines :List<Pair<String,Int>>
    private var currentLine = 0
    private val currentLineStr :String
        get() = "on line ${lines[currentLine].second} (${lines[currentLine].first})"
    private val stack = Stack<Int>()
    private val gotos = mutableMapOf<String,Int>()
    private val ints = mutableMapOf<Int,Int>()
    private val stringBuffer = mutableListOf<Int>()

    override fun run(input: String, conf :Map<String,List<String>>): Int {
       lines = input.split("\n")
            .mapIndexed { index, s -> Pair(s,index + 1) }
            .flatMap { pair -> pair.first.split(";").filter { w -> w != "" }.map { w -> Pair(w.trim(),pair.second) } }
        val linesWithRealLineNumbers = lines.mapIndexed { index, pair -> Pair(pair.first,index) }


        while(true) {
            val lineX :Pair<String,Int>? = lines[currentLine]
            if(lineX == null){
                System.err.println("Unable to find line $currentLine")
                System.err.println("Make sure you have an exit statement")
                System.exit(1)
            }
            var line :String = lineX!!.first
            if(line.startsWith("//")){
                currentLine++
                continue
            }

            while(line.contains(":POP")){
                line = line.replaceFirst(":POP",pop().toString())
            }
            while(line.contains(":PEEK")){
                line = line.replaceFirst(":PEEK",peek().toString())
            }
            while(line.contains(":SIZE")){
                line = line.replaceFirst(":SIZE","" + stack.size)
            }



            while(line.contains(mapRegex.toRegex())) {
                val matcher = mapRegex.matcher(line)
                matcher.find()
                line = line.substring(0,matcher.start()) +
                        getMap(Integer.parseInt(line.substring(matcher.start() + 2,matcher.end()-1))) +
                        line.substring(matcher.end())
            }

            when{
                line.startsWith("exit") -> {
                    val number = getNumber(line,"exit")
                    System.exit(number)
                }
                line.startsWith("push") -> {
                    val number = getNumber(line,"push")
                    push(number)
                }
                line.startsWith("map") -> {
                    val number = getNumber(line,"map")
                    ints[number] = pop()
                }
                line.startsWith("buff") -> {
                    val value = getNumber(line,"buff")
                    stringBuffer.add(value)
                }
                line.startsWith("strbuff") -> {
                    val value = line.replaceFirst("strbuff","").trim()
                    for(i in value){
                        stringBuffer.add(i.toInt())
                    }
                }
                line.startsWith("printbuff") -> {
                    print(stringBuffer.map { it.toChar() }.fold("") { a,b -> "$a$b" })
                    stringBuffer.clear()
                }
                line == "clearbuff" -> {
                    stringBuffer.clear()
                }
                line.startsWith("println") -> {
                    val number = getNumber(line,"println")
                    println(number)
                }
                line.startsWith("print") -> {
                    val number = getNumber(line,"print")
                    print(number)
                }
                line == "ln" -> println()
                line == "wait" -> Thread.sleep(1000)
                //math
                line == "add" -> push(pop() + pop())
                line == "div" -> {
                    val x = pop()
                    val y = pop()
                    push(y / x)
                }
                line == "sub" -> {
                    val x = pop()
                    val y = pop()
                    push(y - x)
                }
                line == "mult" -> push(pop() * pop())

                //stuff with goto
                line.startsWith("setgoto") -> {
                    val key = line.replaceFirst("setgoto","").trim()
                    gotos[key] = currentLine
                }
                line.startsWith("pgoto") -> {
                    val key = line.replaceFirst("pgoto","").trim()
                    for(lineE in linesWithRealLineNumbers){
                        if(lineE.first.trim() == "setgoto $key"){
                            gotos[key] = lineE.second
                            break
                        }
                    }
                }

                line.startsWith("gotoif") -> {
                    //if the top of the stack is 0, goto
                    //pops the stack
                    if(pop() <= 0) {
                        val key = line.replaceFirst("gotoif", "").trim()
                        if (!gotos.containsKey(key)) {
                            System.err.println("Can not find goto key \"$key\" $currentLineStr")
                            System.exit(5)
                        }
                        currentLine = gotos[key]!!
                    }
                }
                line.startsWith("goto") -> {
                    val key = line.replaceFirst("goto","").trim()
                    if(!gotos.containsKey(key)){
                        System.err.println("Can not find goto key \"$key\" $currentLineStr")
                        System.exit(5)
                    }
                    currentLine = gotos[key]!!
                }
                line == "dupl" -> {
                    val x = pop()
                    push(x)
                    push(x)
                }
                line == "swap" -> {
                    val x = pop()
                    val y = pop()
                    push(x)
                    push(y)
                }
                line.startsWith("consume") -> {}
                line == "reverse" -> {
                    val size = stack.size
                    val reverseArray = IntArray(size)
                    for (i in 0 until size) {
                        reverseArray[i] = stack.pop()
                    }
                    for(i in 0 until size){
                        stack.push(reverseArray[i])
                    }
                }
                line == "input" -> {
                    System.out.print(">")
                    val x = Scanner(System.`in`).nextLine().toIntOrNull() ?: {
                        System.err.println("Can not parse int. $currentLineStr")
                        System.exit(1)
                        0
                    }()
                    push(x)
                }
                line == "debug" -> println("$currentLineStr : $stack")
                else -> {
                    System.err.println("Unknown keyword $currentLineStr")
                }
            }


            currentLine++
        }
    }

    fun pop():Int{
        if(stack.empty()){
            System.err.println("Can not pop empty stack $currentLineStr")
            System.exit(4)
        }
        return stack.pop()
    }
    fun peek():Int{
        if(stack.empty()){
            System.err.println("Can not peek empty stack $currentLineStr")
            System.exit(4)
        }
        return stack.peek()
    }

    fun push(i: Int) {
        stack.push(i)
    }

    fun getNumber(line: String, replace: String): Int {
        val x = line.replaceFirst(replace,"").trim().toIntOrNull()
        if(x == null){
            System.err.println("Unable to parse int \"${line.replaceFirst(replace,"").trim()}\" $currentLineStr")
            System.exit(2)
        }
        return x!!
    }

    fun getMap(i :Int):Int{
        val x = ints[i]
        if(x == null){
            System.err.println("No known int $i in the map $currentLineStr")
            System.exit(3)
        }
        return x!!
    }

}