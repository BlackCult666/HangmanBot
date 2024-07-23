package utils

import java.io.File

class WordProvider {
    private val categoryWords = mutableMapOf<String, MutableMap<String, List<String>>>()
    private val categoryTranslations = mutableMapOf<String, MutableMap<String, String>>()

    fun loadWords(category: String, filePath: String, language: String) {
        val words = File(filePath).readLines()
        categoryWords.computeIfAbsent(category.lowercase()) { mutableMapOf() }
            .computeIfAbsent(language.lowercase()) { words }

        categoryTranslations.computeIfAbsent(category.lowercase()) { mutableMapOf() }
            .put(language.lowercase(), category)
    }

    fun getRandomWord(category: String, language: String): String? {
        val langWords = categoryWords[category.lowercase()] ?: return null
        val words = langWords[language.lowercase()] ?: return null
        return words.randomOrNull()
    }
}