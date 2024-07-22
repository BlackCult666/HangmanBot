package game

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup

class Game(
    private val id: String,
    private val word: String
) {
    private val guessedWord = CharArray(word.length) { if (word[it] == ' ') ' ' else '_' }
    private val usedLetters = mutableSetOf<Char>()

    var errors = 0

    fun guess(letter: Char) : GameResult {
        if (letter in usedLetters) return GameResult.LETTER_USED

        usedLetters.add(letter)

        val indices = word.indices.filter { word[it] == letter }
        if (indices.isEmpty()) {
            errors += 1
            if (errors < 5) return GameResult.WRONG_LETTER

            return GameResult.GAME_LOST
        }

        for (index in indices) guessedWord[index] = letter

        return if (guessedWord.joinToString("") == word) {
            GameResult.GAME_WON
        } else {
            GameResult.CORRECT_LETTER
        }
    }

    fun getHiddenWord(): String {
        return guessedWord.joinToString(" ")
    }

    fun getKeyboard() : InlineKeyboardMarkup {
        return createLettersKeyboard()
    }

    private fun createLettersKeyboard() : InlineKeyboardMarkup {
        val alphabet = ('A' .. 'Z').toList()

        val buttons = alphabet.map { letter ->
            val text = if (letter.lowercaseChar() in usedLetters) "-" else letter.toString()
            CallbackDataInlineKeyboardButton(text, "letter_${id}_$letter")
        }

        return InlineKeyboardMarkup.fromColumns(6, buttons)
    }
}