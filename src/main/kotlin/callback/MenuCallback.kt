package callback

import database.MongoWrapper
import game.GameStorage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import lang.Messages
import utils.*

class MenuCallback(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper,
    private val gameStorage: GameStorage
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("menu")) return

        val lang = mongoWrapper.getUserLang(callbackQuery.sender.id)
        val action = data.split("_")[1]
        when (action) {
            "match" -> handleMatchCallback(callbackQuery, lang)
            "stats" -> handleStatsCallback(callbackQuery, lang)
            "lang" -> handleLangCallback(callbackQuery, lang)
        }
    }

    private fun handleMatchCallback(callbackQuery: CallbackQuery, lang: String) {
        val textMessage = Messages.getMessage(lang, "category_message")

        bot.editMessage(callbackQuery, textMessage, getCategoryKeyboard(lang))
        bot.answerCallback(callbackQuery, "")
    }

    private fun handleStatsCallback(callbackQuery: CallbackQuery, lang: String) {
        val topUsers = mongoWrapper.getTopUsers("points")

        bot.editMessage(callbackQuery, getStatsMessage(lang, topUsers, "points"), getStatsKeyboard(lang, "ratio"))
        bot.answerCallback(callbackQuery, "")
    }

    private fun handleLangCallback(callbackQuery: CallbackQuery, lang: String) {
        bot.editMessage(callbackQuery, Messages.getMessage(lang, "lang_message"), getLangKeyboard())
        bot.answerCallback(callbackQuery, "")
    }
}