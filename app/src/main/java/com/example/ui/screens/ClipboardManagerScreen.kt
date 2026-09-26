package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ClipbordApp
import com.example.data.local.ClipboardEntity
import com.example.domain.shamsi.PersianDateUtils
import com.example.ui.components.ClipbordTopBar
import com.example.ui.components.PersianCard
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.SecondaryGold
import kotlinx.coroutines.launch

@Composable
fun ClipboardManagerScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardRepo = ClipbordApp.instance.clipboardRepository
    val scope = rememberCoroutineScope()

    val allItems by clipboardRepo.allItems.collectAsState(initial = emptyList())
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<ClipboardEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<ClipboardEntity?>(null) }
    var newSnippetText by remember { mutableStateOf("") }
    var newSnippetCategory by remember { mutableStateOf("پاسخ سریع") }

    val filteredItems = remember(allItems, selectedTabIndex, searchQuery) {
        val baseList = when (selectedTabIndex) {
            1 -> allItems.filter { it.isPinned }
            2 -> allItems.filter { it.isFavorite }
            else -> allItems
        }
        if (searchQuery.isBlank()) baseList
        else baseList.filter { it.text.contains(searchQuery, ignoreCase = true) }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                ClipbordTopBar(
                    title = "مدیریت کلیپبورد و پیام‌های آماده",
                    subtitle = "ذخیره، پین و کپی سریع متن‌ها",
                    onBackClick = onBackClick
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        newSnippetText = ""
                        newSnippetCategory = "پاسخ سریع"
                        showAddDialog = true
                    },
                    containerColor = PrimaryCyan,
                    contentColor = Color(0xFF07211E),
                    modifier = Modifier.testTag("fab_add_clipboard")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "افزودن متن آماده")
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .testTag("clipboard_manager_screen")
            ) {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("جستجو در متن‌ها و یادداشت‌ها...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("search_clipboard_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    singleLine = true
                )

                // Tabs: All / Pinned / Favorites
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = PrimaryCyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("همه (${PersianDateUtils.toPersianDigits(allItems.size)})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("📌 سنجاق‌شده‌ها", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("⭐ برگزیده‌ها", fontWeight = FontWeight.Bold) }
                    )
                }

                // List of items
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "متنی مطابق با جستجو پیدا نشد." else "هنوز متنی در این بخش ذخیره نشده است.\nبا زدن دکمه + می‌توانید متن آماده اضافه کنید.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredItems, key = { it.id }) { item ->
                            ClipboardItemCard(
                                item = item,
                                onCopy = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Clipbord", item.text))
                                    Toast.makeText(context, "متن در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show()
                                },
                                onTogglePin = {
                                    scope.launch { clipboardRepo.togglePin(item.id, item.isPinned) }
                                },
                                onToggleFavorite = {
                                    scope.launch { clipboardRepo.toggleFavorite(item.id, item.isFavorite) }
                                },
                                onEdit = {
                                    itemToEdit = item
                                },
                                onDelete = {
                                    itemToDelete = item
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }

        // Add Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("افزودن متن آماده / پاسخ سریع", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newSnippetCategory,
                            onValueChange = { newSnippetCategory = it },
                            label = { Text("دسته‌بندی (مثلاً: کاری، مالی، احوال‌پرسی)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newSnippetText,
                            onValueChange = { newSnippetText = it },
                            label = { Text("متن پیام آماده") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newSnippetText.isNotBlank()) {
                                scope.launch {
                                    clipboardRepo.addCustomSnippet(
                                        text = newSnippetText,
                                        category = newSnippetCategory.ifBlank { "شخصی" },
                                        isPinned = true
                                    )
                                }
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("ذخیره", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("انصراف")
                    }
                }
            )
        }

        // Edit Dialog
        itemToEdit?.let { item ->
            var editText by remember { mutableStateOf(item.text) }
            var editCat by remember { mutableStateOf(item.category) }
            AlertDialog(
                onDismissRequest = { itemToEdit = null },
                title = { Text("ویرایش متن", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editCat,
                            onValueChange = { editCat = it },
                            label = { Text("دسته‌بندی") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editText,
                            onValueChange = { editText = it },
                            label = { Text("متن") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editText.isNotBlank()) {
                                scope.launch {
                                    clipboardRepo.update(item.copy(text = editText, category = editCat))
                                }
                                itemToEdit = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
                    ) {
                        Text("به‌روزرسانی", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToEdit = null }) {
                        Text("انصراف")
                    }
                }
            )
        }

        // Delete Dialog
        itemToDelete?.let { item ->
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("حذف متن", fontWeight = FontWeight.Bold) },
                text = { Text("آیا از حذف این متن از کلیپ‌بورد اطمینان دارید؟") },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch { clipboardRepo.delete(item) }
                            itemToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("حذف", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("انصراف")
                    }
                }
            )
        }
    }
}

@Composable
fun ClipboardItemCard(
    item: ClipboardEntity,
    onCopy: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    PersianCard(
        borderColor = if (item.isPinned) PrimaryCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = item.category,
                    color = PrimaryCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Text(
                text = PersianDateUtils.formatPersianDateWithTime(item.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCopy, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "کپی", tint = PrimaryCyan, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onTogglePin, modifier = Modifier.size(34.dp)) {
                Icon(
                    Icons.Default.PushPin,
                    contentDescription = "پین",
                    tint = if (item.isPinned) PrimaryCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "برگزیده",
                    tint = if (item.isFavorite) SecondaryGold else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "ویرایش", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
            }
        }
    }
}
