package com.example.dictionary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {

    // === Global Dictionary Queries ===

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<DictionaryEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DictionaryEntry): Long

    @Query("SELECT * FROM dictionary_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Long): DictionaryEntry?

    @Query("SELECT * FROM dictionary_entries WHERE normalizedWord = :normalized LIMIT 1")
    suspend fun getExactMatch(normalized: String): DictionaryEntry?

    @Query("SELECT * FROM dictionary_entries WHERE normalizedWord = :normalized AND language = :lang LIMIT 1")
    suspend fun getExactMatchByLang(normalized: String, lang: String): DictionaryEntry?

    @Query("SELECT * FROM dictionary_entries WHERE normalizedWord LIKE :prefix || '%' ORDER BY LENGTH(normalizedWord) ASC LIMIT :limit")
    suspend fun searchPrefix(prefix: String, limit: Int = 20): List<DictionaryEntry>

    @Query("SELECT * FROM dictionary_entries WHERE normalizedWord LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%' OR englishTranslation LIKE '%' || :query || '%' LIMIT :limit")
    suspend fun searchContains(query: String, limit: Int = 20): List<DictionaryEntry>

    @Query("SELECT * FROM dictionary_entries WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<DictionaryEntry>>

    @Query("UPDATE dictionary_entries SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE dictionary_entries SET searchCount = searchCount + 1 WHERE id = :id")
    suspend fun incrementSearchCount(id: Long)

    @Query("SELECT * FROM dictionary_entries ORDER BY id ASC")
    suspend fun getAllEntries(): List<DictionaryEntry>

    @Query("SELECT COUNT(*) FROM dictionary_entries")
    suspend fun getCount(): Int

    // === Personal Dictionary Queries ===

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalEntry(entry: PersonalDictionaryEntry): Long

    @Update
    suspend fun updatePersonalEntry(entry: PersonalDictionaryEntry)

    @Query("DELETE FROM personal_dictionary_entries WHERE id = :id")
    suspend fun deletePersonalEntry(id: Long)

    @Query("SELECT * FROM personal_dictionary_entries WHERE normalizedWord = :normalized LIMIT 1")
    suspend fun getPersonalExactMatch(normalized: String): PersonalDictionaryEntry?

    @Query("SELECT * FROM personal_dictionary_entries WHERE normalizedWord LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    suspend fun searchPersonalEntries(query: String): List<PersonalDictionaryEntry>

    @Query("SELECT * FROM personal_dictionary_entries ORDER BY createdAt DESC")
    fun getAllPersonalEntriesFlow(): Flow<List<PersonalDictionaryEntry>>

    // === History Queries ===

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entry: DictionaryHistoryEntry): Long

    @Query("SELECT * FROM dictionary_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistoryFlow(limit: Int = 30): Flow<List<DictionaryHistoryEntry>>

    @Query("DELETE FROM dictionary_history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("DELETE FROM dictionary_history")
    suspend fun clearHistory()
}
