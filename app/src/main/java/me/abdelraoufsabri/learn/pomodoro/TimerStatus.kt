package me.abdelraoufsabri.learn.pomodoro

import kotlinx.serialization.Serializable

@Serializable
sealed class TimerStatus {
    @Serializable
    object STARTED : TimerStatus()

    @Serializable
    object FINISHED : TimerStatus()

    @Serializable
    object STOPPED : TimerStatus()

    @Serializable
    object PAUSED : TimerStatus()

    @Serializable
    class TICKING(val remaining: Long) : TimerStatus()
}