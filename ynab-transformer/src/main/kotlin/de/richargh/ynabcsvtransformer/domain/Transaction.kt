package de.richargh.ynabcsvtransformer.domain

import de.richargh.ynabcsvtransformer.read.Mapping
import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
        val date: LocalDate,
        val beneficiary: Beneficiary,
        val description: Description,
        val category: Category?,
        val outFlow: BigDecimal,
        val inFlow: BigDecimal) {
    fun withMapping(mapping: Mapping?): Transaction {
        if(mapping == null)
            return this

        return Transaction(
                date,
                mapping.beneficiary,
                description,
                mapping.category,
                outFlow,
                inFlow)
    }
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