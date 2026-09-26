package com.example.domain.smart

import com.example.domain.shamsi.PersianDateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object PersianDateTimeIntelligence {

    data class DateTimeFormatOption(
        val title: String,
        val formattedText: String,
        val category: String
    )

    /**
     * Returns list of available Persian date & time formats for the current timestamp.
     */
    fun getAvailableFormats(currentTimeMillis: Long = System.currentTimeMillis()): List<DateTimeFormatOption> {
        val cal = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        val (jy, jm, jd) = PersianDateUtils.gregorianToJalali(gy, gm, gd)
        val monthName = PersianDateUtils.getPersianMonthName(jm)
        val weekDayName = PersianDateUtils.getPersianDayOfWeekName(dayOfWeek)

        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeWithSecondsFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeString = timeFormatter.format(Date(currentTimeMillis))
        val timeWithSecondsString = timeWithSecondsFormatter.format(Date(currentTimeMillis))

        val faYear = PersianDateUtils.toPersianDigits(jy.toString())
        val faMonth = PersianDateUtils.toPersianDigits(String.format(Locale.US, "%02d", jm))
        val faDay = PersianDateUtils.toPersianDigits(String.format(Locale.US, "%02d", jd))
        val faTime = PersianDateUtils.toPersianDigits(timeString)
        val faTimeWithSeconds = PersianDateUtils.toPersianDigits(timeWithSecondsString)

        val standardDate = "$faYear/$faMonth/$faDay"
        val writtenDate = "${PersianDateUtils.toPersianDigits(jd.toString())} $monthName $faYear"
        val fullDate = "$weekDayName $writtenDate"
        val fullDateTime = "$writtenDate - ساعت $faTime"
        val compactDateTime = "$standardDate - $faTime"

        return listOf(
            DateTimeFormatOption(
                title = "تاریخ عددی",
                formattedText = standardDate,
                category = "تاریخ"
            ),
            DateTimeFormatOption(
                title = "تاریخ متنی",
                formattedText = writtenDate,
                category = "تاریخ"
            ),
            DateTimeFormatOption(
                title = "تاریخ کامل با روز",
                formattedText = fullDate,
                category = "تاریخ"
            ),
            DateTimeFormatOption(
                title = "ساعت جاری",
                formattedText = faTime,
                category = "ساعت"
            ),
            DateTimeFormatOption(
                title = "ساعت دقیق با ثانیه",
                formattedText = faTimeWithSeconds,
                category = "ساعت"
            ),
            DateTimeFormatOption(
                title = "تاریخ و ساعت",
                formattedText = fullDateTime,
                category = "ترکیبی"
            ),
            DateTimeFormatOption(
                title = "تاریخ و ساعت کوتاه",
                formattedText = compactDateTime,
                category = "ترکیبی"
            )
        )
    }
}
