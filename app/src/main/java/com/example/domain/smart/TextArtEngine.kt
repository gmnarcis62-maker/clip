package com.example.domain.smart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

enum class TextArtStyle(val displayName: String, val description: String) {
    REALISTIC_ASCII("ASCII واقع‌گرایانه", "طیف کامل کاراکترهای استاندارد"),
    ULTRA_DETAIL("فوق دقیق (Ultra Detail)", "پالت عریض با بیشترین جزئیات"),
    UNICODE_BLOCKS("بلوک‌های یونیکد", "بلوک‌های تیره و روشن █▓▒░"),
    SYMBOL_LOVE("نماد عشق و قلب", "ترکیب قلب‌های ظریف و پر"),
    SYMBOL_STAR("نماد ستاره و درخشش", "ستاره‌های چهارپر و پنج‌پر"),
    SYMBOL_LUXURY("لوکس و هندسی", "لوزی و مربع‌های هندسی"),
    SYMBOL_MINIMAL("مینیمال و نقطه‌ای", "دایره‌ها و نقاط مونوکروم"),
    EMOJI_COLOR("موزاییک ایموجی رنگی", "نقاشی با ایموجی‌های چندرنگ"),
    EMOJI_SHINE("ایموجی درخشان و آتش", "ماه، ستاره و شعله‌های آتش"),
    EMOJI_HEARTS("ایموجی قلب‌های رنگی", "طیف قلب‌های عاشقانه")
}

enum class SocialMode(
    val displayName: String,
    val targetWidth: Int,
    val appHint: String
) {
    INSTAGRAM_COMMENT("کامنت اینستاگرام", 24, "عرض فشرده جهت جلوگیری از شکستن خط در کامنت"),
    INSTAGRAM_CAPTION("کپشن اینستاگرام", 34, "مناسب پست و خطوط خوانا"),
    TELEGRAM("تلگرام (Monospace)", 48, "خروجی باکیفیت و خطوط عریض"),
    WHATSAPP("واتساپ", 30, "سازگار با فونت پیش‌فرض چت"),
    TIKTOK("تیک‌تاک", 22, "بسیار فشرده برای بیو و کامنت"),
    UNIVERSAL("عمومی (متعادل)", 32, "بیشترین سازگاری متنی در همه برنامه‌ها")
}

data class TextArtConfig(
    val style: TextArtStyle = TextArtStyle.REALISTIC_ASCII,
    val socialMode: SocialMode = SocialMode.UNIVERSAL,
    val customWidth: Int? = null,
    val contrast: Float = 1.0f,
    val brightnessBoost: Float = 0.0f,
    val aspectCorrection: Boolean = true,
    val subjectOnly: Boolean = false,
    val invert: Boolean = false
)

object TextArtEngine {

    // Standard density character ramps (from darkest/heaviest to lightest/empty)
    private const val RAMP_ASCII_REALISTIC = "@&B9#SGHMh352AXsri;:,. "
    private const val RAMP_ULTRA_DETAIL = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~i!lI;:,\"^'` . "
    private const val RAMP_UNICODE_BLOCKS = "█▓▒░ "
    private const val RAMP_LOVE = "♥♡· "
    private const val RAMP_STAR = "★✦✧· "
    private const val RAMP_LUXURY = "■◆◇· "
    private const val RAMP_MINIMAL = "●○◦· "

    private val EMOJI_PALETTE_COLOR = listOf(
        "🖤", "🤎", "💜", "💙", "💚", "💛", "🧡", "❤️", "🤍"
    )

    private val EMOJI_PALETTE_SHINE = listOf(
        "🌑", "🌘", "🌗", "🌖", "🌕", "✨", "⭐", "🔥"
    )

    private val EMOJI_PALETTE_HEARTS = listOf(
        "🖤", "💜", "💙", "💚", "💛", "🧡", "❤️", "💖", "🤍"
    )

