package de.richargh.ynabcsvtransformer.read

import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.read.DomainName.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

internal class CsvReaderTest {

    @Test
    fun `should be able to read singular german plusminus OutFlow transaction`(){
        // given
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"-120"
        """.trimIndent()
        val readConfig = ReadConfig(
            DateTimeFormatter.ofPattern("dd.MM.uuuu"),
            ';',
            Locale.GERMANY,
            CsvHeaders.of(
                BookingDate to "Buchung",
                Beneficiary to "Empfänger",
                Description to "Verwendungszweck",
                MoneyFlow.PlusMinusFlow.Flow to "Umsatz"))
        val testling = CsvReader()

        // when
        val result = csv.byteInputStream().use {
            testling.mapTransactions(it, readConfig).toList()
        }

        // then
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
        // given
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120"
        """.trimIndent()
        val readConfig = ReadConfig(
            DateTimeFormatter.ofPattern("dd.MM.uuuu"),
            ';',
            Locale.GERMANY,
            CsvHeaders.of(
                    BookingDate to "Buchung",
                    Beneficiary to "Empfänger",
                    Description to "Verwendungszweck",
                    MoneyFlow.PlusMinusFlow.Flow to "Umsatz"))
        val testling = CsvReader()

        // when
        val result = csv.byteInputStream().use {
            testling.mapTransactions(it, readConfig).toList()
        }

        // then
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
        // given
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Soll";"Haben"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120";"0"
        """.trimIndent()
        val readConfig = ReadConfig(
            DateTimeFormatter.ofPattern("dd.MM.uuuu"),
            ';',
            Locale.GERMANY,
            CsvHeaders.of(
                    BookingDate to "Buchung",
                    Beneficiary to "Empfänger",
                    Description to "Verwendungszweck",
                    MoneyFlow.InOutFlow.OutFlow to "Soll",
                    MoneyFlow.InOutFlow.InFlow to "Haben"))
        val testling = CsvReader()

        // when
        val result = csv.byteInputStream().use {
            testling.mapTransactions(it, readConfig).toList()
        }

        // then
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
        // given
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz";" "
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120";"S"
        """.trimIndent()
        val csvConfig = ReadConfig(
            DateTimeFormatter.ofPattern("dd.MM.uuuu"),
            ';',
            Locale.GERMANY,
            CsvHeaders.of(
                    BookingDate to "Buchung",
                    Beneficiary to "Empfänger",
                    Description to "Verwendungszweck",
                    MoneyFlow.MarkerFlow.Flow to "Umsatz",
                    MoneyFlow.MarkerFlow.Marker("H", "S") to " "))
        val testling = CsvReader()

        // when
        val result = csv.byteInputStream().use {
            testling.mapTransactions(it, csvConfig).toList()
        }

        // then
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
        // given
        val csv = """
        "Buchung";"Empfänger";"Verwendungszweck";"Währung";"Umsatz";" "
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120";"H"
        """.trimIndent()
        val readConfig = ReadConfig(
            DateTimeFormatter.ofPattern("dd.MM.uuuu"),
            ';',
            Locale.GERMANY,
            CsvHeaders.of(
                    BookingDate to "Buchung",
                    Beneficiary to "Empfänger",
                    Description to "Verwendungszweck",
                    MoneyFlow.MarkerFlow.Flow to "Umsatz",
                    MoneyFlow.MarkerFlow.Marker("H", "S") to " "))
        val testling = CsvReader()

        // when
        val result = csv.byteInputStream().use {
            testling.mapTransactions(it, readConfig).toList()
        }

        // then
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
        // given
        val csvConfig = ReadConfig(
            DateTimeFormatter.ofPattern("MM/dd/uuuu"),
            ';',
            Locale.US,
            CsvHeaders.of(
            BookingDate to "Booking date",
            Beneficiary to "Beneficiary / Originator",
            Description to "Payment Details",
            MoneyFlow.InOutFlow.OutFlow to "Debit",
            MoneyFlow.InOutFlow.InFlow to "Credit"))
        val testling = CsvReader()

        // when
        val result = javaClass.getResourceAsStream("DB-Transactions-Single1[EN].csv").use {
            testling.mapTransactions(it, csvConfig).toList()
        }

        // then
        assertThat(result).containsExactly(Transaction(
                LocalDate.of(2020,2,14),
                de.richargh.ynabcsvtransformer.domain.Beneficiary("ANACONDA EU"),
                de.richargh.ynabcsvtransformer.domain.Description("111-222222-3333333 Anaconda.de"),
                null,
                "150.12",
                ""))
    }

    @Test
    fun `should be able to read german singular VR transaction`(){
        // given
        val csvConfig = ReadConfig(
            DateTimeFormatter.ofPattern("dd.MM.uuuu"),
            ';',
            Locale.GERMANY,
            CsvHeaders.of(
            BookingDate to "Buchungstag",
            Beneficiary to "Empf�nger/Zahlungspflichtiger",
            Description to "Vorgang/Verwendungszweck",
            MoneyFlow.MarkerFlow.Flow to "Umsatz",
            MoneyFlow.MarkerFlow.Marker("H", "S") to " "))
        val testling = CsvReader()

        // when
        val result = javaClass.getResourceAsStream("VR-Transactions-Single1[DE].csv").use {
            testling.mapTransactions(it, csvConfig).toList()
        }

        // then
        assertThat(result).containsExactly(Transaction(
                LocalDate.of(2020,3,23),
                de.richargh.ynabcsvtransformer.domain.Beneficiary("Worldline Sweden AB fuer Clamp"),
                de.richargh.ynabcsvtransformer.domain.Description("""
                    BASISLASTSCHRIFT
                    SHELL 1122/ Frankfurt/DE
                    22.03.2021 um 09:12:34 Uhr""".trimIndent()),
                null,
                "33.33",
                "0"))
    }

}