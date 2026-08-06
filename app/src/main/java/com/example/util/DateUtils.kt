package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    /**
     * Calculates Expiration Date = Renewal Date + 1 Year (+1 Year Rule).
     * Automatically parses renewal date string (e.g. "2026-08-06" or "06/08/2026")
     * and returns the exact date 1 year later.
     */
    fun calculateOneYearExpiration(renewalDateStr: String): String {
        return try {
            val calendar = Calendar.getInstance()
            val parsedDate = parseDateString(renewalDateStr)
            if (parsedDate != null) {
                calendar.time = parsedDate
            }
            calendar.add(Calendar.YEAR, 1)
            isoFormat.format(calendar.time)
        } catch (e: Exception) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, 1)
            isoFormat.format(calendar.time)
        }
    }

    fun getCurrentDateIso(): String {
        return isoFormat.format(Date())
    }

    fun formatToDisplayDate(dateStr: String): String {
        return try {
            val date = parseDateString(dateStr) ?: Date()
            displayFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun parseDateString(dateStr: String): Date? {
        return try {
            if (dateStr.contains("-")) {
                isoFormat.parse(dateStr)
            } else if (dateStr.contains("/")) {
                val parts = dateStr.split("/")
                if (parts.size == 3) {
                    if (parts[0].length == 4) {
                        isoFormat.parse(dateStr.replace("/", "-"))
                    } else {
                        val day = parts[0].toInt()
                        val month = parts[1].toInt() - 1
                        val year = parts[2].toInt()
                        val cal = Calendar.getInstance()
                        cal.set(year, month, day)
                        cal.time
                    }
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getAmharicMonthName(month: Int): String {
        return when (month) {
            1 -> "መስከረም"
            2 -> "ጥቅምት"
            3 -> "ሕዳር"
            4 -> "ታኅሣሥ"
            5 -> "ጥር"
            6 -> "የካቲት"
            7 -> "መጋቢት"
            8 -> "ሚያዝያ"
            9 -> "ግንቦት"
            10 -> "ሰኔ"
            11 -> "ሐምሌ"
            12 -> "ነሐሴ"
            13 -> "ጳጉሜ"
            else -> "ነሐሴ"
        }
    }
}
