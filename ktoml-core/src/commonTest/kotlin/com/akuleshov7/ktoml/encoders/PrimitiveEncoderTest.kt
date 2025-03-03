package com.akuleshov7.ktoml.encoders

import com.akuleshov7.ktoml.annotations.TomlLiteral
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test

class PrimitiveEncoderTest {
    @Serializable
    enum class Greeting {
        @SerialName("hello") Hello
    }

    @Test
    fun primitivesTest() {
        @Serializable
        data class File(
            val enabled: Boolean = true,
            val pi: Double = 3.14,
            val count: Long = 3,
            val greeting: String = "hello",
            val enumGreeting: Greeting = Greeting.Hello,
            @TomlLiteral
            val path: String = """C:\some\path\"""
        )

        assertEncodedEquals(
            value = File(),
            expectedToml = """
                enabled = true
                pi = 3.14
                count = 3
                greeting = "hello"
                enumGreeting = "hello"
                path = 'C:\some\path\'
            """.trimIndent()
        )
    }

    @Test
    fun stringEscapeTest() {
        @Serializable
        data class File(
            val escapeString: String? = null,
            @TomlLiteral
            val literalEscapeString: String? = null
        )

        val tab = '\t'

        assertEncodedEquals(
            value = File("\"hello world\""),
            expectedToml = """escapeString = "\"hello world\"""""
        )

        assertEncodedEquals(
            value = File("hello \b\t\n\u000C\r world"),
            expectedToml = """escapeString = "hello \b$tab\n\f\r world""""
        )

        assertEncodedEquals(
            value = File("hello \u0000 world"),
            expectedToml = """escapeString = "hello \u0000 world""""
        )

        assertEncodedEquals(
            value = File("""hello\world"""),
            expectedToml = """escapeString = "hello\\world""""
        )

        assertEncodedEquals(
            value = File("""hello \Uffffffff world"""),
            expectedToml = """escapeString = "hello \Uffffffff world""""
        )

        assertEncodedEquals(
            value = File(literalEscapeString = "'quotes'"),
            expectedToml = """literalEscapeString = '\'quotes\''"""
        )
    }

    @Test
    fun jsWholeDoubleRegression() {
        @Serializable
        data class File(
            val wholeNumberDouble: Double = 3.0
        )

        assertEncodedEquals(
            value = File(),
            expectedToml = "wholeNumberDouble = 3.0"
        )
    }
}
