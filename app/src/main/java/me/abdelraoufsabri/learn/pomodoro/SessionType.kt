package me.abdelraoufsabri.learn.pomodoro

import kotlinx.serialization.Serializable
import me.abdelraoufsabri.learn.pomodoro.Extensions.minutesAsMillis

@Serializable
sealed class SessionType(val duration: Long) {
    val settings = Companion.settings

    private companion object {
        val settings = Settings()
    }

    @Serializable
    data class Settings(
        var workDuration: Long = 2000L, // 25.minutesAsMillis,
        var shortBreakDuration: Long = 1000, // 5.minutesAsMillis,
        var longBreakDuration: Long = 2000, // 15.minutesAsMillis,
        var longBreakAfter: Int = 4,
        var intervalCount: Int = 1,
    ) {
        fun shouldHaveLongBreak(): Boolean {
            return (intervalCount == longBreakAfter).also { if (it) intervalCount = 1 else ++intervalCount }
        }
    }

    @Serializable
    object WORK : SessionType(settings.workDuration)

    @Serializable
    object SHORT_BREAK : SessionType(settings.shortBreakDuration)

    @Serializable
    object LONG_BREAK : SessionType(settings.longBreakDuration)
}

