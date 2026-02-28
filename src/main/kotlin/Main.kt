package org.example

fun main() {
    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь: ${e.message}")
        return
    }

    while (true) {
        println(
            "========= МЕНЮ =========\n" +
                    "1 - Учить слова \n" +
                    "2 - Статистика\n" +
                    "3 - Сбросить прогресс\n" +
                    "0 - Выход"
        )
        val userInputMenu = readln().trim()
        when (userInputMenu) {
            "1" -> {
                while (true) {
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Все слова в словаре выучены")
                        return
                    }
                    println(question.asConsoleString())
                    val userInputAsk = readln().trim()
                    val userInputAskInt = userInputAsk.toIntOrNull()
                    if (userInputAskInt == null || userInputAskInt !in EXIT_CODE..question.answerOptions.size) {
                        println("Введите число от $ANSWER_INDEX_OFFSET до ${question.answerOptions.size} или $EXIT_CODE!")
                        continue
                    }
                    when (userInputAskInt) {
                        EXIT_CODE -> break
                        in ANSWER_INDEX_OFFSET..question.answerOptions.size -> {
                            if (trainer.checkAnswer(userInputAskInt)) {
                                println("Правильно")
                            } else {
                                println("Неправильно! ${question.correctAnswer.original} - это ${question.correctAnswer.translation}")
                            }
                        }
                    }
                }
            }

            "2" -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learnCount} из ${statistics.totalCount} слов | ${statistics.percent}%")
                println()
            }

            "3" -> {
                trainer.resetProgress()
                println("Прогресс сброшен")
            }

            "0" -> {
                println("Выход из программы")
                return
            }

            else -> {
                println("Введите '1','2','3' или '0'")
                continue
            }
        }
    }
}
