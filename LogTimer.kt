package com.amvac.amvacrfid.utils.logfile

import android.content.Context
import android.content.Intent
import java.util.*

class LogTimer {
    companion object {
        private var logTimer: Timer = Timer()
        fun startTimer(context: Context, delay: Long, period: Long) {
            logTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    LoggerFile.logD(LogTimer::class.simpleName!!, "run()", "LogTimer scheduled where delay time is: $delay and period is: $period")
                    context.startService(Intent(context, LogIntentService::class.java))
                    // TODO start IntentService that will call web API to send the log file to server by calling web service in it.
                }
            }, delay, period)
        }

        fun stopLogTimer() {
            LoggerFile.logD(LogTimer::class.simpleName!!, "stopTimer", "stopping LogTimer")
            logTimer.cancel()
            LoggerFile.logD(LogTimer::class.simpleName!!, "stopTimer", "LogTimer stopped")
        }
    }
}