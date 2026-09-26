package com.example.keyboard

enum class KeyType {
    CHARACTER,
    SPACE,
    HALF_SPACE,
    BACKSPACE,
    ENTER,
    SHIFT,
    LANG_SWITCH,
    MODE_SWITCH,
    EMOJI,
    CLIPBOARD,
    VOICE,
    SETTINGS,
    TAB,
    CURSOR_LEFT,
    CURSOR_RIGHT,
    CURSOR_UP,
    CURSOR_DOWN,
    SELECT_ALL,
    COPY,
    PASTE,
    EXTENDED_TOOLS
}

data class KeyItem(
    val label: String,
    val subLabel: String? = null,
    val output: String = label,
    val type: KeyType = KeyType.CHARACTER,
    val weight: Float = 1.0f,
    val popupOptions: List<String> = emptyList()
)