    suspend fun processImageToTextArt(
        bitmap: Bitmap,
        config: TextArtConfig
    ): String = withContext(Dispatchers.Default) {
        val targetWidth = config.customWidth ?: config.socialMode.targetWidth
        val effectiveWidth = targetWidth.coerceIn(16, 70)

        // Monospace font aspect ratio: character height is usually ~1.8x to 2.0x its width.
        // To preserve original image geometry, vertical height should be scaled down.
        val fontAspectCorrection = if (config.aspectCorrection) 0.52f else 1.0f
        val originalAspect = bitmap.height.toFloat() / bitmap.width.toFloat()
        val targetHeight = max(8, (effectiveWidth * originalAspect * fontAspectCorrection).toInt())

        // Resize bitmap to grid dimensions with bilinear filtering
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, effectiveWidth, targetHeight, true)

        // Calculate average background intensity if subjectOnly is enabled
        var bgAverage = 0.5f
        if (config.subjectOnly) {
            var edgeSum = 0f
            var edgeCount = 0
            for (x in 0 until effectiveWidth) {
                edgeSum += getLuminance(scaledBitmap.getPixel(x, 0))
                edgeSum += getLuminance(scaledBitmap.getPixel(x, targetHeight - 1))
                edgeCount += 2
            }
            for (y in 1 until targetHeight - 1) {
                edgeSum += getLuminance(scaledBitmap.getPixel(0, y))
                edgeSum += getLuminance(scaledBitmap.getPixel(effectiveWidth - 1, y))
                edgeCount += 2
            }
            if (edgeCount > 0) {
                bgAverage = edgeSum / edgeCount
            }
        }

        val sb = StringBuilder()
        // Prepend LTR Mark to isolate Bidirectional layout and prevent line disorder in RTL messaging apps
        sb.append("\u200E")

        val centerX = effectiveWidth / 2f
        val centerY = targetHeight / 2f
        val maxDist = Math.hypot(centerX.toDouble(), centerY.toDouble()).toFloat()

        for (y in 0 until targetHeight) {
            val lineChars = StringBuilder()
            for (x in 0 until effectiveWidth) {
                val pixel = scaledBitmap.getPixel(x, y)
                var lum = getLuminance(pixel)

                // Subject Only (Local Saliency & Background Attenuation)
                if (config.subjectOnly) {
                    val distFromCenter = Math.hypot((x - centerX).toDouble(), (y - centerY).toDouble()).toFloat()
                    val centerWeight = 1.0f - (distFromCenter / maxDist).coerceIn(0f, 0.9f)
                    val diffFromBg = Math.abs(lum - bgAverage)

                    if (diffFromBg < 0.15f && centerWeight < 0.45f) {
                        // Background area: blank out
                        lum = 1.0f
                    } else {
                        // Foreground subject: amplify contrast
                        lum = ((lum - 0.5f) * 1.3f + 0.5f).coerceIn(0f, 1f)
                    }
                }

                // Apply Contrast & Brightness adjustments
                lum = ((lum - 0.5f) * config.contrast + 0.5f + config.brightnessBoost).coerceIn(0f, 1f)

                if (config.invert) {
                    lum = 1.0f - lum
                }

                val charStr = mapLuminanceToCharacter(lum, config.style, pixel)
                lineChars.append(charStr)
            }

            // Trim trailing blank spaces for social clean format
            val cleanLine = lineChars.toString().trimEnd()
            sb.append(cleanLine)
            if (y < targetHeight - 1) {
                sb.append("\n")
            }
        }

