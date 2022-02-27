package com.amvac.amvacrfid.utils.logfile

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.util.Log
import org.koin.android.BuildConfig

import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class LoggerFile {
    companion object {

        private var deviceDetails =
            (Build.MANUFACTURER + "\t" + Build.MODEL + "\t" + Build.VERSION.RELEASE + "\t " + Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name)
        private var appVersionName = BuildConfig.VERSION_NAME

        //private val logFilePath = Environment.getExternalStorageDirectory()
        private const val LOG_FILE_NAME = "LogFile.txt"
        private const val DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss"
        private var userId: String? = null


        /* fun setUserId(userId: String) {
             Companion.userId = userId
         }*/

        fun logD(className: String, methodName: String, message: String) {
            Log.d("$className -> $methodName", message)
            writeToLogFile(className, methodName, message)
        }

        fun logE(className: String, methodName: String, message: String) {
            Log.e("$className -> $methodName", message)
            writeToLogFile(className, methodName, message)
        }

        /* fun logI(className: String, methodName: String, message: String) {
             Log.i("$className -> $methodName", message)
             writeToLogFile(className, methodName, message)
         }

         fun logV(className: String, methodName: String, message: String) {
             Log.v("$className -> $methodName", message)
             writeToLogFile(className, methodName, message)
         }*/

        private fun writeToLogFile(className: String, methodName: String, message: String) {

            val myfolder = Environment.getExternalStorageDirectory().toString() + "/" + "CrashReport"
            //
            val pdfFolder = File(myfolder)
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir()
            }

            File(pdfFolder,
                LOG_FILE_NAME).appendText("${getCurrentDateTime()}()\t" + "$deviceDetails\t" + "$appVersionName\t" + "$userId\t" + "$className\t" + "$methodName\t" + message + "\n")
        }

        /* fun deleteLogFile() {
             File(logFilePath, LOG_FILE_NAME).delete()
         }*/


        @SuppressLint("SimpleDateFormat")
        private fun getCurrentDateTime(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) LocalDateTime.now().format(DateTimeFormatter.ofPattern(
                DATE_TIME_FORMAT))
            else SimpleDateFormat(DATE_TIME_FORMAT).format(Date())
        }
    }
}