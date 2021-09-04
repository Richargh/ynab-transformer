package de.richargh.ynabcsvtransformer.read

import de.richargh.ynabcsvtransformer.domain.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.InputStream
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeParseException


class CsvReader {

    private var indexOf: Map<DomainName, Int> = emptyMap()
    private val foundHeader: Boolean get() = indexOf.isNotEmpty()

    fun read(inputStream: InputStream, config: ReadConfig): Sequence<Transaction> {
        val csvParser = CSVFormat.DEFAULT
                .withDelimiter(config.delimiter)
                .withQuote('"')
        return sequence {
            val records: CSVParser = csvParser.parse(InputStreamReader(inputStream))
            records.forEachIndexed { index, record ->
                tryMap(index, record, config)?.let { yield(it) }
            }
        }
    }

    private fun tryMap(index: Int, csvRecord: CSVRecord, config: ReadConfig): Transaction? = try {
        when {
            foundHeader -> extractTransaction(csvRecord, config)
            matchColumnHeaders(csvRecord, config.headers) -> null
            else -> null
        }
    } catch (e: Exception) {
        null
    }

    private fun extractTransaction(csvRecord: CSVRecord, config: ReadConfig): Transaction? {
        val rawBeneficiary = csvRecord.get(indexOf[DomainName.Beneficiary]!!)
        val rawDescription = csvRecord.get(indexOf[DomainName.Description]!!)
        if(rawBeneficiary.isNullOrBlank())
            return null

        val dateString = csvRecord.get(indexOf[DomainName.BookingDate]!!)
        val date = try {
            LocalDate.parse(dateString, config.dateFormatter)
        } catch (ex: DateTimeParseException){
            System.err.println("Could not parse $dateString. Is the pattern correct?")
            throw ex
        }

        val (inFlow, outFlow) = if(indexOf.containsKey(DomainName.MoneyFlow.InOutFlow.InFlow)){
            Pair(
                    csvRecord.numberOrNull(DomainName.MoneyFlow.InOutFlow.InFlow, config)?.abs() ?: ZERO,
                    csvRecord.numberOrNull(DomainName.MoneyFlow.InOutFlow.OutFlow, config)?.abs() ?: ZERO)
        } else if(indexOf.containsKey(DomainName.MoneyFlow.PlusMinusFlow.Flow)){
            val flow = csvRecord.numberOrNull(DomainName.MoneyFlow.PlusMinusFlow.Flow, config)
            if(flow == null)
                Pair(ZERO, ZERO)
            else
                Pair(
                    if(flow < ZERO) ZERO else flow.abs(),
                    if(flow < ZERO) flow.abs() else ZERO)
        }  else if(indexOf.containsKey(DomainName.MoneyFlow.MarkerFlow.Flow)){
            val flow = csvRecord.numberOrNull(DomainName.MoneyFlow.MarkerFlow.Flow, config)?.abs()
            // FIXME this line is slow because it is repeated so often.
            //  Is there some way to get both key and value from a hashmap directly?
            val m = indexOf.keys.first { it == DomainName.MoneyFlow.MarkerFlow.Marker.any() } as DomainName.MoneyFlow.MarkerFlow.Marker
            val marker = csvRecord.get(indexOf[m]!!).trim()

            if(flow == null)
                Pair(ZERO, ZERO)
            else
                Pair(
                        if(marker == m.inFlowMarker) flow else ZERO,
                        if(marker == m.outFlowMarker) flow else ZERO)
        } else {
            Pair(ZERO, ZERO)
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

    private fun CSVRecord.numberOrNull(name: DomainName, config: ReadConfig): BigDecimal? {
        val raw = get(indexOf[name]!!)
        if(raw.isNullOrEmpty())
            return null

        val numberFormat = DecimalFormat.getInstance(config.locale) as DecimalFormat
        numberFormat.isParseBigDecimal = true

        return numberFormat.parse(raw) as BigDecimal
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
