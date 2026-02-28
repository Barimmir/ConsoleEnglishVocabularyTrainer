package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class QuestionTest {

    @Test
    fun `should create question correctly`() {
        val word1 = Word("hello", "привет", 0)
        val word2 = Word("dog", "собака", 1)
        val word3 = Word("cat", "кошка", 2)
        val word4 = Word("book", "книга", 0)
        
        val variants = listOf(word1, word2, word3, word4)
        val correctAnswer = word1
        val correctTranslations = listOf("привет", "собака", "кошка", "книга")
        val answerOptions = listOf("кошка", "привет", "книга", "собака")
        
        val question = Question(
            variants = variants,
            correctAnswer = correctAnswer,
            correctTranslations = correctTranslations,
            answerOptions = answerOptions
        )
        
        assertEquals(variants, question.variants)
        assertEquals(correctAnswer, question.correctAnswer)
        assertEquals(correctTranslations, question.correctTranslations)
        assertEquals(answerOptions, question.answerOptions)
    }

    @Test
    fun `should display question correctly`() {
        val word1 = Word("hello", "привет", 0)
        val word2 = Word("dog", "собака", 1)
        
        val question = Question(
            variants = listOf(word1, word2),
            correctAnswer = word1,
            correctTranslations = listOf("привет", "собака"),
            answerOptions = listOf("собака", "привет")
        )
        
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))
        
        try {
            val result = question.asConsoleString()
            
            val output = outputStream.toString()
            assertTrue(output.contains("hello:"))
            assertTrue(output.contains("1 - собака"))
            assertTrue(output.contains("2 - привет"))
            assertTrue(output.contains("0 - Меню"))
            
            assertEquals("", result)
        } finally {
            System.setOut(originalOut)
        }
    }

    @Test
    fun `should handle empty answer options`() {
        val word = Word("test", "тест", 0)
        
        val question = Question(
            variants = listOf(word),
            correctAnswer = word,
            correctTranslations = listOf("тест"),
            answerOptions = emptyList()
        )
        
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))
        
        try {
            question.asConsoleString()
            
            val output = outputStream.toString()
            assertTrue(output.contains("test:"))
            assertTrue(output.contains("0 - Меню"))
        } finally {
            System.setOut(originalOut)
        }
    }
}
