package systems.carson


interface GsCompiler{
    val version :String
    fun run(input: String, conf :Map<String,List<String>>): Int
}