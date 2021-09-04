package de.richargh.ynabcsvtransformer

import de.richargh.ynabcsvtransformer.config.ConfigReader
import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.export.YnabCsvWriter
import de.richargh.ynabcsvtransformer.input.CsvConfig
import de.richargh.ynabcsvtransformer.input.CsvReader
import de.richargh.ynabcsvtransformer.input.Mappings
import de.richargh.ynabcsvtransformer.result.Res
import java.io.InputStream

class App {

    private val configReader = ConfigReader()
    private val reader = CsvReader()
    private val writer = YnabCsvWriter()

    fun readConfig(config: InputStream): Res<CsvConfig>{
        return configReader.csvConfig(config)
    }

    fun transform(csv: InputStream, config: CsvConfig){
        val results = reader.mapTransactions(csv, config)
                .map { transform(it, config.mappings) }
        writer.mapTransactions(results)
    }

    private fun transform(transaction: Transaction, mappings: Mappings): Transaction {
        val mapping = mappings.mappingWith(transaction.beneficiary, transaction.description)
        return transaction.withMapping(mapping)
    }

}