package net.sharetrip.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.sharetrip.R
import com.sharetrip.base.data.PrefKey.FIREBASE_DEVICE_TOKEN
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.UserNotification
import com.sharetrip.base.utils.ShareTripAppConstants.NOTIFICATION_DATA
import net.sharetrip.shared.utils.convertString
import net.sharetrip.view.dashboard.DashboardActivity
import java.util.*

class ShareTripMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            if (remoteMessage.data.isNotEmpty()) {
                if (remoteMessage.data["image"] != null && remoteMessage.data["image"]!!.isNotEmpty()) {
                    getBitmapFromGlide(
                        remoteMessage.data["title"],
                        remoteMessage.data["body"],
                        remoteMessage.data["image"],
                        remoteMessage.data["publishDate"]
                    )
                } else {
                    sendNotification(
                        remoteMessage.data["title"],
                        remoteMessage.data["body"],
                        null,
                        "",
                        remoteMessage.data["publishDate"]
                    )
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun sendNotification(
        messageTitle: String? = "",
        messageBody: String? = "",
        img: Bitmap?,
        path: String? = "",
        publishDate: String?
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val m = (Date().time / 1000L % Int.MAX_VALUE).toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel-01"
            val channelName = "Channel Name"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val bigPictureStyle: NotificationCompat.BigPictureStyle?
        val bigTextStyle: NotificationCompat.BigTextStyle?
        val icon: Bitmap?
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder = NotificationCompat.Builder(this, "channel-01")
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        mBuilder.setContentTitle(messageTitle)

        if (img != null) {
            icon = img
            bigPictureStyle = NotificationCompat.BigPictureStyle()
            bigPictureStyle.setBigContentTitle(messageTitle)
            bigPictureStyle.setSummaryText(
                HtmlCompat.fromHtml(
                    messageBody!!,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ).toString()
            )
            bigPictureStyle.bigPicture(icon)
            mBuilder.setStyle(bigPictureStyle)
            mBuilder.setLargeIcon(icon)
        } else {
            bigTextStyle = NotificationCompat.BigTextStyle()
            bigTextStyle.setBigContentTitle(messageTitle)
            bigTextStyle.bigText(messageBody)
            mBuilder.setStyle(bigTextStyle)
        }

        mBuilder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        mBuilder.setSound(defaultSoundUri)
        mBuilder.setContentText(messageBody)
        mBuilder.priority = NotificationCompat.PRIORITY_MAX
        mBuilder.color = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        mBuilder.setAutoCancel(true)
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val userNotification = UserNotification(
            "",
            messageBody, path!!,
            "ANDROID", "", publishDate, 1236544, messageTitle, true
        )
        intent.putExtra(NOTIFICATION_DATA, userNotification.convertString())

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        } else {
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        mBuilder.setContentIntent(resultPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationManager.notify(1, mBuilder.build())
        else
            notificationManager.notify(m, mBuilder.build())
    }

    private fun getBitmapFromGlide(
        messageTitle: String?,
        messageBody: String?,
        path: String?,
        publishDate: String?
    ) {
        Glide.with(this)
            .asBitmap()
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    sendNotification(messageTitle, messageBody, resource, path, publishDate)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    sendNotification(messageTitle, messageBody, null, path, publishDate)
                }
            })
    }

    override fun onNewToken(token: String) {
        storeTokenForServer(token)
    }

    private fun storeTokenForServer(token: String) {
        val sharedPrefsHelper = SharedPrefsHelper(application)
        sharedPrefsHelper.put(FIREBASE_DEVICE_TOKEN, token)
    }
}
