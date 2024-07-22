package game

class GameStorage {
    private var matches = mutableMapOf<String, Game>()

    fun getGameById(id: String) : Game? = matches[id]

    fun startGame(id: String, word: String) : Game {
        val game = Game(id, word)
        matches[id] = game

        return game
    }

    fun removeGame(id: String) {
        matches.remove(id)
    }

}