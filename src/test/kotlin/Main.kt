import picocli.CommandLine
import systems.carson.compilers.comp030.Main

fun main() {
    CommandLine.run(Main(),*"-f ./res/bug#1.gs".split(" ").toTypedArray())
}