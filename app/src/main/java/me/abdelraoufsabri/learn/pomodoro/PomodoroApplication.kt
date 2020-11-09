package me.abdelraoufsabri.learn.pomodoro

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class PomodoroApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this);
    }
}