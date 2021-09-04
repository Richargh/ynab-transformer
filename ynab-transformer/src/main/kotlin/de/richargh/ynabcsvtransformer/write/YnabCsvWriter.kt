package de.richargh.ynabcsvtransformer.write

import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.read.WriteConfig
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.Writer
import java.text.DecimalFormat

class YnabCsvWriter {

    fun write(transactions: Sequence<Transaction>, writeConfig: WriteConfig, writer: Writer) {
        val csvPrinter = CSVPrinter(writer,
                CSVFormat.DEFAULT
                        .withDelimiter(writeConfig.delimiter)
                        .withQuote('"')
                        .withHeader("Date", "Payee", "Category", "Memo", "Outflow", "Inflow"))

        val numberFormat = DecimalFormat.getInstance(writeConfig.locale) as DecimalFormat

        transactions.forEach {
            csvPrinter.printRecord(
                    it.date,
                    it.beneficiary,
                    it.category?.rawValue ?: "",
                    it.description.rawValue,
                    numberFormat.format(it.outFlow),
                    numberFormat.format(it.inFlow))
        }
        csvPrinter.flush()
    }
}