package callback

import utils.answerCallback
import utils.editMessage
import game.Game
import game.GameResult
import game.GameStorage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler

class LetterCallback(
    private val bot: Bot,
    private val gameStorage: GameStorage
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("letter")) return

        val id = data.split("_")[1]
        val letter = data.split("_")[2][0]
        val game = gameStorage.getGameById(id)

        if (game == null) {
            bot.answerCallback(callbackQuery, "This match has expired.")
            return
        }

        val guess : GameResult = game.guess(letter.lowercaseChar())

        when (guess) {
            GameResult.WRONG_LETTER -> handleWrongLetter(callbackQuery, game)
            GameResult.CORRECT_LETTER -> handleCorrectLetter(callbackQuery, game)
            GameResult.LETTER_USED -> handleUsedLetter(callbackQuery)
            GameResult.GAME_WON -> handleGameWon(callbackQuery, id)
            GameResult.GAME_LOST -> handleGameLost(callbackQuery, id)
        }
    }

    private fun handleWrongLetter(callbackQuery: CallbackQuery, game: Game) {
        bot.editMessage(callbackQuery, "<b>Word:</b> ${game.getHiddenWord()}\n\n<b>Errors:</b> ${game.errors}/5", game.getKeyboard())
        bot.answerCallback(callbackQuery, "Letter is wrong.")
    }

    private fun handleCorrectLetter(callbackQuery: CallbackQuery, game: Game) {
        bot.editMessage(callbackQuery, "<b>Word:</b> ${game.getHiddenWord()}\n\n<b>Errors:</b> ${game.errors}/5", game.getKeyboard())
        bot.answerCallback(callbackQuery, "You guessed that letter.")
    }

    private fun handleUsedLetter(callbackQuery: CallbackQuery) {
        bot.answerCallback(callbackQuery, "You've already used that letter.")
    }

    private fun handleGameWon(callbackQuery: CallbackQuery, id: String) {
        bot.editMessage(callbackQuery, "Finished")
        bot.answerCallback(callbackQuery, "You won the game.")

        gameStorage.removeGame(id)
    }

    private fun handleGameLost(callbackQuery: CallbackQuery, id: String) {
        bot.editMessage(callbackQuery, "Lost")
        bot.answerCallback(callbackQuery, "You have lost!")

        gameStorage.removeGame(id)
    }

}