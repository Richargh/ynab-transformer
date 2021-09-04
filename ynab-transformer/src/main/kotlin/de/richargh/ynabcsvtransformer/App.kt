package de.richargh.ynabcsvtransformer

import de.richargh.ynabcsvtransformer.config.ConfigReader
import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.export.YnabCsvWriter
import de.richargh.ynabcsvtransformer.input.CsvConfig
import de.richargh.ynabcsvtransformer.input.CsvReader
import de.richargh.ynabcsvtransformer.result.Res
import java.io.InputStream

class App {

    private val configReader = ConfigReader()
    private val importer = CsvReader()
    private val exporter = YnabCsvWriter()

    fun readConfig(config: InputStream): Res<CsvConfig>{
        return configReader.csvConfig(config)
    }

    fun transform(csv: InputStream, config: CsvConfig){
        val results = importer.mapTransactions(csv, config)
                .map { transform(it, config) }
        exporter.mapTransactions(results)
    }

    private fun transform(transaction: Transaction, csvConfig: CsvConfig): Transaction {
        val mapping = csvConfig.mappingWith(transaction.beneficiary, transaction.description)
        return transaction.withMapping(mapping)
    }

}