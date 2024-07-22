package utils

import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.methods.AnswerCallbackQuery
import io.github.ageofwar.telejam.methods.EditMessageText
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import io.github.ageofwar.telejam.text.Text

fun Bot.answerCallback(callbackQuery: CallbackQuery, text: String) {
    val answerCallbackQuery = AnswerCallbackQuery()
        .callbackQuery(callbackQuery)
        .text(text)

    execute(answerCallbackQuery)

}

fun Bot.editMessage(callbackQuery: CallbackQuery, text: String, replyMarkup: InlineKeyboardMarkup? = null) {
    val editMessage = EditMessageText()
        .callbackQuery(callbackQuery)
        .text(Text.parseHtml(text))
        .replyMarkup(replyMarkup)

    execute(editMessage)
}