package de.richargh.ynabcsvtransformer.domain

data class Transaction(
//      val date
        val beneficiary: Beneficiary,
        val description: Description
//        val debit: Money?,
//        val credit: Money?
)

data class Beneficiary(val rawValue: String)
data class Description(val rawValue: String)

fun beneficiary(rawValue: String) = Beneficiary(rawValue)
fun description(rawValue: String) = Description(rawValue)