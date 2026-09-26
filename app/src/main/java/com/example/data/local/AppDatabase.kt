package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dictionary.data.DictionaryDao
import com.example.dictionary.data.DictionaryEntry
import com.example.dictionary.data.DictionaryHistoryEntry
import com.example.dictionary.data.DictionarySeedData
import com.example.dictionary.data.PersonalDictionaryEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ClipboardEntity::class,
        DictionaryEntry::class,
        PersonalDictionaryEntry::class,
        DictionaryHistoryEntry::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clipboardDao(): ClipboardDao
    abstract fun dictionaryDao(): DictionaryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clipbord_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialTemplates(database.clipboardDao())
                        populateInitialDictionary(database.dictionaryDao())
                    }
                }
            }
        }

        private suspend fun populateInitialDictionary(dao: DictionaryDao) {
            if (dao.getCount() == 0) {
                dao.insertAll(DictionarySeedData.INITIAL_ENTRIES)
            }
        }

        private suspend fun populateInitialTemplates(dao: ClipboardDao) {
            val initialTemplates = listOf(
                ClipboardEntity(
                    text = "سلام، وقتتون بخیر. امیدوارم حالتون عالی باشه.",
                    isPinned = true,
                    isFavorite = true,
                    category = "احوال‌پرسی"
                ),
                ClipboardEntity(
                    text = "سفارش شما با موفقیت ثبت شد و به زودی ارسال می‌شود.",
                    isPinned = true,
                    isFavorite = true,
                    category = "فروشگاه"
                ),
                ClipboardEntity(
                    text = "لطفاً مبلغ را به شماره کارت زیر واریز نمایید و تصویر فیش را ارسال فرمایید:",
                    isPinned = true,
                    isFavorite = false,
                    category = "مالی"
                ),
                ClipboardEntity(
                    text = "خیلی ممنون از همراهی و اعتماد شما 🙏🌹",
                    isPinned = false,
                    isFavorite = true,
                    category = "تشکر"
                ),
                ClipboardEntity(
                    text = "در اسرع وقت بررسی و خدمت شما پاسخ داده خواهد شد.",
                    isPinned = false,
                    isFavorite = false,
                    category = "کاری"
                ),
                ClipboardEntity(
                    text = "ان‌شاءالله در پناه حق پیروز و سلامت باشید.",
                    isPinned = false,
                    isFavorite = true,
                    category = "پیام دوستانه"
                )
            )
            dao.insertAll(initialTemplates)
        }
    }
}
