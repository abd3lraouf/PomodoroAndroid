package me.abdelraoufsabri.learn.pomodoro

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.abdelraoufsabri.learn.pomodoro.Constants.ACTION_PAUSE
import me.abdelraoufsabri.learn.pomodoro.Constants.ACTION_START
import me.abdelraoufsabri.learn.pomodoro.Constants.ACTION_STOP
import me.abdelraoufsabri.learn.pomodoro.Extensions.eventBus
import me.abdelraoufsabri.learn.pomodoro.Extensions.timeFormat
import me.abdelraoufsabri.learn.pomodoro.TimerStatus.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventBus.register(this)
        setContentView(R.layout.activity_main)

        btnStart.setOnClickListener { startTimerService(ACTION_START) }

        btnPause.setOnClickListener { startTimerService(ACTION_PAUSE) }
        btnStop.setOnClickListener { startTimerService(ACTION_STOP) }
    }

    private fun startTimerService(requestedAction: String) {
        val intent = Intent(this, TimerService::class.java).apply { this.action = requestedAction }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTimerEvent(event: TimerEvent) {
        session_type.text = event.sessionType.javaClass.simpleName.capitalize(Locale.getDefault()) + " (${event.sessionType.settings.intervalCount})"
        timer_status.text = event.status.javaClass.simpleName.capitalize(Locale.getDefault())

        if (event.status is TICKING) time_text.text = event.status.remaining.timeFormat
        if (event.status is STOPPED) time_text.text = event.sessionType.duration.timeFormat

        if (event.status is FINISHED){
            startTimerService(ACTION_START)
        }
    }
}