package de.richargh.ynabcsvtransformer.config

import de.richargh.ynabcsvtransformer.input.*
import de.richargh.ynabcsvtransformer.result.Res
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConfigReaderTest {

    @Test
    fun `should be able to read minimal config`(){
        // given
        val config = """
        {
          "dateTimePattern": "MM/dd/uuuu",
          "header": {
            "bookingDate": "Booking date",
            "beneficiary": "Beneficiary / Originator",
            "description": "Payment Details",
            "outflow": "Debit"
          },
          "mappings": []
        }
        """.trimIndent()
        val testling = ConfigReader()

        // when
        val result = config.byteInputStream().use {
            // act
            testling.csvConfig(it)
        }

        // then
        assertThat(result).isInstanceOf(Res.Ok::class.java)
        assertThat((result as Res.Ok<CsvConfig>).value.headers.nameOfColumn)
                .containsEntry(CsvColumn("Booking date"), DomainName.BookingDate)
                .containsEntry(CsvColumn("Beneficiary / Originator"), DomainName.Beneficiary)
                .containsEntry(CsvColumn("Payment Details"), DomainName.Description)
                .containsEntry(CsvColumn("Debit"), DomainName.Outflow)
    }

    @Test
    fun `should be able to read beneficiary mapping`(){
        // given
        val config = """
        {
          "dateTimePattern": "MM/dd/uuuu",
          "header": {
            "bookingDate": "Booking date",
            "beneficiary": "Beneficiary / Originator",
            "description": "Payment Details",
            "outflow": "Debit"
          },
          "mappings": [
            {
              "category" : "Monthly Bills: Energy",
              "beneficiary" : "Powerplant",
              "alias": {
                "beneficiary" : ["POWER"]
              }
            }
          ]
        }
        """.trimIndent()
        val testling = ConfigReader()

        // when
        val result = config.byteInputStream().use {
            // act
            testling.csvConfig(it)
        }

        // then
        assertThat(result).isInstanceOf(Res.Ok::class.java)
        assertThat((result as Res.Ok<CsvConfig>).value.mappings).containsOnly(mappingOf(
                category = "Monthly Bills: Energy", beneficiary = "Powerplant", Beneficiary("POWER")))
    }

    @Test
    fun `should be able to read description mapping`(){
        // given
        val config = """
        {
          "dateTimePattern": "MM/dd/uuuu",
          "header": {
            "bookingDate": "Booking date",
            "beneficiary": "Beneficiary / Originator",
            "description": "Payment Details",
            "outflow": "Debit"
          },
          "mappings": [
            {
              "category" : "Monthly Bills: Energy",
              "beneficiary" : "Powerplant",
              "alias": {
                "description": ["Energy for"]
              }
            }
          ]
        }
        """.trimIndent()
        val testling = ConfigReader()

        // when
        val result = config.byteInputStream().use {
            // act
            testling.csvConfig(it)
        }

        // then
        assertThat(result).isInstanceOf(Res.Ok::class.java)
        assertThat((result as Res.Ok<CsvConfig>).value.mappings).containsOnly(mappingOf(
                category = "Monthly Bills: Energy", beneficiary = "Powerplant", Description("Energy for"))
        )
    }

    @Test
    fun `should be able to read outflow mapping`(){
        // given
        val config = """
        {
          "dateTimePattern": "MM/dd/uuuu",
          "header": {
            "bookingDate": "Booking date",
            "beneficiary": "Beneficiary / Originator",
            "description": "Payment Details",
            "outflow": "Debit"
          },
          "mappings": [
            {
              "category" : "Monthly Bills: Energy",
              "beneficiary" : "Powerplant",
              "alias": {
                "outflow": ["45"]
              }
            }
          ]
        }
        """.trimIndent()
        val testling = ConfigReader()

        // when
        val result = config.byteInputStream().use {
            // act
            testling.csvConfig(it)
        }

        // then
        assertThat(result).isInstanceOf(Res.Ok::class.java)
        assertThat((result as Res.Ok<CsvConfig>).value.mappings).containsOnly(mappingOf(
                category = "Monthly Bills: Energy", beneficiary = "Powerplant", Outflow("45"))
        )
    }

    private fun mappingOf(category: String, beneficiary: String, aliasBene: Beneficiary) = Mapping(
            Category(category),
            Beneficiary(beneficiary),
            Alias(setOf(aliasBene), emptySet(), emptySet()))

    private fun mappingOf(category: String, beneficiary: String, aliasDesc: Description) = Mapping(
            Category(category),
            Beneficiary(beneficiary),
            Alias(emptySet(), setOf(aliasDesc), emptySet()))

    private fun mappingOf(category: String, beneficiary: String, aliasOutflow: Outflow) = Mapping(
            Category(category),
            Beneficiary(beneficiary),
            Alias(emptySet(), emptySet(), setOf(aliasOutflow)))
}