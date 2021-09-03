package de.richargh.ynabcsvtransformer.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

class CsvConfigDto(
        val dateTimePattern: String,
        val header: HeaderConfigDto,
        val mappings: List<MappingDto>
)

class HeaderConfigDto(
        val bookingDate: String,
        val beneficiary: String,
        val description: String,
        val flow: FlowDto
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
        JsonSubTypes.Type(value = PlusMinusFlowDto::class, name = "PlusMinus"),
        JsonSubTypes.Type(value = InOutFlowDto::class, name = "InOut"))
sealed class FlowDto

/**
 * For something where the money is in one column and the +/- before the number declares it as in our out-flow.
 */
class PlusMinusFlowDto(
        val flow: String): FlowDto()

/**
 * For something where the in-flow is in one column and the out-flow is in another column.
 */
class InOutFlowDto(
        val inFlow: String,
        val outFlow: String
): FlowDto()

/**
 * For something where the money is in one column and if it's in or out-flow is declared in another column
 */
class MarkerFlowDto(
        val flow: String,
        val marker: String,
        val markerIn: String,
        val markerOut: String)

class MappingDto(
        val category: String,
        val beneficiary: String,
        val alias: AliasDto
)

class AliasDto(
        val beneficiary: List<String>?,
        val description: List<String>?,
        val outflow: List<String>?
)