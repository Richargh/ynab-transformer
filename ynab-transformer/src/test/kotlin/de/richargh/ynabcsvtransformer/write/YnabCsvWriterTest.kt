package de.richargh.ynabcsvtransformer.write

import de.richargh.ynabcsvtransformer.domain.Beneficiary
import de.richargh.ynabcsvtransformer.domain.Category
import de.richargh.ynabcsvtransformer.domain.Description
import de.richargh.ynabcsvtransformer.domain.Transaction
import de.richargh.ynabcsvtransformer.read.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.*

class YnabCsvWriterTest {

    @Test
    fun `should be able to write ynab header correctly`(){
        // given
        val transaction = Transaction(
                LocalDate.of(2011, 1, 10), Beneficiary("Steve"), Description("None"), null,
                100.5.toBigDecimal(),
                0.0.toBigDecimal()
        )
        val writeConfig = WriteConfig(
                ';',
                Locale.GERMANY)
        val testling = YnabCsvWriter()

        // when
        val result = ByteArrayOutputStream().use { stream ->
            testling.write(sequenceOf(transaction), writeConfig, stream.writer())
            stream.toString()
        }

        // then
        assertThat(result).contains("Date;Payee;Category;Memo;Outflow;Inflow")
    }

    @Test
    fun `should be able to print simple data`(){
        // given
        val transaction = Transaction(
                LocalDate.of(2011, 1, 10), Beneficiary("Steve"), Description("None"), Category("Default"),
                0.toBigDecimal(),
                0.0.toBigDecimal()
        )
        val writeConfig = WriteConfig(
                ';',
                Locale.GERMANY)
        val testling = YnabCsvWriter()

        // when
        val result = ByteArrayOutputStream().use { stream ->
            testling.write(sequenceOf(transaction), writeConfig, stream.writer())
            stream.toString()
        }

        // then
        assertThat(result).contains("2011-01-10;Steve;Default;None;0;0")
    }

    @Test
    fun `should be able to map outflow to us locale`(){
        // given
        val transaction = Transaction(
                LocalDate.of(2011, 1, 10), Beneficiary("Steve"), Description("None"), null,
                100.5.toBigDecimal(),
                0.0.toBigDecimal()
        )
        val writeConfig = WriteConfig(
                ';',
                Locale.US)
        val testling = YnabCsvWriter()

        // when
        val result = ByteArrayOutputStream().use { stream ->
            testling.write(sequenceOf(transaction), writeConfig, stream.writer())
            stream.toString()
        }

        // then
        assertThat(result).contains("2011-01-10;Steve;;None;100.5;0")
    }

    @Test
    fun `should be able to map outflow to german locale`(){
        // given
        val transaction = Transaction(
                LocalDate.of(2011, 1, 10), Beneficiary("Steve"), Description("None"), null,
                100.5.toBigDecimal(),
                0.0.toBigDecimal()
        )
        val writeConfig = WriteConfig(
                ';',
                Locale.GERMANY)
        val testling = YnabCsvWriter()

        // when
        val result = ByteArrayOutputStream().use { stream ->
            testling.write(sequenceOf(transaction), writeConfig, stream.writer())
            stream.toString()
        }

        // then
        assertThat(result).contains("2011-01-10;Steve;;None;100,5;0")
    }

}