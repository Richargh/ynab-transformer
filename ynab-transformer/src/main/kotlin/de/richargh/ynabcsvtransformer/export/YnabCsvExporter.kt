package de.richargh.ynabcsvtransformer.export

import de.richargh.ynabcsvtransformer.domain.Transaction

class YnabCsvExporter {

    fun mapTransactions(transactions: Sequence<Transaction>) {
        println("Date;Payee;Category;Memo;Outflow;Inflow")
        transactions.forEach {
            println("07/01/20;${it.beneficiary.rawValue};None;${it.description.rawValue};0;0")
        }
    }
}