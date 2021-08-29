package de.richargh.ynabcsvtransformer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.richargh.ynabcsvtransformer.config.CsvConfigDto
import de.richargh.ynabcsvtransformer.export.YnabCsvExporter
import de.richargh.ynabcsvtransformer.input.CsvConfig
import de.richargh.ynabcsvtransformer.input.CsvImporter
import de.richargh.ynabcsvtransformer.input.CsvHeaders
import de.richargh.ynabcsvtransformer.input.DomainName
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File
import java.time.format.DateTimeFormatter
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

        val csvConfig = csvConfig()
        val importer = CsvImporter()
        val exporter = YnabCsvExporter()
        val results = importer.mapTransactions(csv.inputStream(), csvConfig)
        exporter.mapTransactions(results)
        return 0
    }

    private fun csvConfig(): CsvConfig {
        val mapper = ObjectMapper().registerModule(KotlinModule())
        val csvConfigDto = mapper.readValue<CsvConfigDto>(config)
        return CsvConfig(
                DateTimeFormatter.ofPattern(csvConfigDto.dateTimePattern),
                CsvHeaders.of(
                        DomainName.BookingDate to csvConfigDto.header.bookingDate,
                        DomainName.Beneficiary to csvConfigDto.header.beneficiary,
                        DomainName.Description to csvConfigDto.header.description,
                        DomainName.Outflow to csvConfigDto.header.outflow))
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