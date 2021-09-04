package de.richargh.ynabcsvtransformer.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.richargh.ynabcsvtransformer.domain.Beneficiary
import de.richargh.ynabcsvtransformer.domain.Category
import de.richargh.ynabcsvtransformer.domain.Description
import de.richargh.ynabcsvtransformer.domain.Outflow
import de.richargh.ynabcsvtransformer.read.*
import de.richargh.ynabcsvtransformer.lang.Res
import de.richargh.ynabcsvtransformer.lang.fail
import de.richargh.ynabcsvtransformer.lang.ok
import java.io.InputStream
import java.time.format.DateTimeFormatter
import java.util.*

class ConfigReader {

    private val mapper = ObjectMapper().registerModule(KotlinModule())

    fun csvConfig(config: InputStream): Res<CsvConfig> {
        val csvConfigDto = try {
            mapper.readValue<CsvConfigDto>(config)
        } catch (e: MismatchedInputException){
            return fail(e.message!!)
        }

        val moneyFlow = when(csvConfigDto.read.flow){
            is PlusMinusFlowDto -> arrayOf(
                    DomainName.MoneyFlow.PlusMinusFlow.Flow to csvConfigDto.read.flow.flow)
            is InOutFlowDto -> arrayOf(
                    DomainName.MoneyFlow.InOutFlow.InFlow to csvConfigDto.read.flow.inFlow,
                    DomainName.MoneyFlow.InOutFlow.OutFlow to csvConfigDto.read.flow.outFlow)
            is MarkerFlowDto -> arrayOf(
                    DomainName.MoneyFlow.MarkerFlow.Flow to csvConfigDto.read.flow.flow,
                    DomainName.MoneyFlow.MarkerFlow.Marker(
                        inFlowMarker = csvConfigDto.read.flow.markerInFlow,
                        outFlowMarker = csvConfigDto.read.flow.markerOutFlow) to csvConfigDto.read.flow.marker)
        }

        return ok(CsvConfig(
                ReadConfig(
                    DateTimeFormatter.ofPattern(csvConfigDto.read.bookingDatePattern),
                    csvConfigDto.read.delimiter,
                    Locale.forLanguageTag(csvConfigDto.read.localeLanguageTag),
                    CsvHeaders.of(
                            DomainName.BookingDate to csvConfigDto.read.bookingDate,
                            DomainName.Beneficiary to csvConfigDto.read.beneficiary,
                            DomainName.Description to csvConfigDto.read.description,
                            *moneyFlow)),
                WriteConfig(
                    csvConfigDto.write.delimiter,
                    Locale.forLanguageTag(csvConfigDto.write.localeLanguageTag)
                ),
                Mappings(csvConfigDto.mappings.map(::mapping))))
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