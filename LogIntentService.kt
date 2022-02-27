package com.amvac.amvacrfid.utils.logfile

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent

@SuppressLint("Registered")
class LogIntentService : IntentService("LogIntentService") {
    override fun onCreate() {
        super.onCreate()
        LoggerFile.logD(LogIntentService::class.simpleName!!, "onCreate()", "onCreate() method called")
    }

    override fun onHandleIntent(intent: Intent?) {
        LoggerFile.logD(LogIntentService::class.simpleName!!, "onHandleIntent()", "onHandleIntent() method is called")
        // TODO call web API to send the Log File to server and after uploading the log file, delete the log file by calling LogTimer.stopLogTimer() method
    }
}