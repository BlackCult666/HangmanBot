package callback

import database.MongoWrapper
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.text.Text
import lang.Messages
import utils.answerCallback
import utils.editMessage
import utils.getMenuKeyboard

class BackCallback(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("back")) return

        val lang = mongoWrapper.getUserLang(callbackQuery.sender.id)

        val startMessage = Messages.getMessage(lang, "start_message")
            .replace("{user}", callbackQuery.sender.firstName)

        bot.editMessage(callbackQuery, startMessage, getMenuKeyboard(lang))
        bot.answerCallback(callbackQuery, "")
    }
}