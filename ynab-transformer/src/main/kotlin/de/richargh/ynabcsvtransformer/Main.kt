package de.richargh.ynabcsvtransformer

import de.richargh.ynabcsvtransformer.config.ConfigReader
import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.export.YnabCsvWriter
import de.richargh.ynabcsvtransformer.input.CsvConfig
import de.richargh.ynabcsvtransformer.input.CsvReader
import de.richargh.ynabcsvtransformer.result.Res
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(name = "checksum", mixinStandardHelpOptions = true, version = ["checksum 4.0"],
        description = ["Prints the checksum (MD5 by default) of a file to STDOUT."])
class Checksum : Callable<Int> {

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

        val configReader = ConfigReader()
        val importer = CsvReader()
        val exporter = YnabCsvWriter()

        val configResult = configReader.csvConfig(config.inputStream())
        if(configResult is Res.Fail){
            println("Config is not correct. Problems: ${configResult.messages}")
            return 1
        }


        val results = importer.mapTransactions(csv.inputStream(), (configResult as Res.Ok).value)
                .map { transform(it, configResult.value) }
        exporter.mapTransactions(results)
        return 0
    }

    private fun transform(transaction: Transaction, csvConfig: CsvConfig): Transaction{
        val mapping = csvConfig.mappingWith(transaction.beneficiary, transaction.description)
        return transaction.withMapping(mapping)
    }

    private fun ensureFilesExist(): Int {
        if(!csv.exists()){
            println("${Checksum::csv.name}=${csv.absolutePath} does not exist.")
            return 1
        }
        if(!config.exists()){
            println("${Checksum::config.name}=${config.absolutePath} does not exist.")
            return 1
        }
        if(outputFile != null && !outputFile.exists()){
            println("${Checksum::outputFile.name}=${outputFile.absolutePath} does not exist.")
            return 1
        }

        return 0
    }
}

fun main(args: Array<String>) : Unit = exitProcess(CommandLine(Checksum()).execute(*args))