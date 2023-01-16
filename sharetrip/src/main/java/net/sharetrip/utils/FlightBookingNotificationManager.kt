package net.sharetrip.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.R
import net.sharetrip.utils.OnetimeBackgroundNotification.Companion.local_notification_description
import net.sharetrip.utils.OnetimeBackgroundNotification.Companion.local_notification_title
import java.util.*

object FlightBookingNotificationManager {

    fun setLocalNotification(bookingDate: Long?, returnBookingDate: Long?, context: Context) {
        val calendar: Calendar = Calendar.getInstance()
        val nowMillis = calendar.timeInMillis
        if (bookingDate != null && bookingDate != 0L) {
            scheduleOneTimeNotification(
                bookingDate - (nowMillis + 21600000L),
                bookingDate,
                context
            )
        }
        if (returnBookingDate != null && returnBookingDate != 0L) {
            scheduleOneTimeNotification(
                returnBookingDate - (nowMillis + 21600000L),
                returnBookingDate,
                context
            )
        }
    }

    private fun scheduleOneTimeNotification(initialDelay: Long, bookingDate: Long, context: Context) {

        val onetimeWork = OneTimeWorkRequest.Builder(OnetimeBackgroundNotification::class.java)
            .setInitialDelay(initialDelay, java.util.concurrent.TimeUnit.MILLISECONDS)
        val data = Data.Builder()
        data.putString(
            local_notification_description,
            R.string.local_notification_description.toString() + DateUtil.parseDisplayDateTimePatternFromMillisecond(
                bookingDate
            )
        )
        data.putString(local_notification_title, R.string.local_notification_title.toString())
        onetimeWork.setInputData(data.build())
        context.let { WorkManager.getInstance(it).enqueue(onetimeWork.build()) }
    }

}