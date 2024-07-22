package inline

import database.MongoWrapper
import game.GameStorage
import io.github.ageofwar.telejam.Bot
import io.github.ageofwar.telejam.inline.InlineQuery
import io.github.ageofwar.telejam.inline.InlineQueryHandler
import io.github.ageofwar.telejam.inline.InlineQueryResultArticle
import io.github.ageofwar.telejam.inline.InputTextMessageContent
import io.github.ageofwar.telejam.methods.AnswerInlineQuery
import io.github.ageofwar.telejam.text.Text
import lang.Messages
import utils.getMenuKeyboard

class StartInlineQuery(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper,
    private val gameStorage: GameStorage
) : InlineQueryHandler {

    override fun onInlineQuery(inlineQuery: InlineQuery) {
        val sender = inlineQuery.sender

        val id = inlineQuery.id
        val data = inlineQuery.query

        val word = data.ifEmpty { "pesce" }
        val game = gameStorage.startGame(id, word)

        val lang = mongoWrapper.getUserLang(sender.id)

        val gameMessage = Text.parseHtml(
            Messages.getMessage(lang, "game_message")
                .replace("{word}", game.getHiddenWord())
                .replace("{errors}", game.errors.toString()
                )
        )

        val startMessage = Text.parseHtml(
            Messages.getMessage(lang, "start_message").replace("{user}", sender.firstName)
        )

        val answerInlineQuery = AnswerInlineQuery()
            .inlineQuery(inlineQuery)
            .cacheTime(0)
            .results(
                InlineQueryResultArticle(
                    "game",
                    Messages.getMessage(lang, "inline_game_title"),
                    InputTextMessageContent(gameMessage),
                    game.getKeyboard(),
                    ""
                ),

                InlineQueryResultArticle(
                    "start",
                    Messages.getMessage(lang, "inline_menu_title"),
                    InputTextMessageContent(startMessage),
                    getMenuKeyboard(lang),
                    ""
                ),
            )

        bot.execute(answerInlineQuery)
    }
}