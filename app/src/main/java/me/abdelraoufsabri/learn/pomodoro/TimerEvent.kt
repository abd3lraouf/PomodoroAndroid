package me.abdelraoufsabri.learn.pomodoro

data class TimerEvent(
    val status: TimerStatus,
    val sessionType: SessionType
)
