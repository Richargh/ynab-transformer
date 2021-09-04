package de.richargh.ynabcsvtransformer

import de.richargh.ynabcsvtransformer.lang.Res
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(name = "checksum", mixinStandardHelpOptions = true, version = ["checksum 4.0"],
        description = ["Prints the checksum (MD5 by default) of a file to STDOUT."])
class Cli : Callable<Int> {

    @Parameters(index = "0", description = ["The csv file to transform."])
    lateinit var csv: File

    @Parameters(index = "1", description = ["The config file that describes how to transform the csv."])
    lateinit var config: File

    @Option(names = ["-o", "--out"], description = ["Output file (default: print to console)"])
    private val outputFile: File? = null

    override fun call(): Int {
        val filesExist = ensureFilesExist()
        if(filesExist != 0)
            return filesExist

        val app = App()

        val configResult = app.readConfig(config.inputStream())
        if(configResult is Res.Fail){
            println("Config is not correct. Problems: ${configResult.messages}")
            return 1
        }
        (outputFile?.writer() ?: System.out.writer()).use { outputWriter ->
            app.transform(csv.inputStream(), (configResult as Res.Ok).value, outputWriter)
        }

        return 0
    }

    private fun ensureFilesExist(): Int {
        if(!csv.exists()){
            println("${Cli::csv.name}=${csv.absolutePath} does not exist.")
            return 1
        }
        if(!config.exists()){
            println("${Cli::config.name}=${config.absolutePath} does not exist.")
            return 1
        }

        return 0
    }
}

fun main(args: Array<String>) : Unit = exitProcess(CommandLine(Cli()).execute(*args))