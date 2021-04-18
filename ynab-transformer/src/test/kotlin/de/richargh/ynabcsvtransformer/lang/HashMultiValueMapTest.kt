package de.richargh.ynabcsvtransformer.lang

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class HashMultiValueMapTest {

    @Test
    fun `should be empty initially `(){
        // arrange
        val testling = HashMultiValueMap<String, String>()

        // act
        val result = testling.isEmpty()

        // assert
        assertThat(result).isTrue()
    }

    @Test
    fun `if I put a new mapping into an empty map, the size should be one`(){
        // arrange
        val testling = HashMultiValueMap<String, String>()

        // act
        testling.put("foo", "bar")

        // assert
        assertThat(testling.size).isEqualTo(1)
    }

    @Test
    fun `size should reflect the number of key-value mappings`(){
        // arrange
        val testling = HashMultiValueMap.of("foo" to "bar", "foo" to "blub", "bingo" to "django")

        // act
        val result = testling.size

        // assert
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `the value of an unknown key should be empty list`(){
        // arrange
        val testling = HashMultiValueMap<String, String>()

        // act
        val result = testling["bla"]

        // assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `if I add an element I should be able to retrieve it`(){
        // arrange
        val testling = HashMultiValueMap<String, String>()

        // act
        testling.put("foo", "bar")

        // assert
        assertThat(testling["foo"]).containsExactly("bar")
    }

    @Test
    fun `if I add two elements with the same key I should be able to retrieve both`(){
        // arrange
        val testling = HashMultiValueMap<String, String>()

        // act
        testling.put("foo", "bar")
        testling.put("foo", "blubb")

        // assert
        assertThat(testling["foo"]).containsExactly("bar", "blubb")
    }

    @Test
    fun `if I add two elements with different keys I should be able to retrieve both`(){
        // arrange
        val testling = HashMultiValueMap<String, String>()

        // act
        testling.put("foo", "bar")
        testling.put("django", "wax")

        // assert
        assertThat(testling["foo"]).containsExactly("bar")
        assertThat(testling["django"]).containsExactly("wax")
    }

    @Test
    fun `if I delete a mapping it should be gone`(){
        // arrange
        val testling = HashMultiValueMap<String, String>()
        testling.put("foo", "bar")

        // act
        testling.deleteMapping("foo", "bar")

        // assert
        assertThat(testling["foo"]).isEmpty()
        assertThat(testling.size).isEqualTo(0)
        assertThat(testling.isEmpty()).isTrue
    }

}