package callback

import database.MongoWrapper
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.callbacks.CallbackQuery
import io.github.ageofwar.telejam.callbacks.CallbackQueryHandler
import utils.answerCallback
import utils.editMessage
import utils.getStatsKeyboard
import utils.getStatsMessage

class StatsCallback(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper
) : CallbackQueryHandler {

    override fun onCallbackQuery(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data.get()
        if (!data.startsWith("stats")) return

        val lang = mongoWrapper.getUserLang(callbackQuery.sender.id)

        val category = data.split("_")[1]

        when (category) {
            "ratio" -> {
                val topUsers = mongoWrapper.getTopUsers("ratio")

                bot.editMessage(callbackQuery, getStatsMessage(lang, topUsers, "ratio"), getStatsKeyboard(lang, "points"))
                bot.answerCallback(callbackQuery , "")
            }

            "points" -> {
                val topUsers = mongoWrapper.getTopUsers("points")

                bot.editMessage(callbackQuery, getStatsMessage(lang, topUsers, "points"), getStatsKeyboard(lang, "ratio"))
                bot.answerCallback(callbackQuery, "")
            }
        }
    }
}