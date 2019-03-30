package systems.carson.compilers.comp030

import java.io.File

object Folder {
    val directory : File by lazy {
        when{
            os.toLowerCase().contains("windows") -> File("${System.getenv("APPDATA")}\\Gumboscript")
            os.toLowerCase().contains("unix") || os.toLowerCase().contains("linux")  -> File("~/.gumbo")
            else -> error("Unknown operating system $os")
        }
    }
    private val os by lazy { System.getProperty("os.name") }
}

fun main() {
    println(Folder.directory)
}