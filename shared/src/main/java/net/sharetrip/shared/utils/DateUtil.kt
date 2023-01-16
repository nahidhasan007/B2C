package net.sharetrip.shared.utils

import android.annotation.SuppressLint
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object DateUtil {
    const val API_DATE_PATTERN = "yyyy-MM-dd"
    private const val DISPLAY_COMMON_DATE_PATTERN = "dd-MM-yyyy"
    private const val DISPLAY_LONG_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val DISPLAY_DATE_PATTERN = "d MMM ''yy"
    const val DISPLAY_MONTH_PATTERN = "MMMM yyyy"
    const val MAX_VALID_AGE = 120

    fun getDateFromMilliSecond(milliSecond: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSecond
        val df = SimpleDateFormat(DISPLAY_DATE_PATTERN)
        return df.format(calendar.time)
    }

    @get:SuppressLint("SimpleDateFormat")
    val todayApiDateFormat: String
        get() {
            val mCalendar = todayDateCalender
            val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
            return mDateFormat.format(mCalendar.time)
        }

    @JvmStatic
    @get:SuppressLint("SimpleDateFormat")
    val tomorrowApiDateFormat: String
        get() {
            val mCalendar = tomorrowDateCalender
            val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
            return mDateFormat.format(mCalendar.time)
        }

    @get:SuppressLint("SimpleDateFormat")
    val dayAfterTomorrowApiDateFormat: String
        get() {
            val mCalendar = dayAfterTomorrowDateCalender
            val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
            return mDateFormat.format(mCalendar.time)
        }

    @get:SuppressLint("SimpleDateFormat")
    val dayAfterOneYearApiDateFormat: String
        get() {
            val mCalendar = Calendar.getInstance()
            mCalendar.add(Calendar.YEAR, 1)
            val mDateFormat = SimpleDateFormat(DISPLAY_COMMON_DATE_PATTERN)
            return mDateFormat.format(mCalendar.time)
        }

    val dayAfterTomorrowDateInMillisecond: Long
        get() = dayAfterTomorrowDateCalender.timeInMillis

    private val tomorrowDateCalender: Calendar
        get() {
            val mCalendar = Calendar.getInstance()
            mCalendar.add(Calendar.DATE, 1)
            return mCalendar
        }

    val dayAfterTomorrowDateCalender: Calendar
        get() {
            val mCalendar = Calendar.getInstance()
            mCalendar.add(Calendar.DATE, 2)
            return mCalendar
        }

    @JvmStatic
    @SuppressLint("SimpleDateFormat")
    fun getApiDateFormat(num: Int): String {
        val mCalendar = getNextDateCalender(num)
        val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
        return mDateFormat.format(mCalendar.time)
    }

    private fun getNextDateCalender(days: Int): Calendar {
        val mCalendar = Calendar.getInstance()
        mCalendar.add(Calendar.DATE, days)
        return mCalendar
    }

    private val todayDateCalender: Calendar
        get() = Calendar.getInstance()

    @SuppressLint("SimpleDateFormat")
    fun parseApiDateFormatFromMillisecond(mMillisecond: Long): String {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = mMillisecond
        val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
        return mDateFormat.format(mCalendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDisplayCommonDatePatternFromMillisecond(mMillisecond: Long): String {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = mMillisecond
        val mDateFormat = SimpleDateFormat(DISPLAY_COMMON_DATE_PATTERN)
        return mDateFormat.format(mCalendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseDateToMillisecond(mDateString: String?): Long {
        return if (!mDateString.isNullOrEmpty()) {
            val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
            val mCalendar = Calendar.getInstance()
            val mDate = mDateFormat.parse(mDateString)
            mCalendar.time = mDate
            mCalendar.timeInMillis
        } else
            return System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseStringToMillisecond(mDateString: String?, format: String): Long {
        return if (!mDateString.isNullOrEmpty()) {
            val mDateFormat = SimpleDateFormat(format)
            val mCalendar = Calendar.getInstance()
            val mDate = mDateFormat.parse(mDateString)
            mCalendar.time = mDate
            mCalendar.timeInMillis
        } else
            return System.currentTimeMillis()
    }

    @JvmStatic
    @Throws(ParseException::class)
    fun getYearfromDate(dateString: String?): Int {
        val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
        val mCalendar = Calendar.getInstance()
        val mDate = mDateFormat.parse(dateString)
        mCalendar.time = mDate
        return mCalendar[Calendar.YEAR]
    }

    @JvmStatic
    @Throws(ParseException::class)
    fun getmonthfromDate(dateString: String?): Int {
        val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
        val mCalendar = Calendar.getInstance()
        val mDate = mDateFormat.parse(dateString)
        mCalendar.time = mDate
        return mCalendar[Calendar.MONTH]
    }

    @JvmStatic
    @Throws(ParseException::class)
    fun getdayfromDate(dateString: String?): Int {
        val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
        val mCalendar = Calendar.getInstance()
        val mDate = mDateFormat.parse(dateString)
        mCalendar.time = mDate
        return mCalendar[Calendar.DAY_OF_MONTH]
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseDisplayDateToMillisecond(mDateString: String?): Long {
        val mDateFormat = SimpleDateFormat(DISPLAY_COMMON_DATE_PATTERN)
        val mCalendar = Calendar.getInstance()
        val mDate = mDateFormat.parse(mDateString)
        mCalendar.time = mDate
        return mCalendar.timeInMillis
    }

    @JvmStatic
    @SuppressLint("SimpleDateFormat")
    fun parseDisplayDateFormatFromApiDateFormat(dateString: String?): String {
        return revampingDateFormatFromCurrentToGiven(
            dateString!!,
            DateFormatPattern.API_DATE_PATTERN,
            DateFormatPattern.DISPLAY_DATE_PATTERN
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDisplayCommonDateFormatFromApiDateFormat(dateString: String?): String {
        return revampingDateFormatFromCurrentToGiven(
            dateString!!,
            DateFormatPattern.API_DATE_PATTERN,
            DateFormatPattern.DISPLAY_COMMON_DATE_PATTERN
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun parseApiDateFormatFromDisplayCommonDateFormat(dateString: String?): String {
        if (dateString == null || dateString.isEmpty()) return ""
        return revampingDateFormatFromCurrentToGiven(
            dateString,
            DateFormatPattern.DISPLAY_COMMON_DATE_PATTERN,
            DateFormatPattern.API_DATE_PATTERN
        )
    }

    fun parseApiDateFormatFromDisplayDateFormat(dateString: String?): String {
        if (dateString == null || dateString.isEmpty()) return ""
        return revampingDateFormatFromCurrentToGiven(
            dateString,
            DateFormatPattern.DISPLAY_DATE_PATTERN,
            DateFormatPattern.API_DATE_PATTERN
        )
    }

    fun isDateIsValid(date: String?): Boolean {
        val pattern = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}")
        return pattern.matcher(date).matches()
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDisplayDateFormatFromApiDateFormatData(dateString: String?): String {
        return revampingDateFormatFromCurrentToGiven(
            dateString!!,
            DateFormatPattern.FLIGHT_API_DATE_PATTERN,
            DateFormatPattern.DISPLAY_DATE_PATTERN
        )
    }

    fun parseDisplayDayNameFormatFromApiDateFormat(dateString: String?): String {
        return revampingDateFormatFromCurrentToGiven(
            dateString!!,
            DateFormatPattern.API_DATE_PATTERN,
            DateFormatPattern.DISPLAY_DAY_PATTERN
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDisplayDateFromDate(mDate: Date?): String {
        val mDisplayDateFormat = SimpleDateFormat(DISPLAY_DATE_PATTERN)
        return mDisplayDateFormat.format(mDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDisplayDateFromDateForNewCalendar(mDate: LocalDate?): String {
        val headerDateFormatter = DateTimeFormatter.ofPattern(DISPLAY_DATE_PATTERN)
        return headerDateFormatter.format(mDate)
    }

    fun parseDisplayDateFromDateForNewCalendarDot(mDate: LocalDate?): String {
        val headerDateFormatter = DateTimeFormatter.ofPattern(API_DATE_PATTERN)
        return headerDateFormatter.format(mDate)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseApiDateFormatFromDisplayDate(mDate: String?): String {
        return parseApiDateFormatFromMillisecond(parseDateToMillisecond(mDate))
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseDisplayDateMonthFormatFromApiDateFormat(mDateString: String?): String {
        val mApiDateFormat = SimpleDateFormat(API_DATE_PATTERN)
        val mDisplayDateFormat = SimpleDateFormat(DISPLAY_DATE_PATTERN)
        val mDate = mApiDateFormat.parse(mDateString)
        return mDisplayDateFormat.format(mDate)
    }

    fun parseDateFromMillisecond(mMillisecond: Long): Date {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = mMillisecond
        return mCalendar.time
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseDateTimeToMillisecond(
        dateString: String?,
        time: String?,
        dateFormat: String = "MM/d/yyyy HH:mm"
    ): Long {
        return if (!dateString.isNullOrEmpty() && !time.isNullOrEmpty()) {
            val mDateFormat = SimpleDateFormat(dateFormat)
            val mCalendar = Calendar.getInstance()
            val mDate = mDateFormat.parse("$dateString $time")
            mCalendar.time = mDate
            mCalendar.timeInMillis
        } else
            System.currentTimeMillis()
    }


    @JvmStatic
    fun getNumberOfDay(dateString: String?): String {
        return dateString!!.substring(8)
    }

    @JvmStatic
    fun getMonthYear(dateString: String): String {
        var index = Integer.valueOf(dateString.substring(5, 7))
        return months[--index] + " ' " + dateString.substring(2, 4)
    }

    fun getWeekDay(dateString: String?): String {
        val calendar = Calendar.getInstance()
        try {
            calendar.timeInMillis = parseDateToMillisecond(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val index = calendar[Calendar.DAY_OF_WEEK]
        return days[index - 1]
    }

    @Throws(ParseException::class)
    fun getAge(birthDate: String?): Int {
        val calendar = Calendar.getInstance()
        val currentYear = calendar[Calendar.YEAR]
        calendar.timeInMillis = parseDateToMillisecond(birthDate)
        val birthYear = calendar[Calendar.YEAR]
        return currentYear - birthYear
    }

    @Throws(ParseException::class)
    fun getAgeForFlight(birthDate: String?, flightDate: String?): Int {
        val calendar1 = Calendar.getInstance()
        calendar1.timeInMillis = parseDateToMillisecond(flightDate)
        val currentYear = calendar1[Calendar.YEAR]
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = parseDateToMillisecond(birthDate)
        val birthYear = calendar2[Calendar.YEAR]
        var age = currentYear - birthYear
        if (calendar1[Calendar.DAY_OF_YEAR] < calendar2[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    fun getMonthYearTrimmed(dateString: String): String {
        var index = Integer.valueOf(dateString.substring(5, 7))
        return months[--index] + " " + dateString.substring(2, 4)
    }

    fun getDayAfterTomorrowDateInMillisecondForVisa(): Long {
        return dayAfterTomorrowDateInMillisecond
    }

    @SuppressLint("SimpleDateFormat")
    fun parseDisplayDateFormatFromMilliSecond(millisecond: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millisecond
        val mDateFormat = SimpleDateFormat(API_DATE_PATTERN)
        val mDisplayDateFormat = SimpleDateFormat(DISPLAY_DATE_PATTERN)
        val time = mDateFormat.format(calendar.time)
        val mDate = mDateFormat.parse(time)
        return mDisplayDateFormat.format(mDate)
    }

    fun parseDisplayDateFormatFromLongDateString(mDateString: String): String {
        val mApiDateFormat = SimpleDateFormat(DISPLAY_LONG_DATE_PATTERN)
        val mApiDatePattern = SimpleDateFormat(API_DATE_PATTERN)
        val mDate = mApiDateFormat.parse(mDateString)
        return mApiDatePattern.format(mDate)
    }

    fun revampingDateFormatFromCurrentToGiven(
        dateString: String,
        currentFormat: DateFormatPattern,
        expectedFormat: DateFormatPattern
    ): String {
        val existingDateFormat = SimpleDateFormat(currentFormat.datePattern)
        val expectedDateFormat = SimpleDateFormat(expectedFormat.datePattern)
        return try {
            val date = existingDateFormat.parse(dateString)
            return expectedDateFormat.format(date)
        } catch (exception: Exception) {
            ""
        }
    }

    fun isTimeBetweenTwoTimes(startTime: String, endTime: String, departureTime: String): Boolean {
        var isBetween = false

        return try {
            val format = SimpleDateFormat("HH:mm")
            val departureFormat = SimpleDateFormat("hh:mm a")
            val date1 = format.parse(startTime)
            val date2 = format.parse(endTime)
            val departureDate = departureFormat.parse(departureTime)

            if (date1.before(departureDate) && date2.after(departureDate)) {
                isBetween = true
            }

            return isBetween
        } catch (exception: Exception) {
            isBetween
        }
    }


    fun getDuration(startTime: String?, endTime: String, durationIn: String?): Long {
        val format = SimpleDateFormat(API_DATE_PATTERN)
        var startT = startTime
        if (startT == null)
            startT = getCurrentDate()
        val date1 = format.parse(startT)
        val date2 = format.parse(parseApiDateFormatFromDisplayCommonDateFormat(endTime))
        val diff: Long = date2.time - date1.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when (durationIn) {
            "hour" -> hours
            "min" -> minutes
            "sec" -> seconds
            "day" -> days
            "mon" -> days / 30
            else -> days / 30 / 12
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun parseDisplayDateTimePatternFromMillisecond(millisecond: Long): String {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = millisecond
        val mDateFormat = SimpleDateFormat(DateFormatPattern.DISPLAY_DATE_TIME_PATTERN.datePattern)
        return mDateFormat.format(mCalendar.time)
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    fun increaseOrDecreaseDate(date: String, days: Int): String {
        val inputDateFormat = SimpleDateFormat(API_DATE_PATTERN)
        val calendar = Calendar.getInstance()
        calendar.time = inputDateFormat.parse(date)!!
        calendar.add(Calendar.DATE, days)
        return inputDateFormat.format(calendar.time)
    }

    private val months =
        arrayOf(
            "JAN",
            "FAB",
            "MAR",
            "APR",
            "MAY",
            "JUN",
            "JUL",
            "AUG",
            "SEP",
            "OCT",
            "NOV",
            "DEC"
        )
    private val days = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
}

enum class DateFormatPattern(val datePattern: String) {
    API_DATE_PATTERN("yyyy-MM-dd"),
    API_DATE_TIME_PATTERN("yyyy-MM-dd HH:mm"),
    DISPLAY_DATE_PATTERN_FOR_HOTEL("d MMM"),
    FLIGHT_API_DATE_PATTERN("MM/dd/yyyy"),
    DISPLAY_COMMON_DATE_PATTERN("dd-MM-yyyy"),
    DISPLAY_DATE_PATTERN("d MMM ''yy"),
    CIRIUM_API_DATE_PATTERN("yyyy-MM-dd'T'HH:mm:ss.SSS"),
    DISPLAY_DATE_TIME_PATTERN("h:mm a, d MMM ''yy"),
    DISPLAY_DAY_PATTERN("EEEE")
}