        sb.append("\u200E")
        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }

        sb.toString()
    }

    private fun getLuminance(color: Int): Float {
        val r = Color.red(color) / 255f
        val g = Color.green(color) / 255f
        val b = Color.blue(color) / 255f
        // Standard Rec. 601 perceived luminance
        return (0.299f * r + 0.587f * g + 0.114f * b).coerceIn(0f, 1f)
    }

    private fun mapLuminanceToCharacter(lum: Float, style: TextArtStyle, pixelColor: Int): String {
        return when (style) {
            TextArtStyle.REALISTIC_ASCII -> {
                pickFromRamp(lum, RAMP_ASCII_REALISTIC)
            }
            TextArtStyle.ULTRA_DETAIL -> {
                pickFromRamp(lum, RAMP_ULTRA_DETAIL)
            }
            TextArtStyle.UNICODE_BLOCKS -> {
                pickFromRamp(lum, RAMP_UNICODE_BLOCKS)
            }
            TextArtStyle.SYMBOL_LOVE -> {
                pickFromRamp(lum, RAMP_LOVE)
            }
            TextArtStyle.SYMBOL_STAR -> {
                pickFromRamp(lum, RAMP_STAR)
            }
            TextArtStyle.SYMBOL_LUXURY -> {
                pickFromRamp(lum, RAMP_LUXURY)
            }
            TextArtStyle.SYMBOL_MINIMAL -> {
                pickFromRamp(lum, RAMP_MINIMAL)
            }
            TextArtStyle.EMOJI_COLOR -> {
                val index = ((1.0f - lum) * (EMOJI_PALETTE_COLOR.size - 1)).toInt().coerceIn(0, EMOJI_PALETTE_COLOR.size - 1)
                EMOJI_PALETTE_COLOR[index]
            }
            TextArtStyle.EMOJI_SHINE -> {
                val index = ((1.0f - lum) * (EMOJI_PALETTE_SHINE.size - 1)).toInt().coerceIn(0, EMOJI_PALETTE_SHINE.size - 1)
                EMOJI_PALETTE_SHINE[index]
            }
            TextArtStyle.EMOJI_HEARTS -> {
                val index = ((1.0f - lum) * (EMOJI_PALETTE_HEARTS.size - 1)).toInt().coerceIn(0, EMOJI_PALETTE_HEARTS.size - 1)
                EMOJI_PALETTE_HEARTS[index]
            }
        }
    }

    private fun pickFromRamp(lum: Float, ramp: String): String {
        // lum = 0.0 is darkest (needs first ramp char), lum = 1.0 is brightest (needs last char: space)
        val index = (lum * (ramp.length - 1)).toInt().coerceIn(0, ramp.length - 1)
        return ramp[index].toString()
    }

    // Built-in presets for instant preview without gallery dependency
    fun createPresetBitmap(presetName: String): Bitmap {
        val size = 160
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Background: clean bright fill
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)

        when (presetName.lowercase()) {
            "heart", "عشق", "قلب" -> {
                paint.color = Color.rgb(220, 20, 60)
                paint.style = Paint.Style.FILL
                val path = Path()
                path.moveTo(size / 2f, size * 0.78f)
                path.cubicTo(size * 0.1f, size * 0.5f, size * 0.15f, size * 0.18f, size * 0.38f, size * 0.22f)
                path.cubicTo(size * 0.48f, size * 0.24f, size / 2f, size * 0.34f, size / 2f, size * 0.34f)
                path.cubicTo(size / 2f, size * 0.34f, size * 0.52f, size * 0.24f, size * 0.62f, size * 0.22f)
                path.cubicTo(size * 0.85f, size * 0.18f, size * 0.9f, size * 0.5f, size / 2f, size * 0.78f)
                canvas.drawPath(path, paint)
            }
            "star", "ستاره" -> {
                paint.color = Color.rgb(240, 160, 0)
                paint.style = Paint.Style.FILL
                val path = Path()
                val cx = size / 2f
                val cy = size / 2f
                val outerR = size * 0.42f
                val innerR = size * 0.18f
                for (i in 0 until 10) {
                    val r = if (i % 2 == 0) outerR else innerR
                    val angle = Math.toRadians((i * 36.0) - 90.0)
                    val x = cx + (r * cos(angle)).toFloat()
                    val y = cy + (r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                canvas.drawPath(path, paint)
            }
            "cat", "گربه" -> {
                paint.color = Color.rgb(40, 40, 40)
                paint.style = Paint.Style.FILL
                // Head
                canvas.drawCircle(size / 2f, size * 0.55f, size * 0.30f, paint)
                // Ears
                val earLeft = Path().apply {
                    moveTo(size * 0.25f, size * 0.45f)
                    lineTo(size * 0.20f, size * 0.18f)
                    lineTo(size * 0.42f, size * 0.32f)
                    close()
                }
                val earRight = Path().apply {
                    moveTo(size * 0.75f, size * 0.45f)
                    lineTo(size * 0.80f, size * 0.18f)
                    lineTo(size * 0.58f, size * 0.32f)
                    close()
                }
                canvas.drawPath(earLeft, paint)
                canvas.drawPath(earRight, paint)

                // White eyes
                paint.color = Color.WHITE
                canvas.drawCircle(size * 0.40f, size * 0.52f, size * 0.05f, paint)
                canvas.drawCircle(size * 0.60f, size * 0.52f, size * 0.05f, paint)
            }
            "flower", "گل" -> {
                paint.color = Color.rgb(230, 80, 140)
                paint.style = Paint.Style.FILL
                val cx = size / 2f
                val cy = size / 2f
                val petalR = size * 0.14f
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60.0))
                    val px = cx + (size * 0.22f * cos(angle)).toFloat()
                    val py = cy + (size * 0.22f * sin(angle)).toFloat()
                    canvas.drawCircle(px, py, petalR, paint)
                }
                paint.color = Color.rgb(255, 200, 0)
                canvas.drawCircle(cx, cy, size * 0.15f, paint)
            }
            "car", "ماشین" -> {
                paint.color = Color.rgb(30, 80, 160)
                paint.style = Paint.Style.FILL
                // Car body
                canvas.drawRoundRect(size * 0.12f, size * 0.48f, size * 0.88f, size * 0.72f, 12f, 12f, paint)
                // Cabin
                canvas.drawRoundRect(size * 0.26f, size * 0.30f, size * 0.74f, size * 0.50f, 16f, 16f, paint)
                // Windows (White)
                paint.color = Color.WHITE
                canvas.drawRoundRect(size * 0.30f, size * 0.34f, size * 0.48f, size * 0.46f, 6f, 6f, paint)
                canvas.drawRoundRect(size * 0.52f, size * 0.34f, size * 0.70f, size * 0.46f, 6f, 6f, paint)
                // Wheels
                paint.color = Color.BLACK
                canvas.drawCircle(size * 0.30f, size * 0.72f, size * 0.11f, paint)
                canvas.drawCircle(size * 0.70f, size * 0.72f, size * 0.11f, paint)
            }
            else -> {
                // Portrait / Face silhouette
                paint.color = Color.rgb(50, 50, 60)
                paint.style = Paint.Style.FILL
                canvas.drawCircle(size / 2f, size * 0.38f, size * 0.22f, paint)
                canvas.drawRoundRect(size * 0.22f, size * 0.65f, size * 0.78f, size * 0.98f, 24f, 24f, paint)
            }
        }

        return bitmap
    }

    fun decodeSafeBitmapFromStream(inputStream: InputStream, maxDimension: Int = 300): Bitmap? {
        return try {
            val original = android.graphics.BitmapFactory.decodeStream(inputStream) ?: return null
            if (original.width <= maxDimension && original.height <= maxDimension) {
                original
            } else {
                val ratio = min(maxDimension.toFloat() / original.width, maxDimension.toFloat() / original.height)
                val newW = max(1, (original.width * ratio).toInt())
                val newH = max(1, (original.height * ratio).toInt())
                val resized = Bitmap.createScaledBitmap(original, newW, newH, true)
                original.recycle()
                resized
            }
        } catch (e: Throwable) {
            null
        }
    }
}
