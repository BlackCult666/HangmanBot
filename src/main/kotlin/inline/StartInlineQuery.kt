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
import utils.getMenuKeyboard

class StartInlineQuery(
    private val bot: Bot,
    private val mongoWrapper: MongoWrapper,
    private val gameStorage: GameStorage
) : InlineQueryHandler {

    override fun onInlineQuery(inlineQuery: InlineQuery) {
        val sender = inlineQuery.sender

        if (!mongoWrapper.userExists(sender.id)) mongoWrapper.addUser(sender.id, sender.firstName)

        val id = inlineQuery.id
        val data = inlineQuery.query

        val word = data.ifEmpty { "pesce" }
        val game = gameStorage.startGame(id, word)

        val textMessage = Text.parseHtml(
            "<b>Word:</b> ${game.getHiddenWord()}\n\n<b>Errors:</b> 0/5"
        )

        val startMessage = Text.parseHtml(
            "\uD83D\uDC4B\uD83C\uDFFB Hi <b>${sender.firstName}</b>,\ndo you want to start a new <b>game</b> or open the <b>settings</b>?"
        )

        val answerInlineQuery = AnswerInlineQuery()
            .inlineQuery(inlineQuery)
            .results(
                InlineQueryResultArticle(
                    "game",
                    "Start a new game!",
                    InputTextMessageContent(textMessage),
                    game.getKeyboard(),
                    "Click here to play."
                ),

                InlineQueryResultArticle(
                    "start",
                    "Open the menu",
                    InputTextMessageContent(startMessage),
                    getMenuKeyboard(),
                    "What do you want to do?"
                ),
            )

        bot.execute(answerInlineQuery)
    }
}