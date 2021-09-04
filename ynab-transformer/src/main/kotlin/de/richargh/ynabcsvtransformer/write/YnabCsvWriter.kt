package de.richargh.ynabcsvtransformer.write

import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.read.WriteConfig
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.Writer

class YnabCsvWriter {

    fun mapTransactions(transactions: Sequence<Transaction>, writeConfig: WriteConfig, writer: Writer) {
        val csvPrinter = CSVPrinter(writer,
                CSVFormat.DEFAULT
                        .withDelimiter(writeConfig.delimiter)
                        .withQuote('"')
                        .withHeader("Date", "Payee", "Category", "Memo", "Outflow", "Inflow"))

        transactions.forEach {
            csvPrinter.printRecord(
                    it.date,
                    it.beneficiary,
                    it.category?.rawValue ?: "", "Google",
                    it.description.rawValue,
                    it.outFlow,
                    it.inFlow)
        }
        csvPrinter.flush()
    }
}