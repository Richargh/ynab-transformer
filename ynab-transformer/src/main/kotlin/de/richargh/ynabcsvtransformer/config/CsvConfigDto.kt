package de.richargh.ynabcsvtransformer.config

class CsvConfigDto(
        val dateTimePattern: String,
        val header: HeaderConfigDto)

class HeaderConfigDto(
        val bookingDate: String,
        val beneficiary: String,
        val description: String,
        val outflow: String)