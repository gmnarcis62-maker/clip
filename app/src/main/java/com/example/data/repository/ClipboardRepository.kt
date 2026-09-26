package com.example.data.repository

import com.example.data.local.ClipboardDao
import com.example.data.local.ClipboardEntity
import kotlinx.coroutines.flow.Flow

class ClipboardRepository(private val dao: ClipboardDao) {

    val allItems: Flow<List<ClipboardEntity>> = dao.getAllItems()
    val pinnedItems: Flow<List<ClipboardEntity>> = dao.getPinnedItems()
    val favoriteItems: Flow<List<ClipboardEntity>> = dao.getFavoriteItems()

    fun search(query: String): Flow<List<ClipboardEntity>> = dao.searchItems(query)

    suspend fun saveCopiedText(text: String, isPinned: Boolean = false, category: String = "عمومی") {
        if (text.isBlank()) return
        val existing = dao.findByText(text.trim())
        if (existing != null) {
            dao.updateItem(existing.copy(createdAt = System.currentTimeMillis()))
        } else {
            dao.insertItem(
                ClipboardEntity(
                    text = text.trim(),
                    isPinned = isPinned,
                    category = category
                )
            )
        }
    }

    suspend fun addCustomSnippet(text: String, category: String = "شخصی", isPinned: Boolean = true) {
        if (text.isBlank()) return
        dao.insertItem(
            ClipboardEntity(
                text = text.trim(),
                isPinned = isPinned,
                isFavorite = true,
                category = category
            )
        )
    }

    suspend fun togglePin(id: Long, currentPinned: Boolean) {
        dao.setPinned(id, !currentPinned)
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        dao.setFavorite(id, !currentFavorite)
    }

    suspend fun update(item: ClipboardEntity) {
        dao.updateItem(item)
    }

    suspend fun delete(item: ClipboardEntity) {
        dao.deleteItem(item)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearHistory() {
        dao.clearUnpinned()
    }
}
