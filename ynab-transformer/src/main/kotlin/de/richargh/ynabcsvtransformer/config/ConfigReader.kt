package de.richargh.ynabcsvtransformer.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.richargh.ynabcsvtransformer.domain.Beneficiary
import de.richargh.ynabcsvtransformer.domain.Category
import de.richargh.ynabcsvtransformer.domain.Description
import de.richargh.ynabcsvtransformer.domain.Outflow
import de.richargh.ynabcsvtransformer.input.*
import de.richargh.ynabcsvtransformer.result.Res
import de.richargh.ynabcsvtransformer.result.fail
import de.richargh.ynabcsvtransformer.result.ok
import java.io.InputStream
import java.time.format.DateTimeFormatter

class ConfigReader {

    private val mapper = ObjectMapper().registerModule(KotlinModule())

    fun csvConfig(config: InputStream): Res<CsvConfig> {
        val csvConfigDto = try {
            mapper.readValue<CsvConfigDto>(config)
        } catch (e: MismatchedInputException){
            return fail(e.message!!)
        }

        return ok(CsvConfig(
                DateTimeFormatter.ofPattern(csvConfigDto.dateTimePattern),
                CsvHeaders.of(
                        DomainName.BookingDate to csvConfigDto.header.bookingDate,
                        DomainName.Beneficiary to csvConfigDto.header.beneficiary,
                        DomainName.Description to csvConfigDto.header.description,
                        DomainName.Outflow to csvConfigDto.header.outflow),
                csvConfigDto.mappings.map(::mapping)))
    }

    private fun mapping(mappingDto: MappingDto) = Mapping(
            Category(mappingDto.category),
            Beneficiary(mappingDto.beneficiary),
            alias(mappingDto.alias)
    )

    private fun alias(aliasDto: AliasDto) = Alias(
            aliasDto.beneficiary?.map(::Beneficiary)?.toSet() ?: emptySet(),
            aliasDto.description?.map(::Description)?.toSet() ?: emptySet(),
            aliasDto.outflow?.map(::Outflow)?.toSet() ?: emptySet(),
    )
}