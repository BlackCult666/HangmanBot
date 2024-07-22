package callback

import database.MongoWrapper
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton
import io.github.ageofwar.telejam.inline.InlineKeyboardButton
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import lang.Messages
import utils.answerCallback
import utils.editMessage
import utils.getBackButton

class LangCallback(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        val sender = callbackQuery.sender
        if (!data.startsWith("lang")) return

        val selectedLang = data.split("_")[1]

        if (!mongoWrapper.userExists(sender.id)) mongoWrapper.addUser(sender.id, sender.firstName)

        mongoWrapper.setLang(callbackQuery.sender.id, selectedLang)

        bot.editMessage(
            callbackQuery,
            Messages.getMessage(selectedLang, "lang_changed"),
            InlineKeyboardMarkup(getBackButton(selectedLang)))

        bot.answerCallback(callbackQuery, "")
    }
}