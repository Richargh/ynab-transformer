package de.richargh.ynabcsvtransformer.domain

import de.richargh.ynabcsvtransformer.config.mappingOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TransactionTest {

    private val anyDate = LocalDate.of(2020, 1, 20)

    @Test
    fun `should not change the transaction when the mapping is null`(){
        // given
        val testling = Transaction(anyDate, Beneficiary("Thomas"), Description("lunch sandwich"))

        // when
        val result = testling.withMapping(null)

        // then
        assertThat(result).isEqualTo(testling)
    }

    @Test
    fun `should change the beneficiary to the one in the mapping`(){
        // given
        val mapping = mappingOf(
                category = "Food: Restaurant",
                beneficiary = "Rentmaster",
                alias = Description("Mortgage"), Description("Rent"))
        val testling = Transaction(anyDate, Beneficiary("Thomas"), Description("lunch sandwich"))

        // when
        val result = testling.withMapping(mapping)

        // then
        assertThat(result).isEqualTo(Transaction(anyDate, Beneficiary("Rentmaster"), Description("lunch sandwich")))
    }
}