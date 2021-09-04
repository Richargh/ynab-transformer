package de.richargh.ynabcsvtransformer.read

import de.richargh.ynabcsvtransformer.domain.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlin.math.abs

class CsvReader {

    private var indexOf: Map<DomainName, Int> = emptyMap()
    private val foundHeader: Boolean get() = indexOf.isNotEmpty()

    fun mapTransactions(inputStream: InputStream, config: CsvConfig): Sequence<Transaction> {
        val csvParser = CSVFormat.DEFAULT
                .withDelimiter(config.read.delimiter)
                .withQuote('"')
        return sequence {
            val records: CSVParser = csvParser.parse(InputStreamReader(inputStream))
            records.forEachIndexed { index, record ->
                tryMap(index, record, config)?.let { yield(it) }
            }
        }
    }

    private fun tryMap(index: Int, csvRecord: CSVRecord, config: CsvConfig): Transaction? = try {
        when {
            foundHeader -> mapTransaction(csvRecord, config)
            matchColumnHeaders(csvRecord, config.read.headers) -> null
            else -> null
        }
    } catch (e: Exception) {
        null
    }

    private fun mapTransaction(csvRecord: CSVRecord, config: CsvConfig): Transaction? {
        val rawBeneficiary = csvRecord.get(indexOf[DomainName.Beneficiary]!!)
        val rawDescription = csvRecord.get(indexOf[DomainName.Description]!!)
        if(rawBeneficiary.isNullOrBlank())
            return null

        val dateString = csvRecord.get(indexOf[DomainName.BookingDate]!!)
        val date = try {
            LocalDate.parse(dateString, config.read.dateFormatter)
        } catch (ex: DateTimeParseException){
            System.err.println("Could not parse $dateString. Is the pattern correct?")
            throw ex
        }

        val (inFlow, outFlow) = if(indexOf.containsKey(DomainName.MoneyFlow.InOutFlow.InFlow)){
            Pair(
                    csvRecord.get(indexOf[DomainName.MoneyFlow.InOutFlow.InFlow]!!),
                    csvRecord.get(indexOf[DomainName.MoneyFlow.InOutFlow.OutFlow]!!))
        } else if(indexOf.containsKey(DomainName.MoneyFlow.PlusMinusFlow.Flow)){
            val flow = csvRecord.get(indexOf[DomainName.MoneyFlow.PlusMinusFlow.Flow]!!).toIntOrNull()
            if(flow == null)
                Pair("", "")
            else
                Pair(
                    if(flow < 0) "0" else "$flow",
                    if(flow < 0) "${abs(flow)}" else "0")
        }  else if(indexOf.containsKey(DomainName.MoneyFlow.MarkerFlow.Flow)){
            val flow = csvRecord.get(indexOf[DomainName.MoneyFlow.MarkerFlow.Flow]!!).toIntOrNull()
            // FIXME this line is slow because it is repeated so often.
            //  Is there some way to get both key and value from a hashmap directly?
            val m = indexOf.keys.first { it == DomainName.MoneyFlow.MarkerFlow.Marker.any() } as DomainName.MoneyFlow.MarkerFlow.Marker
            val marker = csvRecord.get(indexOf[m]!!).trim()

            if(flow == null)
                Pair("", "")
            else
                Pair(
                        if(marker == m.inFlowMarker) "$flow" else "0",
                        if(marker == m.outFlowMarker) "$flow" else "0")
        } else {
            Pair("", "")
        }

        val beneficiary = Beneficiary(rawBeneficiary)
        val description = Description(rawDescription)

        return Transaction(
                date,
                beneficiary,
                description,
                null,
                outFlow,
                inFlow
        )
    }

    private fun matchColumnHeaders(csvRecord: CSVRecord, headerMappings: CsvHeaders): Boolean {
        val indexOfMappableColumn: Map<DomainName, Int> = csvRecord.asSequence()
                .mapIndexed { index, value -> CsvColumn(value) to index }
                .filter { (column, index) -> column in headerMappings }
                .map { (column, index) -> headerMappings[column] to index }
                .toMap()
        if(0 < indexOfMappableColumn.size && indexOfMappableColumn.size < headerMappings.size){
            val missing = headerMappings.nameOfColumn.values - indexOfMappableColumn.keys
            System.err.println("Could not find columns for: $missing")
        }
        val foundHeaders = indexOfMappableColumn.size == headerMappings.size
        if(foundHeaders)
            this.indexOf = indexOfMappableColumn
        return foundHeaders
    }

}
