package de.richargh.ynabcsvtransformer.input

import de.richargh.ynabcsvtransformer.domain.Beneficiary
import de.richargh.ynabcsvtransformer.domain.Category
import de.richargh.ynabcsvtransformer.domain.Description
import de.richargh.ynabcsvtransformer.domain.Outflow
import java.security.InvalidParameterException
import java.time.format.DateTimeFormatter

class CsvConfig(
        val dateFormatter: DateTimeFormatter,
        val headers: CsvHeaders,
        val mappings: List<Mapping>
) {
    fun mappingWith(beneficiary: Beneficiary, description: Description): Mapping? {
        return mappings.firstOrNull { m ->
            (m.alias.beneficiary.isEmpty() || m.alias.beneficiary.firstOrNull { alias -> beneficiary.contains(alias) } != null)
            && (m.alias.description.isEmpty() || m.alias.description.firstOrNull { alias -> description.contains(alias) } != null)
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

data class CsvColumn(val rawValue: String){
    override fun toString() = rawValue
}
