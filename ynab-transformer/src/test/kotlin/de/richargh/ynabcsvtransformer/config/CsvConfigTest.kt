package de.richargh.ynabcsvtransformer.config

import de.richargh.ynabcsvtransformer.input.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.format.DateTimeFormatter

class CsvConfigTest {

    @Test
    fun `should find mapping when single beneficiary alias matches exactly`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Beneficiary("John Master"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master"), Description("My Monthly Rent"))

        // then
        assertThat(result).isEqualTo(mapping)
    }

    @Test
    fun `should not find mapping when single beneficiary alias does not match at all`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Beneficiary("John Master"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("Jim Close"), Description("My Monthly Rent"))

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `should find mapping when single description alias matches exactly`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Description("My Monthly Rent"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master"), Description("My Monthly Rent"))

        // then
        assertThat(result).isEqualTo(mapping)
    }

    @Test
    fun `should not find mapping when single description alias does not match at all`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Description("My Monthly Rent"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master"), Description("My Blue Jeans"))

        // then
        assertThat(result).isNull()
    }

    private fun configOf(vararg mappings: Mapping) = CsvConfig(
            anyDatePattern(),
            anyHeaders(),
            mappings.asList())

    private fun anyDatePattern() = DateTimeFormatter.ofPattern("MM/dd/uuuu")

    private fun anyHeaders() = CsvHeaders.of(
            DomainName.BookingDate to "foo1",
            DomainName.Beneficiary to "foo2",
            DomainName.Description to "foo3",
            DomainName.Outflow to "foo4")

    private fun mappingOf(category: String, beneficiary: String, alias: Beneficiary) = Mapping(
            Category(category),
            Beneficiary(beneficiary),
            Alias(setOf(alias), emptySet(), emptySet()))

    private fun mappingOf(category: String, beneficiary: String, alias: Description) = Mapping(
            Category(category),
            Beneficiary(beneficiary),
            Alias(emptySet(), setOf(alias), emptySet()))

    private fun mappingOf(category: String, beneficiary: String, alias: Outflow) = Mapping(
            Category(category),
            Beneficiary(beneficiary),
            Alias(emptySet(), emptySet(), setOf(alias)))
}