package de.richargh.ynabcsvtransformer.config

import de.richargh.ynabcsvtransformer.read.*
import de.richargh.ynabcsvtransformer.domain.*
import java.time.format.DateTimeFormatter
import java.util.*

fun configOf(vararg mappings: Mapping) = CsvConfig(
        anyReadConfig(),
        anyWriteConfig(),
        Mappings(mappings.asList()))

private fun anyReadConfig() = ReadConfig(
        anyDatePattern(),
        ';',
        Locale.GERMANY,
        anyHeaders()
)

private fun anyWriteConfig() = WriteConfig(
        ';'
)

private fun anyDatePattern() = DateTimeFormatter.ofPattern("MM/dd/uuuu")

private fun anyHeaders() = CsvHeaders.of(
        DomainName.BookingDate to "foo1",
        DomainName.Beneficiary to "foo2",
        DomainName.Description to "foo3",
        DomainName.MoneyFlow.InOutFlow.InFlow to "foo4",
        DomainName.MoneyFlow.InOutFlow.OutFlow to "foo5")

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