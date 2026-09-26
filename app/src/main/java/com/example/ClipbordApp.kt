package com.example

import android.app.Application
import com.example.ai.domain.AiRepository
import com.example.ai.domain.AiRepositoryImpl
import com.example.billing.MyketBillingManager
import com.example.data.datastore.KeyboardPreferences
import com.example.data.local.AppDatabase
import com.example.data.repository.ClipboardRepository
import com.example.dictionary.domain.DictionaryRepository

class ClipbordApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var clipboardRepository: ClipboardRepository
        private set

    lateinit var dictionaryRepository: DictionaryRepository
        private set

    lateinit var preferences: KeyboardPreferences
        private set

    lateinit var billingManager: MyketBillingManager
        private set

    lateinit var aiRepository: AiRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        clipboardRepository = ClipboardRepository(database.clipboardDao())
        dictionaryRepository = DictionaryRepository(database.dictionaryDao())
        preferences = KeyboardPreferences(this)
        billingManager = MyketBillingManager(this, preferences)
        aiRepository = AiRepositoryImpl(preferences)
    }

    companion object {
        lateinit var instance: ClipbordApp
            private set
    }
}
