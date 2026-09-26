package com.example.dictionary.domain

import com.example.dictionary.data.DictionaryDao
import com.example.dictionary.data.DictionaryEntry
import com.example.dictionary.data.DictionaryHistoryEntry
import com.example.dictionary.data.DictionarySeedData
import com.example.dictionary.data.PersonalDictionaryEntry
import com.example.dictionary.engine.FuzzySearchEngine
import com.example.dictionary.engine.PersianTextNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed class DictionaryItemResult {
    data class Global(val entry: DictionaryEntry) : DictionaryItemResult()
    data class Personal(val entry: PersonalDictionaryEntry) : DictionaryItemResult()

    val displayWord: String
        get() = when (this) {
            is Global -> entry.word
            is Personal -> entry.word
        }

    val displayDefinition: String
        get() = when (this) {
            is Global -> entry.definition
            is Personal -> entry.definition
        }

    val isFavorite: Boolean
        get() = when (this) {
            is Global -> entry.isFavorite
            is Personal -> entry.isFavorite
        }
}

class DictionaryRepository(private val dao: DictionaryDao) {

    suspend fun ensureDatabasePopulated() {
        withContext(Dispatchers.IO) {
            if (dao.getCount() == 0) {
                dao.insertAll(DictionarySeedData.INITIAL_ENTRIES)
            }
        }
    }

    /**
     * Searches across Personal Dictionary (highest priority) and Global Dictionary.
     * Supports Exact, Prefix, Substring, and Fuzzy matching.
     */
    suspend fun search(query: String, language: String? = null): List<DictionaryItemResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        ensureDatabasePopulated()

        val normalized = PersianTextNormalizer.normalize(query)
        val results = mutableListOf<DictionaryItemResult>()

        // 1. Record in History
        dao.insertHistory(
            DictionaryHistoryEntry(
                query = query.trim(),
                normalizedQuery = normalized,
                language = language ?: "fa"
            )
        )

        // 2. Search Personal Dictionary FIRST (Top Priority)
        val personalMatches = dao.searchPersonalEntries(normalized)
        personalMatches.forEach {
            results.add(DictionaryItemResult.Personal(it))
        }

        // 3. Search Global Dictionary
        // Exact Match
        val exact = if (language != null) {
            dao.getExactMatchByLang(normalized, language)
        } else {
            dao.getExactMatch(normalized)
        }

        if (exact != null && results.none { it.displayWord.equals(exact.word, ignoreCase = true) }) {
            dao.incrementSearchCount(exact.id)
            results.add(DictionaryItemResult.Global(exact))
        }

        // Prefix Matches
        val prefixMatches = dao.searchPrefix(normalized, limit = 15)
        prefixMatches.forEach { entry ->
            if (results.none { it.displayWord.equals(entry.word, ignoreCase = true) }) {
                results.add(DictionaryItemResult.Global(entry))
            }
        }

        // Contains / Substring Matches
        val containsMatches = dao.searchContains(normalized, limit = 15)
        containsMatches.forEach { entry ->
            if (results.none { it.displayWord.equals(entry.word, ignoreCase = true) }) {
                results.add(DictionaryItemResult.Global(entry))
            }
        }

        // 4. Fuzzy Levenshtein Search if few results
        if (results.size < 5) {
            val all = dao.getAllEntries()
            for (entry in all) {
                if (results.size >= 15) break
                if (results.none { it.displayWord.equals(entry.word, ignoreCase = true) }) {
                    if (FuzzySearchEngine.isFuzzyMatch(normalized, entry.normalizedWord)) {
                        results.add(DictionaryItemResult.Global(entry))
                    }
                }
            }
        }

        results
    }

    suspend fun getEntryById(id: Long): DictionaryEntry? = withContext(Dispatchers.IO) {
        dao.getEntryById(id)
    }

    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        dao.updateFavorite(id, !currentStatus)
    }

    fun getFavoritesFlow(): Flow<List<DictionaryEntry>> = dao.getFavorites()

    fun getHistoryFlow(): Flow<List<DictionaryHistoryEntry>> = dao.getRecentHistoryFlow()

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteHistoryItem(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        dao.clearHistory()
    }

    // Personal Dictionary operations
    fun getPersonalEntriesFlow(): Flow<List<PersonalDictionaryEntry>> = dao.getAllPersonalEntriesFlow()

    suspend fun addPersonalWord(word: String, definition: String, synonyms: String = "", note: String = ""): Long = withContext(Dispatchers.IO) {
        val entry = PersonalDictionaryEntry(
            word = word.trim(),
            normalizedWord = PersianTextNormalizer.normalize(word),
            definition = definition.trim(),
            synonyms = synonyms.trim(),
            note = note.trim()
        )
        dao.insertPersonalEntry(entry)
    }

    suspend fun deletePersonalWord(id: Long) = withContext(Dispatchers.IO) {
        dao.deletePersonalEntry(id)
    }

    /**
     * Deterministic offline Word of the Day based on day of year.
     */
    suspend fun getWordOfTheDay(): DictionaryEntry? = withContext(Dispatchers.IO) {
        ensureDatabasePopulated()
        val all = dao.getAllEntries()
        if (all.isEmpty()) return@withContext null

        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val index = (dayOfYear + 7) % all.size
        all[index]
    }
}
