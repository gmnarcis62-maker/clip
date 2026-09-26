package com.example.domain.smart

data class SnippetItem(
    val id: String,
    val shortcut: String, // e.g. "/addr", "/tel", "/card"
    val title: String,
    val content: String,
    val category: String = "عمومی",
    val isPinned: Boolean = false
)

data class PersonalDictionaryWord(
    val word: String,
    val shortcut: String? = null,
    val frequency: Int = 1
)

object SmartSnippetsAndDictionary {

    val DEFAULT_SNIPPETS = listOf(
        SnippetItem(
            id = "s_addr",
            shortcut = "/addr",
            title = "آدرس پستی",
            content = "تهران، خیابان ولیعصر، نرسیده به میدان ونک، پلاک ۱۲",
            category = "آدرس",
            isPinned = true
        ),
        SnippetItem(
            id = "s_tel",
            shortcut = "/tel",
            title = "شماره تماس",
            content = "۰۹۱۲۱۲۳۴۵۶۷",
            category = "تماس",
            isPinned = true
        ),
        SnippetItem(
            id = "s_mail",
            shortcut = "/mail",
            title = "ایمیل کاری",
            content = "info@example.com",
            category = "تماس"
        ),
        SnippetItem(
            id = "s_card",
            shortcut = "/card",
            title = "شماره کارت بانکی",
            content = "۶۰۳۷-۹۹۷۵-۱۲۳۴-۵۶۷۸",
            category = "بانکی",
            isPinned = true
        ),
        SnippetItem(
            id = "s_thanks",
            shortcut = "/thanks",
            title = "متن تشکر رسمی",
            content = "از حسن توجه و پیگیری جنابعالی بسیار سپاسگزارم.",
            category = "پیام آماده"
        ),
        SnippetItem(
            id = "s_hello",
            shortcut = "/hi",
            title = "درود و احوالپرسی",
            content = "سلام و عرض ادب، وقت شما بخیر و شادی.",
            category = "پیام آماده"
        )
    )

    private val userSnippets = mutableListOf<SnippetItem>().apply { addAll(DEFAULT_SNIPPETS) }
    private val personalWords = mutableListOf<PersonalDictionaryWord>()

    fun getAllSnippets(): List<SnippetItem> {
        return userSnippets.toList()
    }

    fun addSnippet(snippet: SnippetItem) {
        userSnippets.removeAll { it.id == snippet.id }
        userSnippets.add(0, snippet)
    }

    fun removeSnippet(id: String) {
        userSnippets.removeAll { it.id == id }
    }

    fun searchSnippets(query: String): List<SnippetItem> {
        if (query.isBlank()) return userSnippets
        return userSnippets.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.content.contains(query, ignoreCase = true) ||
            it.shortcut.contains(query, ignoreCase = true)
        }
    }

    fun findSnippetByShortcut(text: String): SnippetItem? {
        val trimmed = text.trim()
        return userSnippets.find { it.shortcut.equals(trimmed, ignoreCase = true) }
    }

    // Personal Dictionary
    fun addWord(word: String, shortcut: String? = null) {
        personalWords.removeAll { it.word.equals(word, ignoreCase = true) }
        personalWords.add(0, PersonalDictionaryWord(word, shortcut))
    }

    fun getPersonalWords(): List<PersonalDictionaryWord> {
        return personalWords.toList()
    }

    fun findWordByShortcut(shortcut: String): String? {
        return personalWords.find { it.shortcut?.equals(shortcut, ignoreCase = true) == true }?.word
    }
}
