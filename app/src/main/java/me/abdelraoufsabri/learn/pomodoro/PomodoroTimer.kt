package me.abdelraoufsabri.learn.pomodoro

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.abdelraoufsabri.learn.pomodoro.Constants.PREFERENCE_KEY.SESSION_REMAINING
import me.abdelraoufsabri.learn.pomodoro.Constants.PREFERENCE_KEY.SESSION_SETTINGS
import me.abdelraoufsabri.learn.pomodoro.Constants.PREFERENCE_KEY.SESSION_TYPE
import me.abdelraoufsabri.learn.pomodoro.Constants.PREFERENCE_KEY.TIMER_STATUS
import me.abdelraoufsabri.learn.pomodoro.Extensions.eventBus
import me.abdelraoufsabri.learn.pomodoro.Extensions.json
import me.abdelraoufsabri.learn.pomodoro.SessionType.*
import me.abdelraoufsabri.learn.pomodoro.TimerStatus.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

private const val TAG = "PomodoroTimer"

class PomodoroTimer {
    private val step = 1000L
    private var remaining: AtomicLong = AtomicLong()
    private lateinit var sessionType: SessionType
    private var timerStatus: TimerStatus = STOPPED
        set(value) {
            field = value
            eventBus.post(TimerEvent(timerStatus, sessionType))
        }

    private var client: Disposable = Disposable.disposed()
    private val timer = Observable.interval(0, step, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map { remaining.addAndGet(-step) }
        .takeWhile { remaining.get() >= -step }
        .doOnNext { timerStatus = (if (it == -step) FINISHED else TICKING(it)) }

    fun start() {
        sessionType = when (timerStatus) {
            FINISHED -> when (sessionType) { // Start next
                is WORK -> if (sessionType.settings.shouldHaveLongBreak()) LONG_BREAK else SHORT_BREAK // start break or long break
                else -> WORK // start work
            }
            PAUSED -> sessionType // keep old session
            else -> WORK
        }

        if (timerStatus !is PAUSED) remaining.set(sessionType.duration)
        timerStatus = STARTED

        client.dispose()
        client = timer.subscribe()
    }

    fun pause() {
        timerStatus = PAUSED
        client.dispose()
    }

    fun stop() {
        timerStatus = STOPPED
        client.dispose()
    }

    fun persistState(context: Context) {
        context.getSharedPreferences("service_settings", MODE_PRIVATE).edit {
            val timerStatusString = json.encodeToString(timerStatus)
            val sessionTypeString = json.encodeToString(sessionType)
            val sessionSettingsString = json.encodeToString(sessionType.settings)

            putLong(SESSION_REMAINING, remaining.get())
            putString(TIMER_STATUS, timerStatusString)
            putString(SESSION_TYPE, sessionTypeString)
            putString(SESSION_SETTINGS, sessionSettingsString)
        }
    }

    fun restoreState(context: Context) {
        context.getSharedPreferences("service_settings", MODE_PRIVATE).apply {
            val defaultRemaining = 0L
            val defaultTimerStatus = json.encodeToString(STOPPED as TimerStatus)
            val defaultSessionType = json.encodeToString(WORK as SessionType)
            val defaultSettings = json.encodeToString(Settings())

            val remainingFromPrefs = getLong(SESSION_REMAINING, defaultRemaining)
            val timerStatusString = getString(TIMER_STATUS, defaultTimerStatus)!!
            val sessionTypeString = getString(SESSION_TYPE, defaultSessionType)!!
            val sessionSettingsString = getString(SESSION_SETTINGS, defaultSettings)!!

            remaining.set(remainingFromPrefs)
            json.decodeFromString<SessionType>(sessionTypeString).also { sessionType = it }
            json.decodeFromString<TimerStatus>(timerStatusString).also { timerStatus = it  }
            sessionType.settings.apply {
                val settings = json.decodeFromString<Settings>(sessionSettingsString)
                this.workDuration = settings.workDuration
                this.shortBreakDuration = settings.shortBreakDuration
                this.longBreakDuration = settings.longBreakDuration
                this.longBreakAfter = settings.longBreakAfter
                this.intervalCount = settings.intervalCount
            }
        }

        eventBus.post(TimerEvent(timerStatus, sessionType))
    }
}