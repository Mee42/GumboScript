package systems.carson.compilers.comp030

import systems.carson.Logger
import java.util.regex.Pattern


class Segment(private var xstring :String? = null,private val xexpression : Expression? = null){
    val string :String
        get() = xstring!!
    val expression : Expression
        get() = xexpression!!

    fun isString() = xstring != null
    fun isExpression() = xexpression != null
    override fun toString(): String {
        return if(xstring != null)
            "Segment(\"$xstring\")"
        else
            "Segment { $xexpression }"
    }

}

const val symbols = "_"
const val lowercase = "abcdefghijklmnopqrstuvwxyz"
const val numbers = "0123456789"
val letters = lowercase + lowercase.toUpperCase()
val all = letters + numbers + symbols

fun isValidIdentifier(string :String):Boolean{
//    println("string:$string  isBlank:${string.isNotBlank()} " +
//            " second: ${letters.contains(string[0])} " +
//            " third:"  + string.all { character -> all.contains(character) })
    return string.isNotBlank() &&
            letters.contains(string[0]) &&
            string.all { character -> all.contains(character) }
}

fun getVariableNames(list :List<Segment>,variables :Map<String,Type>, line :Int = -1) :List<Segment>{
    return list.map {
        if(it.isString()){
            if(isValidIdentifier(it.string)){
                val type = variables[it.string] ?: error("Unknown variable ${it.string} on line $line")
                Segment(xexpression = VariableGetExpression(type, it.string))
            }else{
                it
            }
        }else{
            it
        }
    }
}


fun compileExpression(s :String, variables :Map<String,Type>, line :Int = -1):Expression{
    val str = s.trim()
    val segments = match(getVariableNames(getSegments(str,variables,line),variables,line))
    Logger.verbose("Compiling expression; str:\"$s\"  segments: $segments")
    when{
        segments.isEmpty() -> error("no expression returned \"$s\" on line $line")
        segments.size != 1 -> error("more then one segment returned on line $line $segments")
        !segments[0].isExpression() -> error("expression returns a string, not a value(string) on line $line \"$s\" ($segments)")
        else -> return segments[0].expression
    }
}

private fun match(list :List<Segment>):List<Segment>{
    Logger.verbose("matching $list")
    if(list.isEmpty() || list.size == 1)return list

    for(matcher in Matchers.values()){
        Logger.verbose("testing matcher $matcher")
        for(i in 0 until list.size - matcher.args.size + 1){
            val sublist = list.subList(i,i + matcher.args.size)
            Logger.verbose("\t testing sublist $sublist")
            if(matcher.matches(sublist)){
                //it works!
                Logger.verbose("\t\tMatched!")
                return match(list.subList(0,i) +
                        listOf(Segment(xexpression = matcher.process(sublist))) +
                        list.subList(i + matcher.args.size,list.size))
            }
        }
    }
    return list
}

private fun getSegments(s :String, variables :Map<String,Type>, line :Int = -1):List<Segment>{
    val list = mutableListOf<Segment>()
    var cache = ""
    var on = 0
    fun popCache(){
        if(cache.isNotBlank()){
            cache.trim().split(Pattern.compile("""[\s]+"""))
                .map { it.trim() }
                .filter { !it.isBlank() }
                .forEach { list.add(Segment(xstring = it)) }
            cache = ""
        }
    }


    while(on < s.length){
        when {
            s[on] == ':' &&
                    on + 1 < s.length &&
                    s[on + 1] == '[' -> {
                popCache()
                val end = s.indexOf(']', on)
                if (end == -1) error("Unable to find closing [ of :[ on line $line")
                val name = s.substring(on + 2, end)
                val type = variables[name] ?: error("Unknown variable $name on line $line")
                val exp = VariableGetExpression(type, name)
                list.add(Segment(xexpression = exp))
                on = end + 1
            }
            // _-_true-_-
            // 0123456789
            //    |
            //    on
            on + 3 < s.length &&
                    s.substring(on,on + 4) == "true" -> {
                popCache()
                list.add(Segment(xexpression = Expression.forValue(true)))
                on += 5
            }

            // _-_false-_-
            // 0123456789
            //    |
            //    on
            on + 4 < s.length &&
                    s.substring(on,on + 5) == "false" -> {
                popCache()
                list.add(Segment(xexpression = Expression.forValue(false)))
                on += 6
            }
            s[on] == '"' -> {
                popCache()
                val end = s.indexOf("\"",on + 1)
                if(end == -1) error("Unable to find closing \" on line $line")
                val content = s.substring(on + 1,end)
                val exp = Expression.forValue(content)
                list.add(Segment(xexpression = exp))
                on = end + 1
            }
            "0123456789".contains(s[on]) -> {
                popCache()
                var onn = on
                //while the next one isn't a line. Note: consecutive integers work with this :thonk:

                while (onn < s.length && "0123456789".contains(s[onn])) onn++
//                    onn--
                val int = s.substring(on, onn).toInt()
                list.add(Segment(xexpression = Expression.forValue(int)))
                on = onn

            }
            s[on] == '(' -> {
                popCache()
                //collect the string until the ) is reached
                var nested = 0
                var end = on + 1
                val builder = StringBuilder()
                while(true){
                    if(end >= s.length)
                        error("Reached end of expression while looking for ending ) on line $line")
                    if(s[end] == ')'){
                        if(nested == 0){
                            break
                        }
                        nested--
                    }
                    if(s[end] == '('){
                        nested++
                    }
                    builder.append(s[end])
                    end++
                }
                list.add(Segment(xexpression = compileExpression(builder.toString(),variables,line)))
                on = end + 1
            }
            else -> {
                cache += s[on]
                on ++
            }
        }
    }
    popCache()
    Logger.verbose("parsed segments: $list on line $line")
    return list
}