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
                alias = Beneficiary("John Master Smith"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Rent for the Month"))

        // then
        assertThat(result).isEqualTo(mapping)
    }

    @Test
    fun `should find mapping when single beneficiary alias matches part of the beneficiary`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Beneficiary("Master"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Rent for the Month"))

        // then
        assertThat(result).isEqualTo(mapping)
    }

    @Test
    fun `should find mapping when one of the beneficiary alias matches part of the beneficiary`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Beneficiary("Steve"), Beneficiary("Master"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Rent for the Month"))

        // then
        assertThat(result).isEqualTo(mapping)
    }

    @Test
    fun `should not find mapping when single beneficiary alias does not match at all`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Beneficiary("John Master Smith"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("Jim Close"), Description("My Rent for the Month"))

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `should find mapping when single description alias matches exactly`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Description("My Rent for the Month"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Rent for the Month"))

        // then
        assertThat(result).isEqualTo(mapping)
    }

    @Test
    fun `should find mapping when single description alias matches part of the description`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Description("Rent"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Rent for the Month"))

        // then
        assertThat(result).isEqualTo(mapping)
    }



    @Test
    fun `should find mapping when one of the description alias matches part of the description`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Description("Mortgage"), Description("Rent"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Rent for the Month"))

        // then
        assertThat(result).isEqualTo(mapping)
    }

    @Test
    fun `should not find mapping when single description alias does not match at all`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                alias = Description("My Rent for the Month"))
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Blue Jeans"))

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

    private fun mappingOf(category: String, beneficiary: String, alias: Beneficiary, vararg extraAlias: Beneficiary) =
            Mapping(
                Category(category),
                Beneficiary(beneficiary),
                Alias(setOf(alias, *extraAlias), emptySet(), emptySet()))

    private fun mappingOf(category: String, beneficiary: String, alias: Description, vararg extraAlias: Description) =
            Mapping(
                Category(category),
                Beneficiary(beneficiary),
                Alias(emptySet(), setOf(alias, *extraAlias), emptySet()))

    private fun mappingOf(category: String, beneficiary: String, alias: Outflow, vararg aliasOutflow: Outflow) =
            Mapping(
                Category(category),
                Beneficiary(beneficiary),
                Alias(emptySet(), emptySet(), setOf(alias, *aliasOutflow)))

    private fun mappingOf(category: String, beneficiary: String,
                          aliasBeneficiary: Beneficiary, aliasDescription: Description, aliasOutflow: Outflow) =
            Mapping(
                Category(category),
                Beneficiary(beneficiary),
                Alias(setOf(aliasBeneficiary), setOf(aliasDescription), setOf(aliasOutflow)))
}