package de.richargh.ynabcsvtransformer.input

import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.domain.beneficiary
import de.richargh.ynabcsvtransformer.domain.description
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.InputStream
import java.io.InputStreamReader
import java.security.InvalidParameterException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CsvReader {

    private var indexOf: Map<DomainName, Int> = emptyMap()
    private val foundHeader: Boolean get() = indexOf.isNotEmpty()

    fun mapTransactions(inputStream: InputStream, config: CsvConfig): Sequence<Transaction> {
        val csvParser = CSVFormat.DEFAULT
                .withDelimiter(';')
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
            matchColumnHeaders(csvRecord, config.headers) -> null
            else -> null
        }
    } catch (e: Exception) {
        null
    }

    private fun mapTransaction(csvRecord: CSVRecord, config: CsvConfig): Transaction? {
        val dateString = csvRecord.get(indexOf[DomainName.BookingDate]!!)

        val date = LocalDate.parse(dateString, config.dateFormatter)
        val rawBeneficiary = csvRecord.get(indexOf[DomainName.Beneficiary]!!)
        val rawDescription = csvRecord.get(indexOf[DomainName.Description]!!)
        if(rawBeneficiary.isNullOrBlank())
            return null

        val beneficiary = beneficiary(rawBeneficiary)
        val description = description(rawDescription)

        return Transaction(
                date,
                beneficiary,
                description
        )
    }

    private fun matchColumnHeaders(csvRecord: CSVRecord, headerMappings: CsvHeaders): Boolean {
        val indexOfMappableColumn = csvRecord.asSequence()
                .mapIndexed { index, value -> CsvColumn(value) to index }
                .filter { (column, index) -> column in headerMappings }
                .map { (column, index) -> headerMappings[column] to index }
                .toMap()

        val foundHeaders = indexOfMappableColumn.size == headerMappings.size
        if(foundHeaders)
            this.indexOf = indexOfMappableColumn
        return foundHeaders
    }

}

class CsvConfig(
        val dateFormatter: DateTimeFormatter,
        val headers: CsvHeaders,
        val mappings: List<Mapping>
) {
    fun mappingWith(beneficiary: Beneficiary, description: Description): Mapping? {
        return mappings.firstOrNull {
            (it.alias.beneficiary.isEmpty() || it.alias.beneficiary.contains(beneficiary))
            && (it.alias.description.isEmpty() || it.alias.description.contains(description))
        }
    }

}

class CsvHeaders private constructor(
        val nameOfColumn: Map<CsvColumn, DomainName>) {

    operator fun get(csvColumn: CsvColumn): DomainName = nameOfColumn[csvColumn]!!
    operator fun contains(csvColumn: CsvColumn) = nameOfColumn.containsKey(csvColumn)
    val size get() = nameOfColumn.size

    companion object {
        fun of(vararg domainNameToCsvColumn: Pair<DomainName, String>): CsvHeaders {
            val map = mutableMapOf<CsvColumn, DomainName>()
            domainNameToCsvColumn.forEach {
                val key = it.first
                val column = CsvColumn(it.second)
                map.put(column, key)
            }

            val missingDomainNames: Set<DomainName> = DomainName::class.sealedSubclasses.mapNotNull { it.objectInstance }.toSet() -
                    map.values.toSet()
            if (missingDomainNames.isNotEmpty())
                throw InvalidParameterException("All domain names must have a mapping. Missing are $missingDomainNames")
            return CsvHeaders(map)
        }
    }
}

data class Mapping(
        val category: Category,
        val beneficiary: Beneficiary,
        val alias: Alias
)

data class Alias(
    val beneficiary: Set<Beneficiary>,
    val description: Set<Description>,
    val outflow: Set<Outflow>
)

sealed class DomainName {
    object BookingDate : DomainName()
    object Beneficiary : DomainName()
    object Description : DomainName()
    object Outflow : DomainName()

    companion object {
        val objects get() = DomainName::class.sealedSubclasses.mapNotNull { it.objectInstance }.toSet()
        fun of(rawValue: String): DomainName {
            return DomainName::class.sealedSubclasses.singleOrNull { it.simpleName == rawValue }?.objectInstance
                    ?: throw RuntimeException("The key $rawValue does not exist in the domain.")
        }
    }
}

data class CsvColumn(val rawValue: String)


data class BookingDate(val rawValue: String)
data class Beneficiary(val rawValue: String)
data class Description(val rawValue: String)
data class Outflow(val rawValue: String)

data class Category(val rawValue: String)