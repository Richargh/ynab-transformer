package de.richargh.ynabcsvtransformer.input

import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.lang.MultiValueMap
import de.richargh.ynabcsvtransformer.lang.mutableMultiValueMapOf
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.InputStream
import java.io.InputStreamReader
import java.security.InvalidParameterException

class CsvImporter {

    private var indexOf: Map<DomainName, Int> = emptyMap()
    private val foundHeader: Boolean get() = indexOf.isNotEmpty()

    fun mapTransactions(inputStream: InputStream, mappings: CsvMappings): Sequence<Transaction> {
        val csvParser = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withQuote('"')
        return sequence {
            val records: CSVParser = csvParser.parse(InputStreamReader(inputStream))
            records.forEachIndexed { index, record ->
                tryMap(index, record, mappings)?.let { yield(it) }
            }
        }
    }

    private fun tryMap(index: Int, csvRecord: CSVRecord, mappings: CsvMappings): Transaction? = try {
        when {
            foundHeader -> mapTransaction(csvRecord)
            matchColumnHeaders(csvRecord, mappings) -> null
            else -> null
        }
    } catch (e: Exception) {
        null
    }

    private fun mapTransaction(csvRecord: CSVRecord): Transaction? {
        val beneficiary = csvRecord.get(indexOf[DomainName.Beneficiary]!!)
        if(beneficiary.isNullOrBlank())
            return null

        return Transaction(beneficiary)
    }

    private fun matchColumnHeaders(csvRecord: CSVRecord, mappings: CsvMappings): Boolean {
        val indexOfMappableColumn = csvRecord.asSequence()
                .mapIndexed { index, value -> CsvColumn(value) to index }
                .filter { (column, index) -> column in mappings }
                .map { (column, index) -> mappings[column] to index }
                .toMap()

        val foundHeaders = indexOfMappableColumn.size == mappings.size
        if(foundHeaders)
            this.indexOf = indexOfMappableColumn
        return foundHeaders
    }

}

class CsvMappings private constructor(
        val nameOfColumn: Map<CsvColumn, DomainName>) {

    operator fun get(csvColumn: CsvColumn): DomainName = nameOfColumn[csvColumn]!!
    operator fun contains(csvColumn: CsvColumn) = nameOfColumn.containsKey(csvColumn)
    val size get() = nameOfColumn.size

    companion object {
        fun of(vararg domainNameToCsvColumn: Pair<DomainName, String>): CsvMappings {
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
            return CsvMappings(map)
        }
    }
}

sealed class DomainName {
    object Beneficiary : DomainName()

    companion object {
        val objects get() = DomainName::class.sealedSubclasses.mapNotNull { it.objectInstance }.toSet()
        fun of(rawValue: String): DomainName {
            return DomainName::class.sealedSubclasses.singleOrNull { it.simpleName == rawValue }?.objectInstance
                    ?: throw RuntimeException("The key $rawValue does not exist in the domain.")
        }
    }
}

data class CsvColumn(val rawValue: String)