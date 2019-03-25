package systems.carson.compilers.comp030

import picocli.CommandLine
import picocli.CommandLine.*
import systems.carson.Logger
import systems.carson.Parser
import java.io.File
import java.lang.IllegalStateException


fun main(args: Array<String>) {
    CommandLine.run(Main(),*args)
}

@Command(name = "gumbo",
    mixinStandardHelpOptions = true)
class Main :Runnable {
    @Option(names = ["-v","--verbose"],
        description = ["Verbose mode. Useful for debugging"])
    var verbose :Boolean = false

    @Option(names = ["-f","--file"],
        description = ["The file to use"])
    var file : File? = null

    @Option(names = ["-c","--compile"],
        description = ["Compiles the given file into a .gsc file"])
    var compile :Boolean = false

    @Option(names = ["-r","--run"],
        description = ["Runs a given .gsc file"])
    var run :Boolean = false

    @Option(names = ["-g","--gumbo"],
        description = ["Compiles file into memory and runs it."])
    var gumboIsSpecified :Boolean = false
    var gumbo :Boolean = true

    @Option(names = ["--print-compiled"],
        description = ["Print the compiled structure after compile"])
    var printCompiled :Boolean = false

    @Option(names = ["--print-stacktrace"],
        description = ["Prints the kotlin stacktrace if compilation fails"])
    var printStackTrace :Boolean = false

    @Option(names = ["-p","--print-all"],
        description = ["Print everything: verbose, stacktrace, compiled structure, etc"])
    var printEverything :Boolean = false



    override fun run() {
        Logger.verbose = verbose
        try {
            if(printEverything){
                printStackTrace = true
                printCompiled = true
                verbose = true
                Logger.verbose = true
            }

            if(gumboIsSpecified)
                gumbo = true
            Logger.verbose("actualGumbo = $gumbo")
            if((run || compile) && !gumboIsSpecified) {
                Logger.verbose("Setting gumbo to false")
                gumbo = false
            }

            if(run && compile)
                error("You can not run and compile the same file")

            if((run && gumbo) ||(compile && gumbo))
                error("Can not ${if(run)"run" else "compile"} program and compile/run in memory at the same time")

            if(file == null)
                error("No file specified")
            val code = when {
                gumbo -> gumbo()
                run -> runCompiledFile()
                compile -> compile()
                else -> error("none of the operations (compile/run/gumbo) are specified")
            }
            System.exit(code)
        }catch(e :IllegalStateException){
            e.message?.let { Logger.error(it) }
            if(printStackTrace){
                System.err.println("\n\n\n")
                e.printStackTrace()
            }
            System.exit(-1)
        }
    }
    private fun compile() :Int{
        error("Gumboscript currently does not support compiling")//TODO
    }

    private fun runCompiledFile(): Int {
        error("Gumboscript currently does not support running from a compiled file")//TODO
    }


    private fun gumbo():Int{
        val pair = Parser.initalParse(file!!)
        val program = Compiler030().compile(pair.first.fold("") { a, b -> a + "\n" + b }, pair.second)
        if(printCompiled) println("\n   ---  structure  ---    \n\n" + program.main.toString() + "\n   ---  compiled  ---    ")
        return program.execute()
    }
}
