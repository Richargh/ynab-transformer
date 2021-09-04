package de.richargh.ynabcsvtransformer

import de.richargh.ynabcsvtransformer.config.ConfigReader
import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.write.YnabCsvWriter
import de.richargh.ynabcsvtransformer.read.CsvConfig
import de.richargh.ynabcsvtransformer.read.CsvReader
import de.richargh.ynabcsvtransformer.read.Mappings
import de.richargh.ynabcsvtransformer.lang.Res
import java.io.InputStream
import java.io.Writer

class App {

    private val configReader = ConfigReader()
    private val reader = CsvReader()
    private val csvWriter = YnabCsvWriter()

    fun readConfig(config: InputStream): Res<CsvConfig>{
        return configReader.csvConfig(config)
    }

    fun transform(csv: InputStream, config: CsvConfig, writer: Writer){
        val results = reader.read(csv, config.read)
                .map { transform(it, config.mappings) }
        csvWriter.write(results, config.write, writer)
    }

    private fun transform(transaction: Transaction, mappings: Mappings): Transaction {
        val mapping = mappings.mappingWith(transaction.beneficiary, transaction.description)
        return transaction.withMapping(mapping)
    }

}