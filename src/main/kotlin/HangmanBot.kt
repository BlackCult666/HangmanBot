import callback.*
import database.MongoWrapper
import game.GameStorage
import inline.StartInlineQuery
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot

class HangmanBot(
    bot: Bot
) : LongPollingBot(bot) {

    init {
        val mongoWrapper = MongoWrapper()
        val gameStorage = GameStorage()

        events.apply {
            registerUpdateHandler(LangCallback(bot, mongoWrapper))
            registerUpdateHandler(BackCallback(bot, mongoWrapper))
            registerUpdateHandler(StatsCallback(bot, mongoWrapper))
            registerUpdateHandler(MenuCallback(bot, mongoWrapper, gameStorage))
            registerUpdateHandler(LetterCallback(bot, mongoWrapper, gameStorage))
            registerUpdateHandler(StartInlineQuery(bot, mongoWrapper, gameStorage))

        }
    }
}

fun main() {
    val token = "token"
    val bot = Bot.fromToken(token)

    HangmanBot(bot).run()
}