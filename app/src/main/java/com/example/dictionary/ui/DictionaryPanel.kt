package com.example.dictionary.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ClipbordApp
import com.example.ai.domain.AiOperation
import com.example.ai.domain.AiResult
import com.example.dictionary.data.DictionaryEntry
import com.example.dictionary.data.PersonalDictionaryEntry
import com.example.dictionary.domain.DictionaryItemResult
import com.example.dictionary.engine.DictionaryTtsEngine
import com.example.themes.KeyboardTheme
import kotlinx.coroutines.launch

@Composable
fun DictionaryPanel(
    modifier: Modifier = Modifier,
    theme: KeyboardTheme,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = ClipbordApp.instance.dictionaryRepository
    val aiRepo = ClipbordApp.instance.aiRepository
    val ttsEngine = remember { DictionaryTtsEngine(context) }

    // ✅ Shut down the TTS engine when this panel leaves the composition to
    //    prevent the underlying Android TTS service from leaking.
    DisposableEffect(ttsEngine) {
        onDispose {
            ttsEngine.shutdown()
        }
    }

    var searchResults by remember { mutableStateOf<List<DictionaryItemResult>>(emptyList()) }
    var selectedEntry by remember { mutableStateOf<DictionaryEntry?>(null) }
    var selectedPersonalEntry by remember { mutableStateOf<PersonalDictionaryEntry?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var wordOfTheDay by remember { mutableStateOf<DictionaryEntry?>(null) }

    // Personal add dialog state
    var showAddPersonalDialog by remember { mutableStateOf(false) }
    var newPersonalWord by remember { mutableStateOf("") }
    var newPersonalDefinition by remember { mutableStateOf("") }

    // AI Deep Explain state
    var isAiLoading by remember { mutableStateOf(false) }
    var aiExplanation by remember { mutableStateOf<String?>(null) }

    val favorites by repo.getFavoritesFlow().collectAsState(initial = emptyList())
    val history by repo.getHistoryFlow().collectAsState(initial = emptyList())
    val personalEntries by repo.getPersonalEntriesFlow().collectAsState(initial = emptyList())

    // Load word of the day once
    LaunchedEffect(Unit) {
        wordOfTheDay = repo.getWordOfTheDay()
    }

    LaunchedEffect(searchQuery, selectedTab) {
        if (searchQuery.isNotBlank()) {
            val lang = when (selectedTab) {
                1 -> "fa"
                2 -> "en"
                else -> null
            }
            searchResults = repo.search(searchQuery, lang)
        } else {
            searchResults = emptyList()
        }
    }

    val tabs = listOf("همه", "فارسی", "انگلیسی", "علاقه‌مندی‌ها", "تاریخچه", "لغت‌نامه من")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(theme.surfaceColor, RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedEntry != null || selectedPersonalEntry != null) {
                    IconButton(
                        onClick = {
                            selectedEntry = null
                            selectedPersonalEntry = null
                            aiExplanation = null
                        },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "بازگشت", tint = theme.accentColor)
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        selectedEntry != null -> selectedEntry?.word ?: "واژه‌نامه"
                        selectedPersonalEntry != null -> selectedPersonalEntry?.word ?: "لغت شخصی"
                        else -> "📖 دیکشنری هوشمند و آفلاین"
                    },
                    color = theme.keyTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                Icon(Icons.Default.Close, contentDescription = "بستن", tint = theme.keySubTextColor)
            }
        }

        // Search Input Bar (when not in detail view)
        if (selectedEntry == null && selectedPersonalEntry == null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "جستجوی واژه یا معنی...",
                            fontSize = 12.sp,
                            color = theme.keySubTextColor
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    null,
                                    tint = theme.keySubTextColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 13.sp,
                        color = theme.keyTextColor
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.accentColor,
                        unfocusedBorderColor = theme.keyBackgroundColor,
                        focusedContainerColor = theme.keyBackgroundColor,
                        unfocusedContainerColor = theme.keyBackgroundColor,
                        focusedTextColor = theme.keyTextColor,
                        unfocusedTextColor = theme.keyTextColor
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Navigation Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(tabs) { idx, title ->
                    val isSelected = idx == selectedTab
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) theme.accentColor else theme.keyBackgroundColor,
                        border = BorderStroke(0.5.dp, if (isSelected) theme.accentColor else theme.keyTopHighlightColor),
                        modifier = Modifier.clickable {
                            selectedTab = idx
                        }
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) theme.accentTextColor else theme.keyTextColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Body Content
        when {
            // 1. Detailed Word View
            selectedEntry != null -> {
                val entry = selectedEntry!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = theme.keyBackgroundColor,
                            border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = entry.word,
                                            color = theme.accentColor,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (entry.pronunciation.isNotEmpty()) {
                                            Text(
                                                text = entry.pronunciation,
                                                color = theme.keySubTextColor,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = { ttsEngine.speak(entry.word, entry.language) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.VolumeUp, "تلفظ", tint = theme.accentColor, modifier = Modifier.size(16.dp))
                                        }

                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    repo.toggleFavorite(entry.id, entry.isFavorite)
                                                    selectedEntry = entry.copy(isFavorite = !entry.isFavorite)
                                                }
                                            },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                if (entry.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                "علاقه‌مندی",
                                                tint = if (entry.isFavorite) Color(0xFFFFB800) else theme.keySubTextColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (entry.partOfSpeech.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = theme.surfaceColor
                                        ) {
                                            Text(
                                                text = entry.partOfSpeech,
                                                color = theme.keyTextColor,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (entry.root.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = theme.surfaceColor
                                        ) {
                                            Text(
                                                text = "ریشه: ${entry.root}",
                                                color = theme.keySubTextColor,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "📖 معنی: ${entry.definition}",
                                    color = theme.keyTextColor,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )

                                if (entry.synonyms.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🔄 مترادف‌ها: ${entry.synonyms}",
                                        color = theme.accentColor,
                                        fontSize = 11.sp
                                    )
                                }

                                if (entry.antonyms.isNotEmpty() && entry.antonyms != "-") {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "⚡ متضاد: ${entry.antonyms}",
                                        color = theme.keySubTextColor,
                                        fontSize = 11.sp
                                    )
                                }

                                if (entry.englishTranslation.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🌐 ترجمه انگلیسی: ${entry.englishTranslation}",
                                        color = theme.keyTextColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (entry.examples.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "💬 مثال: «${entry.examples}»",
                                        color = theme.keySubTextColor,
                                        fontSize = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { onInsertText(entry.word) },
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp)
                                    ) {
                                        Icon(Icons.Default.Check, null, tint = theme.accentTextColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("درج واژه", color = theme.accentTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Dictionary", "${entry.word}: ${entry.definition}"))
                                            Toast.makeText(context, "کپی شد", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.specialKeyBackgroundColor),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, null, tint = theme.specialKeyTextColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("کپی کامل", color = theme.specialKeyTextColor, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFFB800).copy(alpha = 0.15f),
                                    border = BorderStroke(0.5.dp, Color(0xFFFFB800).copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (!isAiLoading) {
                                                isAiLoading = true
                                                scope.launch {
                                                    val res = aiRepo.executeOperation(
                                                        operation = AiOperation.CUSTOM_PROMPT,
                                                        inputText = entry.word,
                                                        customPrompt = "کلمه «${entry.word}» را به زبانی ساده با ریشه‌شناسی و ۳ مثال واقعی توضیح بده."
                                                    )
                                                    isAiLoading = false
                                                    when (res) {
                                                        is AiResult.Success -> aiExplanation = res.outputText
                                                        else -> aiExplanation = "خطا در برقراری ارتباط با هوش مصنوعی"
                                                    }
                                                }
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (isAiLoading) {
                                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color(0xFFFFB800), strokeWidth = 2.dp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("در حال دریافت توضیح جامع با هوش مصنوعی مرسانا...", color = Color(0xFFFFB800), fontSize = 10.sp)
                                        } else {
                                            Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFFFB800), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("🤖 توضیح بیشتر و تحلیل ادبی با هوش مصنوعی", color = Color(0xFFFFB800), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (aiExplanation != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = theme.surfaceColor,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = aiExplanation ?: "",
                                            color = theme.keyTextColor,
                                            fontSize = 11.sp,
                                            lineHeight = 17.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Personal Word Detail View
            selectedPersonalEntry != null -> {
                val pEntry = selectedPersonalEntry!!
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = theme.keyBackgroundColor,
                    border = BorderStroke(0.5.dp, theme.accentColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(pEntry.word, color = theme.accentColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        repo.deletePersonalWord(pEntry.id)
                                        selectedPersonalEntry = null
                                    }
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(Icons.Default.Delete, "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("📖 تعریف شخصی: ${pEntry.definition}", color = theme.keyTextColor, fontSize = 12.sp)
                        if (pEntry.synonyms.isNotEmpty()) {
                            Text("🔄 مترادف: ${pEntry.synonyms}", color = theme.keySubTextColor, fontSize = 11.sp)
                        }
                        if (pEntry.note.isNotEmpty()) {
                            Text("📝 یادداشت: ${pEntry.note}", color = theme.keySubTextColor, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onInsertText(pEntry.word) },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().height(32.dp)
                        ) {
                            Text("درج در متن", color = theme.accentTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Favorites Tab
            selectedTab == 3 -> {
                if (favorites.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("هنوز واژه‌ای به علاقه‌مندی‌ها اضافه نشده است.", color = theme.keySubTextColor, fontSize = 11.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(favorites) { fav ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = theme.keyBackgroundColor,
                                border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch { repo.recordSearch(fav.word, fav.language) }
                                        selectedEntry = fav
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(fav.word, color = theme.keyTextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(fav.shortDefinition.ifEmpty { fav.definition }, color = theme.keySubTextColor, fontSize = 10.sp, maxLines = 1)
                                    }
                                    Icon(Icons.Default.Bookmark, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 4. History Tab
            selectedTab == 4 -> {
                if (history.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("تاریخچه جستجو خالی است.", color = theme.keySubTextColor, fontSize = 11.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "پاکسازی تاریخچه",
                                    color = Color(0xFFEF4444),
                                    fontSize = 10.sp,
                                    modifier = Modifier.clickable {
                                        scope.launch { repo.clearHistory() }
                                    }
                                )
                            }
                        }
                        items(history) { h ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = theme.keyBackgroundColor,
                                border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSearchQueryChange(h.query) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(h.query, color = theme.keyTextColor, fontSize = 12.sp)
                                    IconButton(
                                        onClick = { scope.launch { repo.deleteHistoryItem(h.id) } },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = theme.keySubTextColor, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Personal Dictionary Tab
            selectedTab == 5 -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("لغت‌نامه اختصاصی شما", color = theme.keyTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { showAddPersonalDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Add, null, tint = theme.accentTextColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("افزودن واژه", color = theme.accentTextColor, fontSize = 10.sp)
                        }
                    }

                    if (showAddPersonalDialog) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.keyBackgroundColor,
                            border = BorderStroke(0.5.dp, theme.accentColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                OutlinedTextField(
                                    value = newPersonalWord,
                                    onValueChange = { newPersonalWord = it },
                                    placeholder = { Text("واژه...", fontSize = 10.sp, color = theme.keySubTextColor) },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                OutlinedTextField(
                                    value = newPersonalDefinition,
                                    onValueChange = { newPersonalDefinition = it },
                                    placeholder = { Text("معنی یا تعریف شخصی...", fontSize = 10.sp, color = theme.keySubTextColor) },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Button(
                                        onClick = {
                                            if (newPersonalWord.isNotBlank() && newPersonalDefinition.isNotBlank()) {
                                                scope.launch {
                                                    repo.addPersonalWord(newPersonalWord, newPersonalDefinition)
                                                    newPersonalWord = ""
                                                    newPersonalDefinition = ""
                                                    showAddPersonalDialog = false
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("ذخیره", color = theme.accentTextColor, fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { showAddPersonalDialog = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = theme.specialKeyBackgroundColor),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("انصراف", color = theme.specialKeyTextColor, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (personalEntries.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("هنوز واژه شخصی ثبت نکرده‌اید.", color = theme.keySubTextColor, fontSize = 11.sp)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(personalEntries) { p ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = theme.keyBackgroundColor,
                                    border = BorderStroke(0.5.dp, theme.keyTopHighlightColor),
                                    modifier = Modifier.fillMaxWidth().clickable { selectedPersonalEntry = p }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(p.word, color = theme.accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text(p.definition, color = theme.keySubTextColor, fontSize = 10.sp, maxLines = 1)
                                        }
                                        Text("شخصی", color = theme.accentColor, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 6. Search Results List
            searchResults.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(searchResults) { res ->
                        val isPersonal = res is DictionaryItemResult.Personal
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.keyBackgroundColor,
                            border = BorderStroke(0.5.dp, if (isPersonal) theme.accentColor else theme.keyTopHighlightColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        val lang = when (selectedTab) {
                                            1 -> "fa"
                                            2 -> "en"
                                            else -> null
                                        }
                                        repo.recordSearch(searchQuery, lang)
                                    }
                                    when (res) {
                                        is DictionaryItemResult.Global -> selectedEntry = res.entry
                                        is DictionaryItemResult.Personal -> selectedPersonalEntry = res.entry
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = res.displayWord,
                                            color = theme.keyTextColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isPersonal) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(3.dp),
                                                color = theme.accentColor.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "لغت شما",
                                                    color = theme.accentColor,
                                                    fontSize = 8.sp,
                                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = res.displayDefinition,
                                        color = theme.keySubTextColor,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Button(
                                    onClick = { onInsertText(res.displayWord) },
                                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("درج", color = theme.accentTextColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 7. Empty Search State -> Show "Word of the Day"
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (wordOfTheDay != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = theme.keyBackgroundColor,
                                border = BorderStroke(1.dp, theme.accentColor.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedEntry = wordOfTheDay }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🌟 واژه امروز:", color = Color(0xFFFFB800), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(wordOfTheDay?.word ?: "", color = theme.keyTextColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(wordOfTheDay?.partOfSpeech ?: "", color = theme.keySubTextColor, fontSize = 9.sp)
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = wordOfTheDay?.definition ?: "",
                                        color = theme.keySubTextColor,
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = theme.keyBackgroundColor.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 با جستجوی هر کلمه، معانی فارسی و انگلیسی، مترادف‌ها، متضادها، مثال و ریشه‌شناسی را به صورت کاملاً آفلاین مشاهده کنید.",
                                color = theme.keySubTextColor,
                                fontSize = 10.sp,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}