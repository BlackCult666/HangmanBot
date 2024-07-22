package callback

import database.MongoWrapper
import utils.answerCallback
import utils.editMessage
import game.Game
import game.GameResult
import game.GameStorage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import lang.Messages

class LetterCallback(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper,
    private val gameStorage: GameStorage
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        val sender = callbackQuery.sender

        if (!data.startsWith("letter")) return
        if (!mongoWrapper.userExists(sender.id)) mongoWrapper.addUser(sender.id, sender.firstName)

        val id = data.split("_")[1]
        val letter = data.split("_")[2][0]
        val game = gameStorage.getGameById(id)

        val lang = mongoWrapper.getUserLang(callbackQuery.sender.id)

        if (game == null) {
            bot.answerCallback(callbackQuery, Messages.getMessage(lang, "expired_match"))
            return
        }

        val guess : GameResult = game.guess(letter.lowercaseChar())

        when (guess) {
            GameResult.WRONG_LETTER -> handleWrongLetter(callbackQuery, game, lang)
            GameResult.CORRECT_LETTER -> handleCorrectLetter(callbackQuery, game, lang)
            GameResult.LETTER_USED -> handleUsedLetter(callbackQuery, lang)
            GameResult.GAME_WON -> handleGameWon(callbackQuery, id, lang)
            GameResult.GAME_LOST -> handleGameLost(callbackQuery, id, lang)
        }
    }

    private fun handleWrongLetter(callbackQuery: CallbackQuery, game: Game, lang: String) {
        val gameMessage = Messages.getMessage(lang, "game_message")
            .replace("{word}", game.getHiddenWord())
            .replace("{errors}", game.errors.toString())

        mongoWrapper.updateStats(callbackQuery.sender.id, false)

        bot.editMessage(callbackQuery, gameMessage, game.getKeyboard())
        bot.answerCallback(callbackQuery, Messages.getMessage(lang, "wrong_letter_query"))
    }

    private fun handleCorrectLetter(callbackQuery: CallbackQuery, game: Game, lang: String) {
        val gameMessage = Messages.getMessage(lang, "game_message")
            .replace("{word}", game.getHiddenWord())
            .replace("{errors}", game.errors.toString())

        mongoWrapper.updateStats(callbackQuery.sender.id, true)

        bot.editMessage(callbackQuery, gameMessage, game.getKeyboard())
        bot.answerCallback(callbackQuery, Messages.getMessage(lang, "correct_letter_query"))
    }

    private fun handleUsedLetter(callbackQuery: CallbackQuery, lang: String) {
        bot.answerCallback(callbackQuery, Messages.getMessage(lang, "used_letter_query"))
    }

    private fun handleGameWon(callbackQuery: CallbackQuery, id: String, lang: String) {
        mongoWrapper.updateStats(callbackQuery.sender.id, true)

        bot.editMessage(callbackQuery, Messages.getMessage(lang, "won_message"))
        bot.answerCallback(callbackQuery, Messages.getMessage(lang, "won_query"))

        gameStorage.removeGame(id)
    }

    private fun handleGameLost(callbackQuery: CallbackQuery, id: String, lang: String) {
        mongoWrapper.updateStats(callbackQuery.sender.id, false)

        bot.editMessage(callbackQuery, Messages.getMessage(lang, "lost_message"))
        bot.answerCallback(callbackQuery, Messages.getMessage(lang, "lost_query"))

        gameStorage.removeGame(id)
    }

}