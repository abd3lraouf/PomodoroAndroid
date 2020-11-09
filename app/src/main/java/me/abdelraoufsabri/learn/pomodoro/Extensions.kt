package me.abdelraoufsabri.learn.pomodoro

import android.app.ActivityManager
import android.content.Context
import android.text.format.DateUtils
import kotlinx.serialization.json.Json
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Duration

object Extensions {
    val Number.minutesAsMillis
        get() = Duration.ofMinutes(this.toLong()).toMillis()

    val Long.timeFormat: String
        get() {
//            val duration = Duration.ofMillis(this)
//            val minutes = duration.toMinutes()
//            val seconds = duration.minusMinutes(minutes).seconds
//            return String.format("%02d:%02d", minutes, seconds)
            return DateUtils.formatElapsedTime(this / 1000)
        }

    val eventBus = EventBus.getDefault()

    val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }
}