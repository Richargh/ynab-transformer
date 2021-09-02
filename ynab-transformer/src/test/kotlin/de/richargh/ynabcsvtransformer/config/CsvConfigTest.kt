package de.richargh.ynabcsvtransformer.config

import de.richargh.ynabcsvtransformer.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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

    @Test
    fun `should only find mapping when both provided beneficiary and description alias match`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                Beneficiary("Master"),
                Description("Rent"),
                aliasOutflow = null)
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Rent for the Month"))

        // then
        assertThat(result).isEqualTo(mapping)
    }

    @Test
    fun `should not find mapping when one of the provided beneficiary or description alias do not match`(){
        // given
        val mapping = mappingOf(
                category = "Monthly Bills: Rent",
                beneficiary = "Rentmaster",
                Beneficiary("Steve"),
                Description("Rent"),
                aliasOutflow = null)
        val testling = configOf(mapping)

        // when
        val result = testling.mappingWith(Beneficiary("John Master Smith"), Description("My Blue Jeans"))

        // then
        assertThat(result).isNull()
    }
}