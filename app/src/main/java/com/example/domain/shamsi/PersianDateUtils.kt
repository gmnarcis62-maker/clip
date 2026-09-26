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

    fun toPersianDigits(number: Long): String = toPersianDigits(number.toString())
    fun toPersianDigits(number: Int): String = toPersianDigits(number.toString())

    /**
     * تبدیل تاریخ میلادی به هجری شمسی (جلالی).
     * الگوریتم استاندارد از jdf.scr.ir — تصحیح‌شده
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) -
                ((gy2 + 99) / 100) + ((gy2 + 399) / 400) +
                gd + gDaysInMonth[gm - 1]
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm: Int
        val jd: Int
        if (days < 186) {
            jm = 1 + (days / 31)
            jd = 1 + (days % 31)
        } else {
            jm = 7 + ((days - 186) / 30)
            jd = 1 + ((days - 186) % 30)
        }
        return Triple(jy, jm, jd)
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

    fun getGregorianMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }

    fun getGregorianDayOfWeekName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> ""
        }
    }

    // ============ Shamsi format methods ============

    fun getCurrentPersianDate(): String = formatPersianDate(System.currentTimeMillis())

    fun formatPersianDate(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val (jy, jm, jd) = gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        return toPersianDigits(String.format(Locale.US, "%04d/%02d/%02d", jy, jm, jd))
    }

    fun formatPersianDateWithTime(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val (jy, jm, jd) = gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        val hh = cal.get(Calendar.HOUR_OF_DAY)
        val mm = cal.get(Calendar.MINUTE)
        return toPersianDigits(
            String.format(Locale.US, "%04d/%02d/%02d - %02d:%02d", jy, jm, jd, hh, mm)
        )
    }

    fun formatPersianDateWithMonthName(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val (jy, jm, jd) = gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        return "${toPersianDigits(jd)} ${getPersianMonthName(jm)} ${toPersianDigits(jy)}"
    }

    fun formatPersianDateWithWeekday(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val (jy, jm, jd) = gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        val weekday = getPersianDayOfWeekName(cal.get(Calendar.DAY_OF_WEEK))
        return "$weekday ${toPersianDigits(jd)} ${getPersianMonthName(jm)} ${toPersianDigits(jy)}"
    }

    fun formatPersianTime(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val hh = cal.get(Calendar.HOUR_OF_DAY)
        val mm = cal.get(Calendar.MINUTE)
        return toPersianDigits(String.format(Locale.US, "%02d:%02d", hh, mm))
    }

    // ============ Gregorian format methods ============

    fun getCurrentGregorianDate(): String = formatGregorianDate(System.currentTimeMillis())

    fun formatGregorianDate(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return String.format(
            Locale.US,
            "%04d/%02d/%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun formatGregorianDateWithTime(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return String.format(
            Locale.US,
            "%04d/%02d/%02d - %02d:%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE)
        )
    }

    fun formatGregorianDateWithMonthName(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return String.format(
            Locale.US,
            "%d %s %d",
            cal.get(Calendar.DAY_OF_MONTH),
            getGregorianMonthName(cal.get(Calendar.MONTH) + 1),
            cal.get(Calendar.YEAR)
        )
    }

    fun formatGregorianDateWithWeekday(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return String.format(
            Locale.US,
            "%s, %s %d, %d",
            getGregorianDayOfWeekName(cal.get(Calendar.DAY_OF_WEEK)),
            getGregorianMonthName(cal.get(Calendar.MONTH) + 1),
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.YEAR)
        )
    }

    fun formatGregorianTime(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        return String.format(
            Locale.US,
            "%02d:%02d",
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE)
        )
    }
}