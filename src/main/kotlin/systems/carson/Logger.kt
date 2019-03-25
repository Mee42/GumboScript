package systems.carson

enum class Degree{
    ERROR,
    VERBOSE,
    NORMAL,
    PROGRAM_OUTPUT
}

object Logger{
    var verbose = true
    fun log(s :String,level : Degree){
        when(level){
            Degree.ERROR -> System.err.println(s)
            Degree.NORMAL -> println(s)
            Degree.VERBOSE -> { if(verbose) println(s) }
            Degree.PROGRAM_OUTPUT -> println(s)
        }
    }
    fun normal(s :String) = log(s,Degree.NORMAL)
    fun verbose(s :String) = log(s,Degree.VERBOSE)
    fun error(s :String) = log(s,Degree.ERROR)
    fun output(s :String) = log(s,Degree.PROGRAM_OUTPUT)
}