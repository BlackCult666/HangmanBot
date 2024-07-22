package database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

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
        collection.insertOne(document)
    }

    fun userExists(id: Long): Boolean {
        val player = collection.find(Document("id", id)).first()
        return player != null
    }

    fun getUserInfo(id: Long): UserInfo {
        val user = collection.find(Document("id", id)).first()
        val firstName = user?.getString("firstName") ?: ""
        val locale = user?.getString("locale") ?: ""
        val correctLetters = user?.getInteger("correctLetters") ?: 0
        val wrongLetters = user?.getInteger("wrongLetters") ?: 0

        return UserInfo(id, firstName, locale, correctLetters, wrongLetters)
    }
}

data class UserInfo(
    val id: Long,
    val firstName: String,
    val locale: String,
    val correctLetters: Int,
    val wrongLetters: Int,
)