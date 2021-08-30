package de.richargh.ynabcsvtransformer.domain

import de.richargh.ynabcsvtransformer.input.Beneficiary
import de.richargh.ynabcsvtransformer.input.Description
import java.time.LocalDate

data class Transaction(
        val date: LocalDate,
        val beneficiary: Beneficiary,
        val description: Description
//        val debit: Money?,
//        val credit: Money?
)