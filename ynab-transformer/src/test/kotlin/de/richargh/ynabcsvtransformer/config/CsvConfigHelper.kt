package de.richargh.ynabcsvtransformer.config

import de.richargh.ynabcsvtransformer.input.*
import java.time.format.DateTimeFormatter


fun configOf(vararg mappings: Mapping) = CsvConfig(
        anyDatePattern(),
        anyHeaders(),
        mappings.asList())

private fun anyDatePattern() = DateTimeFormatter.ofPattern("MM/dd/uuuu")

private fun anyHeaders() = CsvHeaders.of(
        DomainName.BookingDate to "foo1",
        DomainName.Beneficiary to "foo2",
        DomainName.Description to "foo3",
        DomainName.Outflow to "foo4")

fun mappingOf(category: String, beneficiary: String, alias: Beneficiary, vararg extraAlias: Beneficiary) =
        Mapping(
                Category(category),
                Beneficiary(beneficiary),
                Alias(setOf(alias, *extraAlias), emptySet(), emptySet()))

fun mappingOf(category: String, beneficiary: String, alias: Description, vararg extraAlias: Description) =
        Mapping(
                Category(category),
                Beneficiary(beneficiary),
                Alias(emptySet(), setOf(alias, *extraAlias), emptySet()))

fun mappingOf(category: String, beneficiary: String, alias: Outflow, vararg aliasOutflow: Outflow) =
        Mapping(
                Category(category),
                Beneficiary(beneficiary),
                Alias(emptySet(), emptySet(), setOf(alias, *aliasOutflow)))

fun mappingOf(category: String, beneficiary: String,
                      aliasBeneficiary: Beneficiary?, aliasDescription: Description?, aliasOutflow: Outflow?) =
        Mapping(
                Category(category),
                Beneficiary(beneficiary),
                Alias(
                        if(aliasBeneficiary != null) setOf(aliasBeneficiary) else emptySet(),
                        if(aliasDescription != null)setOf(aliasDescription) else emptySet(),
                        if(aliasOutflow != null) setOf(aliasOutflow) else emptySet()))