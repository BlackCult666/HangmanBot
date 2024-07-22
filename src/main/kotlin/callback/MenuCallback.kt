package callback

import game.GameStorage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import utils.answerCallback
import utils.editMessage

class MenuCallback(
    private val bot: Bot,
    private val gameStorage: GameStorage
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("menu")) return

        val action = data.split("_")[1]
        when (action) {
            "match" -> handleMatchCallback(callbackQuery)
        }
    }

    private fun handleMatchCallback(callbackQuery: CallbackQuery) {
        val game = gameStorage.startGame(callbackQuery.id, "pesce")

        val textMessage = "<b>Word:</b> ${game.getHiddenWord()}\n\n<b>Errors:</b> 0/5"

        bot.editMessage(callbackQuery, textMessage, game.getKeyboard())
        bot.answerCallback(callbackQuery, "New match started.")
    }
}