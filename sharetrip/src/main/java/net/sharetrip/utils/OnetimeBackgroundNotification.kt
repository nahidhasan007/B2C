package net.sharetrip.utils

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.sharetrip.base.utils.ShareTripAppConstants
import net.sharetrip.R
import net.sharetrip.view.dashboard.DashboardActivity
import kotlin.random.Random

class OnetimeBackgroundNotification(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val intent = Intent(context, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val notification =
            NotificationCompat.Builder(context, ShareTripAppConstants.LOCAL_NOTIFICATION).apply {
                setContentIntent(pendingIntent)
            }
        notification.setContentTitle(workerParameters.inputData.getString(local_notification_title))
        notification.setContentText(
            workerParameters.inputData.getString(
                local_notification_description
            )
        )
        notification.priority = NotificationCompat.PRIORITY_HIGH
        notification.setCategory(NotificationCompat.CATEGORY_ALARM)
        notification.setSmallIcon(R.mipmap.ic_launcher_foreground)
        notification.color =
            ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        notification.setSound(sound)
        notification.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        with(NotificationManagerCompat.from(context)) {
            notify(Random.nextInt(), notification.build())
        }
    }

    companion object {
        const val local_notification_description = "one_time_notification_description"
        const val local_notification_title = "one_time_notification_title"
    }
}

