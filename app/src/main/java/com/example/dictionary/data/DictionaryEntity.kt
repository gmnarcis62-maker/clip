package com.example.dictionary.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dictionary_entries",
    indices = [
        Index(value = ["normalizedWord"]),
        Index(value = ["word"]),
        Index(value = ["language"])
    ]
)
data class DictionaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val normalizedWord: String,
    val language: String = "fa", // "fa" or "en"
    val pronunciation: String = "",
    val partOfSpeech: String = "", // اسم, صفت, فعل, قید, Noun, Verb, etc.
    val definition: String,
    val shortDefinition: String = "",
    val root: String = "",
    val plural: String = "",
    val synonyms: String = "", // Comma-separated or JSON list
    val antonyms: String = "", // Comma-separated or JSON list
    val examples: String = "", // Comma-separated or JSON list
    val englishTranslation: String = "",
    val isFavorite: Boolean = false,
    val searchCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "personal_dictionary_entries",
    indices = [
        Index(value = ["normalizedWord"]),
        Index(value = ["word"])
    ]
)
data class PersonalDictionaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val normalizedWord: String,
    val definition: String,
    val synonyms: String = "",
    val translations: String = "",
    val note: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "dictionary_history",
    indices = [
        Index(value = ["normalizedQuery"]),
        Index(value = ["timestamp"])
    ]
)
data class DictionaryHistoryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val normalizedQuery: String,
    val language: String = "fa",
    val timestamp: Long = System.currentTimeMillis()
)
