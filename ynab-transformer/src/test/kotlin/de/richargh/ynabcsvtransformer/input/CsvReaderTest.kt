package de.richargh.ynabcsvtransformer.input

import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.input.DomainName.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class CsvReaderTest {

    @Test
    fun `should be able to read english singular simple transaction`(){
        // arrange
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120"
        """.trimIndent()
        val csvConfig = CsvConfig(
                DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                CsvHeaders.of(
                BookingDate to "Buchung",
                Beneficiary to "Empfänger",
                Description to "Verwendungszweck",
                Outflow to "Umsatz"),
                emptyList())
        val testling = CsvReader()

        // act
        val result = csv.byteInputStream().use {
            // act
            testling.mapTransactions(it, csvConfig).toList()
        }

        // assert
        assertThat(result).containsExactly(Transaction(
                LocalDate.of(2020,2,21),
                de.richargh.ynabcsvtransformer.domain.Beneficiary("John Mopp"),
                de.richargh.ynabcsvtransformer.domain.Description("Laundry")))
    }

    @Test
    fun `should be able to read english singular simple multi-line transaction`(){
        // arrange
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120"
        """.trimIndent()
        val csvConfig = CsvConfig(
                DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                CsvHeaders.of(
                BookingDate to "Buchung",
                Beneficiary to "Empfänger",
                Description to "Verwendungszweck",
                Outflow to "Umsatz"),
                emptyList())
        val testling = CsvReader()

        // act
        val result = csv.byteInputStream().use {
            // act
            testling.mapTransactions(it, csvConfig).toList()
        }

        // assert
        assertThat(result).containsExactly(Transaction(
                LocalDate.of(2020,2,21),
                de.richargh.ynabcsvtransformer.domain.Beneficiary("John Mopp"),
                de.richargh.ynabcsvtransformer.domain.Description("Laundry")))
    }

    @Test
    fun `should be able to read english singular DB transaction`(){
        // arrange
        val csvConfig = CsvConfig(
                DateTimeFormatter.ofPattern("MM/dd/uuuu"),
                CsvHeaders.of(
                BookingDate to "Booking date",
                Beneficiary to "Beneficiary / Originator",
                Description to "Payment Details",
                Outflow to "Debit"),
                emptyList())
        val testling = CsvReader()

        // act
        val result = javaClass.getResourceAsStream("DB-Transactions-Single1[EN].csv").use {
            // act
            testling.mapTransactions(it, csvConfig).toList()
        }

        // assert
        assertThat(result).containsExactly(Transaction(
                LocalDate.of(2020,2,14),
                de.richargh.ynabcsvtransformer.domain.Beneficiary("ANACONDA EU"),
                de.richargh.ynabcsvtransformer.domain.Description("111-222222-3333333 Anaconda.de")))
    }

    @Test
    fun `should be able to read german singular VR transaction`(){
        // arrange
        val csvConfig = CsvConfig(
                DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                CsvHeaders.of(
                BookingDate to "Buchungstag",
                Beneficiary to "Empf�nger/Zahlungspflichtiger",
                Description to "Vorgang/Verwendungszweck",
                Outflow to "Umsatz"),
                emptyList())
        val testling = CsvReader()

        // act
        val result = javaClass.getResourceAsStream("VR-Transactions-Single1[DE].csv").use {
            // act
            testling.mapTransactions(it, csvConfig).toList()
        }

        // assert
        assertThat(result).containsExactly(Transaction(
                LocalDate.of(2020,3,23),
                de.richargh.ynabcsvtransformer.domain.Beneficiary("Worldline Sweden AB fuer Clamp"),
                de.richargh.ynabcsvtransformer.domain.Description("""
                    BASISLASTSCHRIFT
                    SHELL 1122/ Frankfurt/DE
                    22.03.2021 um 09:12:34 Uhr""".trimIndent())))
    }

}