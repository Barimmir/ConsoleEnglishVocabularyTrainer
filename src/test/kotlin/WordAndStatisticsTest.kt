package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class WordAndStatisticsTest {

    @Test
    fun `should create word correctly`() {
        val word = Word("hello", "привет", 3)
        
        assertEquals("hello", word.original)
        assertEquals("привет", word.translation)
        assertEquals(3, word.correctAnswersCount)
    }

    @Test
    fun `should allow modifying correctAnswersCount`() {
        val word = Word("test", "тест", 0)
        
        word.correctAnswersCount = 5
        assertEquals(5, word.correctAnswersCount)
        
        word.correctAnswersCount = -1
        assertEquals(-1, word.correctAnswersCount)
    }

    @Test
    fun `should create statistics correctly`() {
        val statistics = Statistics(10, 7, "70")
        
        assertEquals(10, statistics.totalCount)
        assertEquals(7, statistics.learnCount)
        assertEquals("70", statistics.percent)
    }

    @Test
    fun `should handle edge cases in statistics`() {
        val emptyStats = Statistics(0, 0, "0")
        assertEquals(0, emptyStats.totalCount)
        assertEquals(0, emptyStats.learnCount)
        assertEquals("0", emptyStats.percent)
        
        val fullStats = Statistics(100, 100, "100")
        assertEquals(100, fullStats.totalCount)
        assertEquals(100, fullStats.learnCount)
        assertEquals("100", fullStats.percent)
    }

    @Test
    fun `should handle words with special characters`() {
        val wordWithSpaces = Word("thank you", "спасибо", 2)
        assertEquals("thank you", wordWithSpaces.original)
        assertEquals("спасибо", wordWithSpaces.translation)
        
        val wordWithPunctuation = Word("hello!", "привет!", 1)
        assertEquals("hello!", wordWithPunctuation.original)
        assertEquals("привет!", wordWithPunctuation.translation)
    }
}
