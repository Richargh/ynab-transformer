package de.richargh.ynabcsvtransformer.input

import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.input.DomainName.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

internal class CsvImporterTest {

    @Test
    fun `should be able to read english singular simple transaction`(){
        // arrange
        val csv = """
        "Buchung";"Empfänger";"Vorgang/Verwendungszweck";"Währung";"Umsatz"
        "21.02.2020";"John Mopp";"Laundry";"EUR";"120"
        """
        val testling = CsvImporter()

        // act
        val result = csv.byteInputStream().use {
            // act
            testling.mapTransactions(it, CsvMappings.of(Beneficiary to "Empfänger")).toList()
        }

        // assert
        assertThat(result).containsExactly(Transaction("John Mopp"))
    }

    @Test
    fun `should be able to read english singular DB transaction`(){
        // arrange
        val testling = CsvImporter()

        // act
        val result = javaClass.getResourceAsStream("DB-Transactions-Single1[EN].csv").use {
            // act
            testling.mapTransactions(it, CsvMappings.of(Beneficiary to "Beneficiary / Originator")).toList()
        }

        // assert
        assertThat(result).containsExactly(Transaction("ANACONDA EU"))
    }

    @Test
    fun `should be able to read german singular VR transaction`(){
        // arrange
        val testling = CsvImporter()

        // act
        val result = javaClass.getResourceAsStream("VR-Transactions-Single1[DE].csv").use {
            // act
            testling.mapTransactions(it, CsvMappings.of(Beneficiary to "Empf�nger/Zahlungspflichtiger")).toList()
        }

        // assert
        assertThat(result).containsExactly(Transaction("Worldline Sweden AB fuer Clamp"))
    }

}