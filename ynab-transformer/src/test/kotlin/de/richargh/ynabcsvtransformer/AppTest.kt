package de.richargh.ynabcsvtransformer

import de.richargh.ynabcsvtransformer.lang.Res
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class AppTest {

    @Test
    fun `should be able to read Db config plus csv and export in a different csv format`(){
        // given
        val testling = App()

        // when
        val result = ByteArrayOutputStream().use { stream ->
            val configRes = javaClass.getResourceAsStream("app/db.config.json").use {
                testling.readConfig(it)
            }
            javaClass.getResourceAsStream("app/db.csv").use {
                testling.transform(it, (configRes as Res.Ok).value, stream.writer())
            }
            stream.toString()
        }

        // then
        assertThat(result)
                .contains("Date;Payee;Category;Memo;Outflow;Inflow")
                .contains("2020-02-14;Anaconda;Entertainment: Gadget;Google;111-222222-3333333 Anaconda.de Notebook;-150.12;")
    }
}