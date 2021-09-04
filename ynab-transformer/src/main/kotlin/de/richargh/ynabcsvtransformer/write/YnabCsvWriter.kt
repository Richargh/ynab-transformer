package de.richargh.ynabcsvtransformer.write

import de.richargh.ynabcsvtransformer.domain.Transaction

class YnabCsvWriter {

    fun mapTransactions(transactions: Sequence<Transaction>) {
        println("Date;Payee;Category;Memo;Outflow;Inflow")
        transactions.forEach {
            println("${it.date};${it.beneficiary.rawValue};${it.category?.rawValue ?: ""};${it.description.rawValue};${it.outFlow};${it.inFlow}")
        }
    }
}