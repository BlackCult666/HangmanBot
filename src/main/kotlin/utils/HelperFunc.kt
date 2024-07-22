package utils

import database.UserInfo
import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton
import io.github.ageofwar.telejam.inline.InlineKeyboardButton
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup
import lang.Messages

fun getStatsMessage(lang: String, topUsers: List<UserInfo>, sortBy: String): String {
    if (topUsers.isEmpty()) return Messages.getMessage(lang, "stats_no_users")

    val messageBuilder = StringBuilder()
    messageBuilder.append(Messages.getMessage(lang, "stats_header"))

    topUsers.forEach { user ->
        messageBuilder.append(
            when (sortBy) {
                "ratio" -> Messages.getMessage(lang, "stats_user_ratio")
                    .replace("{user}", user.firstName)
                    .replace("{guessedLetters}", user.correctLetters.toString())
                    .replace("{wrongLetters}", user.wrongLetters.toString())
                    .replace("{ratio}", user.ratio)
                else -> Messages.getMessage(lang, "stats_user_points")
                    .replace("{user}", user.firstName)
                    .replace("{guessedLetters}", user.correctLetters.toString())
                    .replace("{wrongLetters}", user.wrongLetters.toString())
                    .replace("{points}", user.points.toString())
            }
        )
    }

    return messageBuilder.toString()
}

fun getBackButton(lang: String) : InlineKeyboardButton {
    return CallbackDataInlineKeyboardButton(Messages.getMessage(lang, "back_button"), "back")
}

fun getMenuKeyboard(lang: String) : InlineKeyboardMarkup {
    val buttons = listOf(
        CallbackDataInlineKeyboardButton(Messages.getMessage(lang, "game_button"), "menu_match"),
        CallbackDataInlineKeyboardButton(Messages.getMessage(lang, "stats_button"), "menu_stats"),
        CallbackDataInlineKeyboardButton(Messages.getMessage(lang, "lang_button"), "menu_lang"),
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}

fun getStatsKeyboard(lang: String, type: String) : InlineKeyboardMarkup {

    val buttons = listOf(
        CallbackDataInlineKeyboardButton(Messages.getMessage(lang, "stats_${type}_button"), "stats_${type}"),
        getBackButton(lang),
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}


fun getLangKeyboard() : InlineKeyboardMarkup {
    val buttons = listOf(
        CallbackDataInlineKeyboardButton("English", "lang_en"),
        CallbackDataInlineKeyboardButton("Italiano", "lang_it")
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}