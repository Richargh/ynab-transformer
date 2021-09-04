package de.richargh.ynabcsvtransformer.write

import de.richargh.ynabcsvtransformer.domain.Transaction
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.Writer

class YnabCsvWriter {

    fun mapTransactions(transactions: Sequence<Transaction>, writer: Writer) {
        val csvPrinter = CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader("Date", "Payee", "Category", "Memo", "Outflow", "Inflow"))

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