package de.richargh.ynabcsvtransformer.input

import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.input.DomainName.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class CsvReaderTest {

    @Test
    fun `should be able to read singular german plusminus OutFlow transaction`(){
        // arrange
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"-120"
        """.trimIndent()
        val csvConfig = CsvConfig(
                ReadConfig(
                    DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                    CsvHeaders.of(
                        BookingDate to "Buchung",
                        Beneficiary to "Empfänger",
                        Description to "Verwendungszweck",
                        MoneyFlow.PlusMinusFlow.Flow to "Umsatz")),
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
                de.richargh.ynabcsvtransformer.domain.Description("Laundry"),
                null,
                "120",
                "0"))
    }

    @Test
    fun `should be able to read singular german plusminus InFlow transaction`(){
        // arrange
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120"
        """.trimIndent()
        val csvConfig = CsvConfig(
                ReadConfig(
                    DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                    CsvHeaders.of(
                            BookingDate to "Buchung",
                            Beneficiary to "Empfänger",
                            Description to "Verwendungszweck",
                            MoneyFlow.PlusMinusFlow.Flow to "Umsatz")),
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
                de.richargh.ynabcsvtransformer.domain.Description("Laundry"),
                null,
                "0",
                "120"))
    }

    @Test
    fun `should be able to read singular german inout flow transaction`(){
        // arrange
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Soll";"Haben"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120";"0"
        """.trimIndent()
        val csvConfig = CsvConfig(
                ReadConfig(
                    DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                    CsvHeaders.of(
                            BookingDate to "Buchung",
                            Beneficiary to "Empfänger",
                            Description to "Verwendungszweck",
                            MoneyFlow.InOutFlow.OutFlow to "Soll",
                            MoneyFlow.InOutFlow.InFlow to "Haben")),
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
                de.richargh.ynabcsvtransformer.domain.Description("Laundry"),
                null,
                "120",
                "0"))
    }

    @Test
    fun `should be able to read singular german marker OutFlow transaction`(){
        // arrange
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz";" "
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120";"S"
        """.trimIndent()
        val csvConfig = CsvConfig(
                ReadConfig(
                    DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                    CsvHeaders.of(
                            BookingDate to "Buchung",
                            Beneficiary to "Empfänger",
                            Description to "Verwendungszweck",
                            MoneyFlow.MarkerFlow.Flow to "Umsatz",
                            MoneyFlow.MarkerFlow.Marker("H", "S") to " ")),
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
                de.richargh.ynabcsvtransformer.domain.Description("Laundry"),
                null,
                "120",
                "0"))
    }

    @Test
    fun `should be able to read singular german marker InFlow transaction`(){
        // arrange
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz";" "
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120";"H"
        """.trimIndent()
        val csvConfig = CsvConfig(
                ReadConfig(
                    DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                    CsvHeaders.of(
                            BookingDate to "Buchung",
                            Beneficiary to "Empfänger",
                            Description to "Verwendungszweck",
                            MoneyFlow.MarkerFlow.Flow to "Umsatz",
                            MoneyFlow.MarkerFlow.Marker("H", "S") to " ")),
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
                de.richargh.ynabcsvtransformer.domain.Description("Laundry"),
                null,
                "0",
                "120"))
    }

    @Test
    fun `should be able to read english singular DB transaction`(){
        // arrange
        val csvConfig = CsvConfig(
                ReadConfig(
                    DateTimeFormatter.ofPattern("MM/dd/uuuu"),
                    CsvHeaders.of(
                    BookingDate to "Booking date",
                    Beneficiary to "Beneficiary / Originator",
                    Description to "Payment Details",
                    MoneyFlow.InOutFlow.OutFlow to "Debit",
                    MoneyFlow.InOutFlow.InFlow to "Credit")),
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
                de.richargh.ynabcsvtransformer.domain.Description("111-222222-3333333 Anaconda.de"),
                null,
                "-150.12",
                ""))
    }

    @Test
    fun `should be able to read german singular VR transaction`(){
        // arrange
        val csvConfig = CsvConfig(
                ReadConfig(
                    DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                    CsvHeaders.of(
                    BookingDate to "Buchungstag",
                    Beneficiary to "Empf�nger/Zahlungspflichtiger",
                    Description to "Vorgang/Verwendungszweck",
                    MoneyFlow.InOutFlow.OutFlow to "Umsatz",
                    MoneyFlow.InOutFlow.InFlow to "W�hrung")),
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
                    22.03.2021 um 09:12:34 Uhr""".trimIndent()),
                null,
                "33,33",
                "EUR"))
    }

}