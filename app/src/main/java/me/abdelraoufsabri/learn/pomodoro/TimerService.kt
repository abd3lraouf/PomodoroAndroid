package me.abdelraoufsabri.learn.pomodoro

import android.content.Intent
import androidx.lifecycle.LifecycleService
import me.abdelraoufsabri.learn.pomodoro.Constants.ACTION_PAUSE
import me.abdelraoufsabri.learn.pomodoro.Constants.ACTION_START
import me.abdelraoufsabri.learn.pomodoro.Constants.ACTION_STOP
import me.abdelraoufsabri.learn.pomodoro.Constants.GOODTIME_NOTIFICATION_ID

private const val TAG = "TimerService"

class TimerService : LifecycleService() {
    lateinit var timer: PomodoroTimer
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        timer = PomodoroTimer()
        timer.restoreState(this)
        notificationHelper = NotificationHelper(this)
    }

    override fun onDestroy() {
        timer.persistState(this)
        super.onDestroy()
    }

    @Synchronized
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent == null) {
            return START_STICKY_COMPATIBILITY
        } else {
            when (intent.action!!) {
                ACTION_START -> {
                    timer.start()
                    startForeground(GOODTIME_NOTIFICATION_ID, notificationHelper.builder.build())
                }
                ACTION_PAUSE -> {
                    timer.pause()
                    stopForeground(true)
                    stopSelfResult(startId);
                }
                ACTION_STOP -> {
                    timer.stop()
                    stopForeground(true)
                    stopSelfResult(startId);
                }
            }
        }
        return START_STICKY
    }
}