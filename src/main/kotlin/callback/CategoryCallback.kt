package callback

import database.MongoWrapper
import game.GameStorage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import lang.Messages
import utils.WordProvider
import utils.answerCallback
import utils.editMessage
import utils.getBackButton

class CategoryCallback(
    private val bot: Bot,
    private val wordProvider: WordProvider,
    private val mongoWrapper: MongoWrapper,
    private val gameStorage: GameStorage
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("category")) return

        val lang = mongoWrapper.getUserLang(callbackQuery.sender.id)

        val selectedCategory = data.split("_")[1]
        val formattedCategory = selectedCategory.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }


        val randomWord = wordProvider.getRandomWord(selectedCategory, lang)
        if (randomWord == null) {
            bot.editMessage(callbackQuery, Messages.getMessage(lang, "error_on_start"), InlineKeyboardMarkup(getBackButton(lang)))
            bot.answerCallback(callbackQuery, "")
            return
        }

        val game = gameStorage.startGame(callbackQuery.id, randomWord)
        val gameMessage = Messages.getMessage(lang, "category_game_message")
            .replace("{category}", formattedCategory)
            .replace("{word}", game.getHiddenWord())
            .replace("{errors}", game.errors.toString()
            )

        bot.editMessage(callbackQuery, gameMessage, game.getKeyboard())
        bot.answerCallback(callbackQuery, "")
    }
}