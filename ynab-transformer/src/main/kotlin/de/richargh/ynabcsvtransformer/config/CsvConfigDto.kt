package de.richargh.ynabcsvtransformer.config

class CsvConfigDto(
        val header: HeaderConfigDto)

class HeaderConfigDto(
        val beneficiary: String,
        val description: String)