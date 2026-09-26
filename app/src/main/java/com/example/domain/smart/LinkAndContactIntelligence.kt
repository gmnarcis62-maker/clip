package com.example.domain.smart

object LinkAndContactIntelligence {

    data class DetectedEntity(
        val type: EntityType,
        val rawValue: String,
        val displayLabel: String,
        val quickActionTitle: String
    )

    enum class EntityType {
        URL,
        PHONE,
        EMAIL,
        BANK_CARD
    }

    private val URL_REGEX = Regex("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))")
    private val PHONE_REGEX = Regex("(09[0-9]{9}|\\+989[0-9]{9})")
    private val EMAIL_REGEX = Regex("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+")
    private val BANK_CARD_REGEX = Regex("(?:[0-9]{4}[-\\s]?){3}[0-9]{4}")

    fun detectEntities(text: String): List<DetectedEntity> {
        val results = mutableListOf<DetectedEntity>()

        val engText = PersianNumberIntelligence.toEnglishDigits(text)

        // 1. Detect URLs
        URL_REGEX.findAll(engText).forEach { match ->
            results.add(
                DetectedEntity(
                    type = EntityType.URL,
                    rawValue = match.value,
                    displayLabel = "🔗 پیوند: ${match.value}",
                    quickActionTitle = "کپی یا باز کردن لینک"
                )
            )
        }

        // 2. Detect Phone numbers
        PHONE_REGEX.findAll(engText).forEach { match ->
            results.add(
                DetectedEntity(
                    type = EntityType.PHONE,
                    rawValue = match.value,
                    displayLabel = "📞 شماره تماس: ${PersianNumberIntelligence.toPersianDigits(match.value)}",
                    quickActionTitle = "تماس یا کپی شماره"
                )
            )
        }

        // 3. Detect Emails
        EMAIL_REGEX.findAll(engText).forEach { match ->
            results.add(
                DetectedEntity(
                    type = EntityType.EMAIL,
                    rawValue = match.value,
                    displayLabel = "✉️ ایمیل: ${match.value}",
                    quickActionTitle = "کپی ایمیل"
                )
            )
        }

        // 4. Detect Bank Cards
        BANK_CARD_REGEX.findAll(engText).forEach { match ->
            val digits = match.value.replace(Regex("[-\\s]"), "")
            if (digits.length == 16) {
                val formatted = digits.chunked(4).joinToString("-")
                results.add(
                    DetectedEntity(
                        type = EntityType.BANK_CARD,
                        rawValue = digits,
                        displayLabel = "💳 کارت بانکی: ${PersianNumberIntelligence.toPersianDigits(formatted)}",
                        quickActionTitle = "کپی کارت"
                    )
                )
            }
        }

        return results.distinctBy { it.rawValue }
    }
}
