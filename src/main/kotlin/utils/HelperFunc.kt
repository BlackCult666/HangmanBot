package utils

import io.github.ageofwar.telejam.inline.CallbackDataInlineKeyboardButton
import io.github.ageofwar.telejam.replymarkups.InlineKeyboardMarkup

fun getMenuKeyboard() : InlineKeyboardMarkup {
    val buttons = listOf(
        CallbackDataInlineKeyboardButton("New match", "menu_match"),
        CallbackDataInlineKeyboardButton("Stats", "menu_stats"),
        CallbackDataInlineKeyboardButton("Settings", "menu_settings"),
    )

    return InlineKeyboardMarkup.fromColumns(2, buttons)
}