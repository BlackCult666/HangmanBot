import callback.*
import database.MongoWrapper
import game.GameStorage
import inline.StartInlineQuery
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.LongPollingBot
import utils.WordProvider

class HangmanBot(
    bot: Bot
) : LongPollingBot(bot) {

    init {
        val mongoWrapper = MongoWrapper()
        val gameStorage = GameStorage()
        val wordProvider = WordProvider()

        // Italian words
        wordProvider.loadWords("Animali", "words/it/animals.txt", "it")
        wordProvider.loadWords("Paesi", "words/it/countries.txt", "it")

        // English words
        wordProvider.loadWords("Animals", "words/en/animals.txt", "en")
        wordProvider.loadWords("Countries", "words/en/countries.txt", "en")

        events.apply {
            registerUpdateHandler(LangCallback(bot, mongoWrapper))
            registerUpdateHandler(BackCallback(bot, mongoWrapper))
            registerUpdateHandler(StatsCallback(bot, mongoWrapper))
            registerUpdateHandler(MenuCallback(bot, mongoWrapper, gameStorage))
            registerUpdateHandler(LetterCallback(bot, mongoWrapper, gameStorage))
            registerUpdateHandler(StartInlineQuery(bot, mongoWrapper, gameStorage))
            registerUpdateHandler(CategoryCallback(bot, wordProvider, mongoWrapper, gameStorage))

        }
    }
}

fun main() {
    val token = "token"
    val bot = Bot.fromToken(token)

    HangmanBot(bot).run()
}