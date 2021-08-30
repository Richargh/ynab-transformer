package de.richargh.ynabcsvtransformer.config

class CsvConfigDto(
        val dateTimePattern: String,
        val header: HeaderConfigDto,
        val mappings: List<MappingDto>
)

class HeaderConfigDto(
        val bookingDate: String,
        val beneficiary: String,
        val description: String,
        val outflow: String
)

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