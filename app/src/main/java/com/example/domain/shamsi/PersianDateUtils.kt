package com.example.domain.shamsi

import java.util.Calendar
import java.util.Locale

object PersianDateUtils {

    private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun toPersianDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(PERSIAN_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toPersianDigits(number: Long): String {
        return toPersianDigits(number.toString())
    }

    fun toPersianDigits(number: Int): String {
        return toPersianDigits(number.toString())
    }

    /**
     * Converts a Gregorian timestamp to Solar Hijri (Shamsi) Date
     * Returns Triple(Year, Month, Day)
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) (gy + 1) else gy
        var gDayNo = 365 * gy + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400 - 80 + gd + gDaysInMonth[gm - 1]
        val jy = 979 + 33 * (gDayNo / 12053)
        gDayNo %= 12053
        var jyAdd = 4 * (gDayNo / 1461)
        gDayNo %= 1461
        if (gDayNo >= 366) {
            jyAdd += (gDayNo - 1) / 365
            gDayNo = (gDayNo - 1) % 365
        }
        val jYear = jy + jyAdd
        val jm: Int
        val jd: Int
        if (gDayNo < 186) {
            jm = 1 + gDayNo / 31
            jd = 1 + (gDayNo % 31)
        } else {
            jm = 7 + (gDayNo - 186) / 30
            jd = 1 + ((gDayNo - 186) % 30)
        }
        return Triple(jYear, jm, jd)
    }

    fun getPersianMonthName(month: Int): String {
        return when (month) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> ""
        }
    }

    fun getPersianDayOfWeekName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SATURDAY -> "شنبه"
            Calendar.SUNDAY -> "یک‌شنبه"
            Calendar.MONDAY -> "دوشنبه"
            Calendar.TUESDAY -> "سه‌شنبه"
            Calendar.WEDNESDAY -> "چهارشنبه"
            Calendar.THURSDAY -> "پنج‌شنبه"
            Calendar.FRIDAY -> "جمعه"
            else -> ""
        }
    }

    fun getCurrentPersianDate(): String {
        return formatPersianDate(System.currentTimeMillis())
    }

    fun formatPersianDate(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val formatted = String.format(Locale.US, "%04d/%02d/%02d", jy, jm, jd)
        return toPersianDigits(formatted)
    }

    fun formatPersianDateWithTime(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val formatted = String.format(Locale.US, "%04d/%02d/%02d - %02d:%02d", jy, jm, jd, hour, minute)
        return toPersianDigits(formatted)
    }

    fun formatPersianTime(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val formatted = String.format(Locale.US, "%02d:%02d", hour, minute)
        return toPersianDigits(formatted)
    }
}
