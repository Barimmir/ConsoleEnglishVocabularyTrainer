package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class LearnWordsTrainerTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var testFileName: String
    private lateinit var trainer: LearnWordsTrainer

    @BeforeEach
    fun setUp() {
        testFileName = tempDir.resolve("test_words.txt").toString()
        createTestFile()
        trainer = LearnWordsTrainer(testFileName)
    }

    private fun createTestFile() {
        val testContent = """
            hello|привет|0
            dog|собака|1
            cat|кошка|2
            thank you|спасибо|3
            book|книга|0
        """.trimIndent()
        File(testFileName).writeText(testContent)
    }

    @Test
    fun `should load dictionary correctly`() {
        val dictionary = trainer.dictionary
        assertEquals(5, dictionary.size)
        assertEquals("hello", dictionary[0].original)
        assertEquals("привет", dictionary[0].translation)
        assertEquals(0, dictionary[0].correctAnswersCount)
    }

    @Test
    fun `should calculate statistics correctly`() {
        val statistics = trainer.getStatistics()
        assertEquals(5, statistics.totalCount)
        assertEquals(1, statistics.learnCount)
        assertEquals("20", statistics.percent)
    }

    @Test
    fun `should return question when there are unlearned words`() {
        val question = trainer.getNextQuestion()
        assertNotNull(question)
        assertEquals(4, question?.variants?.size)
        assertEquals(4, question?.answerOptions?.size)
        assertTrue(question?.variants?.all { it.correctAnswersCount < 3 } == true)
    }

    @Test
    fun `should return null when all words are learned`() {

        trainer.dictionary.forEach { it.correctAnswersCount = 3 }
        
        val question = trainer.getNextQuestion()
        assertNull(question)
    }

    @Test
    fun `should check correct answer`() {
        val question = trainer.getNextQuestion()
        requireNotNull(question)
        
        val correctAnswerIndex = question.answerOptions.indexOf(question.correctAnswer.translation) + 1
        val initialCount = question.correctAnswer.correctAnswersCount
        val result = trainer.checkAnswer(correctAnswerIndex)
        
        assertTrue(result)
        assertEquals(initialCount + 1, question.correctAnswer.correctAnswersCount)
    }

    @Test
    fun `should check incorrect answer`() {
        val question = trainer.getNextQuestion()
        requireNotNull(question)

        val incorrectAnswerIndex = question.answerOptions
            .indexOfFirst { it != question.correctAnswer.translation } + 1
        
        val initialCount = question.correctAnswer.correctAnswersCount
        val result = trainer.checkAnswer(incorrectAnswerIndex)
        
        assertFalse(result)
        assertEquals(initialCount, question.correctAnswer.correctAnswersCount)
    }

    @Test
    fun `should reset progress`() {

        trainer.dictionary[0].correctAnswersCount = 2
        trainer.dictionary[1].correctAnswersCount = 3
        
        trainer.resetProgress()
        
        trainer.dictionary.forEach { word ->
            assertEquals(0, word.correctAnswersCount)
        }
    }

    @Test
    fun `should handle malformed lines gracefully`() {
        val malformedContent = """
            hello|привет|0
            invalid_line
            dog|собака|1
            incomplete|перевод
            cat|кошка|two
            valid|word|2
        """.trimIndent()
        
        val malformedFileName = tempDir.resolve("malformed.txt").toString()
        File(malformedFileName).writeText(malformedContent)
        
        val malformedTrainer = LearnWordsTrainer(malformedFileName)
        val dictionary = malformedTrainer.dictionary
        
        assertEquals(4, dictionary.size)
        assertEquals("hello", dictionary[0].original)
        assertEquals("dog", dictionary[1].original)
        assertEquals("cat", dictionary[2].original)
        assertEquals("valid", dictionary[3].original)
    }

    @Test
    fun `should throw exception when file does not exist`() {
        val nonExistentFile = tempDir.resolve("nonexistent.txt").toString()
        
        assertThrows(Exception::class.java) {
            LearnWordsTrainer(nonExistentFile)
        }
    }

    @Test
    fun `should save dictionary correctly`() {

        trainer.dictionary[0].correctAnswersCount = 5
        
        trainer.saveDictionary()
        
        val fileContent = File(testFileName).readLines()
        assertEquals("hello|привет|5", fileContent[0])
        assertEquals("dog|собака|1", fileContent[1])
    }

    @Test
    fun `should handle empty and blank lines`() {
        val contentWithBlanks = """
            hello|привет|0
            
            dog|собака|1
            
            cat|кошка|2
        """.trimIndent()
        
        val blanksFileName = tempDir.resolve("blanks.txt").toString()
        File(blanksFileName).writeText(contentWithBlanks)
        
        val blanksTrainer = LearnWordsTrainer(blanksFileName)
        val dictionary = blanksTrainer.dictionary
        
        assertEquals(3, dictionary.size)
    }
}
