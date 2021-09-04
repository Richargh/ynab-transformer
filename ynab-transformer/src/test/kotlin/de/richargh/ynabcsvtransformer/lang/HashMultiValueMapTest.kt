package de.richargh.ynabcsvtransformer.lang

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HashMultiValueMapTest {

    @Test
    fun `should be empty initially `(){
        // given
        val testling = HashMultiValueMap<String, String>()

        // when
        val result = testling.isEmpty()

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `if I put a new mapping into an empty map, the size should be one`(){
        // given
        val testling = HashMultiValueMap<String, String>()

        // when
        testling.put("foo", "bar")

        // then
        assertThat(testling.size).isEqualTo(1)
    }

    @Test
    fun `size should reflect the number of key-value mappings`(){
        // given
        val testling = HashMultiValueMap.of("foo" to "bar", "foo" to "blub", "bingo" to "django")

        // when
        val result = testling.size

        // then
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `the value of an unknown key should be empty list`(){
        // given
        val testling = HashMultiValueMap<String, String>()

        // when
        val result = testling["bla"]

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `if I add an element I should be able to retrieve it`(){
        // given
        val testling = HashMultiValueMap<String, String>()

        // when
        testling.put("foo", "bar")

        // then
        assertThat(testling["foo"]).containsExactly("bar")
    }

    @Test
    fun `if I add two elements with the same key I should be able to retrieve both`(){
        // given
        val testling = HashMultiValueMap<String, String>()

        // when
        testling.put("foo", "bar")
        testling.put("foo", "blubb")

        // then
        assertThat(testling["foo"]).containsExactly("bar", "blubb")
    }

    @Test
    fun `if I add two elements with different keys I should be able to retrieve both`(){
        // given
        val testling = HashMultiValueMap<String, String>()

        // when
        testling.put("foo", "bar")
        testling.put("django", "wax")

        // then
        assertThat(testling["foo"]).containsExactly("bar")
        assertThat(testling["django"]).containsExactly("wax")
    }

    @Test
    fun `if I delete a mapping it should be gone`(){
        // given
        val testling = HashMultiValueMap<String, String>()
        testling.put("foo", "bar")

        // when
        testling.deleteMapping("foo", "bar")

        // then
        assertThat(testling["foo"]).isEmpty()
        assertThat(testling.size).isEqualTo(0)
        assertThat(testling.isEmpty()).isTrue
    }

}