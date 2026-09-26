package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {

    @Query("SELECT * FROM clipboard_items ORDER BY isPinned DESC, createdAt DESC")
    fun getAllItems(): Flow<List<ClipboardEntity>>

    @Query("SELECT * FROM clipboard_items WHERE isPinned = 1 ORDER BY createdAt DESC")
    fun getPinnedItems(): Flow<List<ClipboardEntity>>

    @Query("SELECT * FROM clipboard_items WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteItems(): Flow<List<ClipboardEntity>>

    @Query("SELECT * FROM clipboard_items WHERE text LIKE '%' || :query || '%' ORDER BY isPinned DESC, createdAt DESC")
    fun searchItems(query: String): Flow<List<ClipboardEntity>>

    @Query("SELECT * FROM clipboard_items WHERE text = :text LIMIT 1")
    suspend fun findByText(text: String): ClipboardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ClipboardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ClipboardEntity>)

    @Update
    suspend fun updateItem(item: ClipboardEntity)

    @Delete
    suspend fun deleteItem(item: ClipboardEntity)

    @Query("DELETE FROM clipboard_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM clipboard_items WHERE isPinned = 0")
    suspend fun clearUnpinned()

    @Query("UPDATE clipboard_items SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: Long, isPinned: Boolean)

    @Query("UPDATE clipboard_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
}
