import callback.LetterCallback
import callback.MenuCallback
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
            registerUpdateHandler(LetterCallback(bot, gameStorage))
            registerUpdateHandler(MenuCallback(bot, gameStorage))
            registerUpdateHandler(StartInlineQuery(bot, mongoWrapper, gameStorage))

        }
    }
}

fun main() {
    val token = "token"
    val bot = Bot.fromToken(token)

    HangmanBot(bot).run()
}