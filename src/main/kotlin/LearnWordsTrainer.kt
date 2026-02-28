package org.example

import java.io.File

data class Word(
    val original: String,
    val translation: String,
    var correctAnswersCount: Int,
)

data class Statistics(
    val totalCount: Int,
    val learnCount: Int,
    val percent: String,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
    val correctTranslations: List<String>,
    val answerOptions: List<String>,
)

class LearnWordsTrainer(
    private val fileName: String = "words.txt",
) {
    private var question: Question? = null
    val dictionary = loadDictionary()

    private fun loadDictionary(): MutableList<Word> {
        val wordsFile = File(fileName)

        if (!wordsFile.exists()) {
            throw Exception("Файл словаря '$fileName' не найден")
        }

        val lines: List<String> = wordsFile.readLines()

        val dictionary = mutableListOf<Word>()

        for (line in lines) {
            if (line.isBlank()) continue

            val split = line.split("|")
            if (split.size < 3) {
                println("Пропуск некорректной строки: $line")
                continue
            }

            try {
                val word = Word(
                    original = split[0].trim(),
                    translation = split[1].trim(),
                    correctAnswersCount = split[2].trim().toIntOrNull() ?: EXIT_CODE
                )
                if (word.original.isNotEmpty() && word.translation.isNotEmpty()) {
                    dictionary.add(word)
                }
            } catch (e: Exception) {
                println("Ошибка при обработке строки '$line': ${e.message}")
            }
        }
        return dictionary
    }

    fun saveDictionary() {
        try {
            val wordsFile = File(fileName)
            val lines = dictionary.map { "${it.original}|${it.translation}|${it.correctAnswersCount}" }
            wordsFile.writeText(lines.joinToString("\n"))
        } catch (e: Exception) {
            println("Ошибка при сохранении словаря: ${e.message}")
        }
    }

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnCount = dictionary.filter { it.correctAnswersCount >= NEED_COUNT_TO_LEARN }.size
        val percent = (learnCount * MAX_PERCENTAGE / totalCount).toString()
        return Statistics(
            totalCount,
            learnCount,
            percent
        )
    }

    fun getNextQuestion(): Question? {
        val notLearnedWords = dictionary.filter { it.correctAnswersCount < NEED_COUNT_TO_LEARN }
        if (notLearnedWords.isEmpty()) return null

        val questionWords = notLearnedWords.shuffled().take(NUMBER_OF_WORDS_TO_LEARN)
        val correctAnswer = questionWords.random()

        val correctTranslations = questionWords.map { it.translation }
        val answerOptions = if (correctTranslations.size >= NUMBER_OF_WORDS_TO_LEARN) {
            correctTranslations.shuffled().take(NUMBER_OF_WORDS_TO_LEARN)
        } else {
            val learnedWords = dictionary.filter { it.correctAnswersCount >= NEED_COUNT_TO_LEARN }
            val additionalTranslations = learnedWords.shuffled()
                .take(NUMBER_OF_WORDS_TO_LEARN - correctTranslations.size)
                .map { it.translation }
            (correctTranslations + additionalTranslations).shuffled()
        }

        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
            correctTranslations = correctTranslations,
            answerOptions = answerOptions
        )
        return question
    }

    fun checkAnswer(userInputAskInt: Int): Boolean {
        if (question == null) return false
        val correctAnswerId =
            (question?.answerOptions?.indexOf(question?.correctAnswer?.translation)?.plus(ANSWER_INDEX_OFFSET))
        if (correctAnswerId == userInputAskInt) {
            question?.correctAnswer?.correctAnswersCount++
            saveDictionary()
            return true
        } else {
            return false
        }
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}

fun Question.asConsoleString(): String {
    println("\n${this.correctAnswer.original}:")
    this.answerOptions.forEachIndexed { index, option ->
        println("${index + ANSWER_INDEX_OFFSET} - $option")
    }
    println(
        "----------\n" +
                "$EXIT_CODE - Меню"
    )
    return String()
}

const val NEED_COUNT_TO_LEARN = 3
const val MAX_PERCENTAGE = 100
const val NUMBER_OF_WORDS_TO_LEARN = 4
const val ANSWER_INDEX_OFFSET = 1
const val EXIT_CODE = 0
