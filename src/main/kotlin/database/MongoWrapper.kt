package database

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Sorts
import org.bson.Document
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class MongoWrapper {
    private val client: MongoClient = MongoClients.create("mongodb://localhost:27017")
    private val database: MongoDatabase = client.getDatabase("hangmanBot")
    private var collection: MongoCollection<Document> = database.getCollection("users")

    fun addUser(id: Long, firstName: String) {
        val document = Document("id", id)
        document.append("firstName", firstName)
        document.append("locale", "en")
        document.append("correctLetters", 0)
        document.append("wrongLetters", 0)
        document.append("points", 0.0)
        document.append("ratio", "NDA")
        collection.insertOne(document)
    }

    fun setLang(id: Long, lang: String) {
        val filter = Document("id", id)
        val update = BasicDBObject("\$set", BasicDBObject("locale", lang))
        collection.updateOne(filter, update)
    }

    fun userExists(id: Long): Boolean {
        val player = collection.find(Document("id", id)).first()
        return player != null
    }

    fun getUserInfo(id: Long): UserInfo {
        val user = collection.find(Document("id", id)).first()
        val firstName = user?.getString("firstName") ?: ""
        val locale = user?.getString("locale") ?: "en"
        val correctLetters = user?.getInteger("correctLetters") ?: 0
        val wrongLetters = user?.getInteger("wrongLetters") ?: 0
        val points = user?.getDouble("points") ?: 0.0
        val ratio = user?.getString("ratio") ?: "NDA"

        return UserInfo(id, firstName, locale, correctLetters, wrongLetters, points, ratio)
    }

    fun getTopUsers(field: String): List<UserInfo> {
        val topPlayers = collection.find()
            .sort(Sorts.descending(field))
            .limit(4)
            .map { doc ->
                UserInfo(
                    id = doc.getLong("id"),
                    firstName = doc.getString("firstName"),
                    locale = doc.getString("locale"),
                    correctLetters = doc.getInteger("correctLetters"),
                    wrongLetters = doc.getInteger("wrongLetters"),
                    points = doc.getDouble("points"),
                    ratio = doc.getString("ratio")
                )
            }
            .toList()

        return topPlayers
    }

    fun updateStats(id: Long, correct: Boolean) {
        val user = collection.find(Document("id", id)).first()
        user?.let {
            val correctLetters = it.getInteger("correctLetters")
            val wrongLetters = it.getInteger("wrongLetters")
            val points = it.getDouble("points")

            val newPoints = if (correct) points + 1 else maxOf(0.0, points - 0.5)

            val newCorrectLetters = if (correct) correctLetters + 1 else correctLetters
            val newWrongLetters = if (!correct) wrongLetters + 1 else wrongLetters

            val ratio = if (newWrongLetters > 0) newCorrectLetters.toDouble() / newWrongLetters else newCorrectLetters.toDouble()

            val symbols = DecimalFormatSymbols().apply {
                decimalSeparator = '.'
            }

            val decimalFormat = DecimalFormat("#.00", symbols)
            val formattedRatio = decimalFormat.format(ratio)

            val update = BasicDBObject("\$set", BasicDBObject(
                    mapOf(
                        "points" to newPoints,
                        "correctLetters" to newCorrectLetters,
                        "wrongLetters" to newWrongLetters,
                        "ratio" to formattedRatio
                    )
                )
            )
            collection.updateOne(Document("id", id), update)
        }
    }



    fun getUserLang(id: Long) : String {
        val user = collection.find(Document("id", id)).first()
        val locale = user?.getString("locale") ?: "en"
        return locale
    }
}

data class UserInfo(
    val id: Long,
    val firstName: String,
    val locale: String,
    val correctLetters: Int,
    val wrongLetters: Int,
    val points: Double,
    val ratio: String,
)