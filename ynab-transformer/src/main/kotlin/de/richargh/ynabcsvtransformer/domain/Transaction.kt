package de.richargh.ynabcsvtransformer.domain

import java.time.LocalDate

data class Transaction(
        val date: LocalDate,
        val beneficiary: Beneficiary,
        val description: Description
//        val debit: Money?,
//        val credit: Money?
)

data class BookingDate(val rawValue: String){
    override fun toString() = rawValue
}
data class Beneficiary(val rawValue: String){
    override fun toString() = rawValue
    fun contains(beneficiary: Beneficiary) = rawValue.contains(beneficiary.rawValue)
}
data class Description(val rawValue: String){
    override fun toString() = rawValue
    fun contains(description: Description) = rawValue.contains(description.rawValue)
}
data class Outflow(val rawValue: String){
    override fun toString() = rawValue
}

data class Category(val rawValue: String){
    override fun toString() = rawValue
}