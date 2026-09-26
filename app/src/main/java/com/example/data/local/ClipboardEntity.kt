package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clipboard_items")
data class ClipboardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val category: String = "عمومی"
)
