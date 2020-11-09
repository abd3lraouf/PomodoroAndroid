package me.abdelraoufsabri.learn.pomodoro

import android.content.Intent

object Constants {
    val ACTION_START ="TimerAction.START"
    val ACTION_PAUSE ="TimerAction.PAUSE"
    val ACTION_STOP ="TimerAction.STOP"

    val GOODTIME_NOTIFICATION = "goodtime.notification"
    val GOODTIME_NOTIFICATION_ID = 551

    object PREFERENCE_KEY{
        val TIMER_STATUS = "${javaClass.simpleName}.STATUS"
        val SESSION_REMAINING = "${javaClass.simpleName}.REMAINING"
        val SESSION_TYPE = "${javaClass.simpleName}.SESSION_TYPE"
        val SESSION_SETTINGS = "${javaClass.simpleName}.SESSION_SETTINGS"
    }
}