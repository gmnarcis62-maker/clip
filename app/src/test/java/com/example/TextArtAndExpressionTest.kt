package com.example

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.domain.smart.ExpressionCenterData
import com.example.domain.smart.SocialCaptionAndBioData
import com.example.domain.smart.SocialMode
import com.example.domain.smart.TextArtConfig
import com.example.domain.smart.TextArtEngine
import com.example.domain.smart.TextArtStyle
import com.example.domain.smart.TextDecoratorEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TextArtAndExpressionTest {

    @Test
    fun testTextArtRealisticAscii() = runBlocking {
        val bmp = TextArtEngine.createPresetBitmap("heart")
        assertNotNull(bmp)

        val config = TextArtConfig(
            style = TextArtStyle.REALISTIC_ASCII,
            socialMode = SocialMode.UNIVERSAL
        )
        val result = TextArtEngine.processImageToTextArt(bmp, config)

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        assertTrue("Should start with LTR mark", result.startsWith("\u200E"))
        assertTrue("Should end with LTR mark", result.endsWith("\u200E"))

        val lines = result.trim('\u200E').split("\n")
        assertTrue("Should have multiple lines", lines.size >= 5)
        // Check line lengths do not exceed reasonable social width
        for (line in lines) {
            assertTrue("Line length should not overflow social width", line.length <= 40)
        }
    }

    @Test
    fun testTextArtUltraDetailAndUnicodeBlocks() = runBlocking {
        val bmp = TextArtEngine.createPresetBitmap("star")

        val ultraConfig = TextArtConfig(style = TextArtStyle.ULTRA_DETAIL, customWidth = 30)
        val ultraRes = TextArtEngine.processImageToTextArt(bmp, ultraConfig)
        assertTrue(ultraRes.contains("\u200E"))

        val unicodeConfig = TextArtConfig(style = TextArtStyle.UNICODE_BLOCKS, customWidth = 25)
        val unicodeRes = TextArtEngine.processImageToTextArt(bmp, unicodeConfig)
        assertTrue(unicodeRes.isNotEmpty())
        // Unicode block art should contain block characters
        val hasBlock = unicodeRes.any { it in "█▓▒░ " }
        assertTrue(hasBlock)
    }

    @Test
    fun testTextArtSymbolAndEmojiModes() = runBlocking {
        val bmp = TextArtEngine.createPresetBitmap("flower")

        // Love symbols
        val loveRes = TextArtEngine.processImageToTextArt(bmp, TextArtConfig(style = TextArtStyle.SYMBOL_LOVE, customWidth = 20))
        assertTrue(loveRes.isNotEmpty())

        // Star symbols
        val starRes = TextArtEngine.processImageToTextArt(bmp, TextArtConfig(style = TextArtStyle.SYMBOL_STAR, customWidth = 20))
        assertTrue(starRes.isNotEmpty())

        // Emoji color mosaic
        val emojiRes = TextArtEngine.processImageToTextArt(bmp, TextArtConfig(style = TextArtStyle.EMOJI_COLOR, customWidth = 18))
        assertTrue(emojiRes.isNotEmpty())
    }

    @Test
    fun testTextArtSubjectOnlyAndInvert() = runBlocking {
        val bmp = TextArtEngine.createPresetBitmap("cat")

        val configSubjectOnly = TextArtConfig(
            style = TextArtStyle.REALISTIC_ASCII,
            subjectOnly = true,
            invert = false
        )
        val result = TextArtEngine.processImageToTextArt(bmp, configSubjectOnly)
        assertTrue(result.isNotEmpty())

        val configInvert = TextArtConfig(
            style = TextArtStyle.REALISTIC_ASCII,
            invert = true
        )
        val invertResult = TextArtEngine.processImageToTextArt(bmp, configInvert)
        assertTrue(invertResult.isNotEmpty())
    }

    @Test
    fun testTextArtSocialModes() = runBlocking {
        val bmp = TextArtEngine.createPresetBitmap("car")

        val commentRes = TextArtEngine.processImageToTextArt(
            bmp,
            TextArtConfig(socialMode = SocialMode.INSTAGRAM_COMMENT)
        )
        val captionRes = TextArtEngine.processImageToTextArt(
            bmp,
            TextArtConfig(socialMode = SocialMode.INSTAGRAM_CAPTION)
        )
        val telegramRes = TextArtEngine.processImageToTextArt(
            bmp,
            TextArtConfig(socialMode = SocialMode.TELEGRAM)
        )

        assertTrue(commentRes.isNotEmpty())
        assertTrue(captionRes.isNotEmpty())
        assertTrue(telegramRes.isNotEmpty())
    }

    @Test
    fun testTextDecoratorEngine() {
        val persianText = "سلام دنیا"
        val variations = TextDecoratorEngine.decorate(persianText)
        assertTrue("Should have multiple decorated frames", variations.size >= 8)

        // Verify framed variations contain original text
        val hasStarFrame = variations.any { it.preview.contains("✦ $persianText ✦") }
        assertTrue(hasStarFrame)

        val hasHeartFrame = variations.any { it.preview.contains("♡ $persianText ♡") }
        assertTrue(hasHeartFrame)

        // English text decorations
        val engText = "Life"
        val engVariations = TextDecoratorEngine.decorate(engText)
        val hasBoldSerif = engVariations.any { it.title.contains("Bold") }
        assertTrue(hasBoldSerif)

        val boldScript = TextDecoratorEngine.toUnicodeFont("Hello", TextDecoratorEngine.FontStyle.BOLD_SCRIPT)
        assertFalse(boldScript == "Hello")
        assertTrue(boldScript.isNotEmpty())
    }

    @Test
    fun testSocialCaptionAndBioData() {
        assertEquals(13, SocialCaptionAndBioData.CAPTION_CATEGORIES.size)
        assertTrue(SocialCaptionAndBioData.CAPTIONS.size >= 10)
        assertTrue(SocialCaptionAndBioData.BIO_PRESETS.isNotEmpty())

        val bio = SocialCaptionAndBioData.BIO_PRESETS[0].format()
        assertTrue(bio.contains("────────────"))

        assertTrue(SocialCaptionAndBioData.SEPARATORS.size >= 5)
        for (cat in SocialCaptionAndBioData.SEPARATORS) {
            assertTrue(cat.items.isNotEmpty())
        }
    }

    @Test
    fun testExpressionCenterData() {
        assertEquals("Should have exactly 15 comprehensive emoji categories", 15, ExpressionCenterData.EMOJI_CATEGORIES.size)
        for (cat in ExpressionCenterData.EMOJI_CATEGORIES) {
            assertTrue("Category ${cat.title} should have emojis", cat.items.isNotEmpty())
        }

        assertTrue("Kaomoji categories should not be empty", ExpressionCenterData.KAOMOJI_CATEGORIES.isNotEmpty())
        for (kmCat in ExpressionCenterData.KAOMOJI_CATEGORIES) {
            assertTrue(kmCat.items.isNotEmpty())
        }
    }
}
