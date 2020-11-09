package me.abdelraoufsabri.learn.pomodoro

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import me.abdelraoufsabri.learn.pomodoro.Constants.GOODTIME_NOTIFICATION
import java.util.concurrent.TimeUnit.MILLISECONDS

class NotificationHelper(context: Context?) : ContextWrapper(context) {
    private val manager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val builder: NotificationCompat.Builder

    fun clearNotification() {
        manager.cancelAll()
    }

    private fun buildProgressText(duration: Long): CharSequence {
        var secondsLeft = MILLISECONDS.toSeconds(duration)
        val minutesLeft = secondsLeft / 60
        secondsLeft %= 60
        return (if (minutesLeft > 9) minutesLeft else "0$minutesLeft").toString() + ":" +
                if (secondsLeft > 9) secondsLeft else "0$secondsLeft"
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun initChannels() {
        val channelInProgress = NotificationChannel(
            GOODTIME_NOTIFICATION, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_LOW
        )
        channelInProgress.setBypassDnd(true)
        channelInProgress.setShowBadge(true)
        channelInProgress.setSound(null, null)
        manager.createNotificationChannel(channelInProgress)
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initChannels()
        }
        val notifyIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder = NotificationCompat.Builder(this, GOODTIME_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_status_icon)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(notifyPendingIntent)
            .setOngoing(true)
            .setShowWhen(false)
    }
}