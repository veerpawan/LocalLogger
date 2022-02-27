package com.amvac.amvacrfid.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.amvac.amvacrfid.R
import com.amvac.amvacrfid.rfidsdk.common.ExceptionForToast
import com.amvac.amvacrfid.rfidsdk.util.Configuration
import com.amvac.amvacrfid.utils.AmvacAppConstants
import com.amvac.amvacrfid.utils.MySharedPreferences
import com.amvac.amvacrfid.utils.logfile.CrashAnalyzer
import com.amvac.amvacrfid.utils.logfile.LoggerFile.Companion.logD
import com.senter.support.openapi.StUhf
import com.senter.support.openapi.StUhf.Function
import com.senter.support.openapi.StUhf.InterrogatorModel

import org.koin.android.ext.android.startKoin
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import kotlin.system.exitProcess


/**
 * description：
 * com.senter.demo.uhf.modelA
 * com.senter.demo.uhf.modelB
 * com.senter.demo.uhf.modelC
 * com.senter.demo.uhf.modelD2
 * com.senter.demo.uhf.modelE
 * com.senter.demo.uhf.modelF
 * every package represents a module modle.so before reading this project source code,please confirm which model in your pda or pad
 *
 */


class App : Application() {
    var context: Context? = null
    var mBaseUrl: String? = null
    var mBaseUrlType: String? = null


    override fun onCreate() {
        super.onCreate()
        mSinglton = this


        /*   if (LeakCanary.isInAnalyzerProcess(this)) {
               // This process is dedicated to LeakCanary for heap analysis.
               // You should not init your app in this process.
               return;
           }
           LeakCanary.install(this);*/

        startKoin(this, listOf(appModule))
        uhf = getUhf()
        getPreferences()
        //CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/nirmala_0.ttf").build())
        Thread.setDefaultUncaughtExceptionHandler { thread, e -> handleUncaughtException(thread, e) }

        appConext = this

    }

    companion object {


        lateinit var appConext: App
            private set
        private var activityVisible: Boolean = false
        private const val TAG = "MainApp"
        private var uhf: StUhf? = null
        private var mSinglton: App? = null
        private var mAppConfiguration: Configuration? = null
        @SuppressLint("StaticFieldLeak")
        var mPreferences: MySharedPreferences? = null

        fun getPreferences(): MySharedPreferences? {
            if (mPreferences == null) mPreferences = MySharedPreferences(mSinglton!!)
            return mPreferences
        }

        fun appInstance(): App? {
            return mSinglton
        }

        /**
         * create a uhf instance with the specified model if need
         */
        fun getUhf(): StUhf? {
            if (uhf == null) {
                uhf = StUhf.getUhfInstance(InterrogatorModel.InterrogatorModelD2)
                uhfInterfaceAsModel = InterrogatorModel.InterrogatorModelD2
            }
            return uhf
        }


        private fun uhf(): StUhf? {
            return uhf
        }

        @Throws(ExceptionForToast::class)
        fun uhfInit() {
            //Log.i(TAG, "App.uhfInit()")
            if (uhf == null) {


                throw ExceptionForToast(appConext.getString(R.string.no_device))
            }
            val inited = uhf!!.init()
            if (!inited) {
                throw ExceptionForToast(appConext.getString(R.string.text_try_again))
            }
        }

        fun uhfUninit() {
            if (uhf == null) {
                return
            }
            uhf!!.uninit()
        }

        fun uhfClear() {
            uhf = null
            uhfInterfaceAsModel = null
        }

        private var uhfInterfaceAsModel: InterrogatorModel? = null

        private fun uhfInterfaceAsModel(): InterrogatorModel {
            if (uhf == null || uhfInterfaceAsModel == null) {
                throw IllegalStateException()
            }
            return uhfInterfaceAsModel as InterrogatorModel
        }

        fun uhfInterfaceAsModelD2(): StUhf.InterrogatorModelDs.InterrogatorModelD2 {
            assetUhfInstanceObtained()
            assert(uhfInterfaceAsModel() == InterrogatorModel.InterrogatorModelD2)
            return uhf!!.getInterrogatorAs(StUhf.InterrogatorModelDs.InterrogatorModelD2::class.java)
        }


        private fun assetUhfInstanceObtained() {
            if (uhf == null || uhfInterfaceAsModel == null) {
                throw IllegalStateException()
            }
        }

        //stop the operation excuting by module,three times if need.
        fun stop(): Boolean {
            if (uhf != null) {
                if (uhf!!.isFunctionSupported(Function.StopOperation)) {
                    for (i in 0..2) {
                        if (uhf()!!.stopOperation()) {
                            return true
                        }
                    }
                    return false
                }
            }
            return true
        }

        //clear both mask setting
        fun clearMaskSettings() {
            if (uhf!!.isFunctionSupported(Function.DisableMaskSettings)) {
                uhf!!.disableMaskSettings()
            }
        }

        private fun appCfgSavedModel(): InterrogatorModel? {
            val modelName = appConfiguration().getString(appConext.getString(R.string.model_number), AmvacAppConstants.EMPTY)
            if (modelName!!.isNotEmpty()) {
                try {
                    return InterrogatorModel.valueOf(modelName)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return null
        }

        fun appCfgSaveModelClear() {
            appConfiguration().setString(appConext.getString(R.string.model_number), AmvacAppConstants.EMPTY)
        }

        fun appCfgSaveModel(model: InterrogatorModel?) {
            if (model == null) {
                throw NullPointerException()
            }
            appConfiguration().setString(appConext.getString(R.string.model_number), model.name)

        }

        //app configuration setting
        private fun appConfiguration(): Configuration {
            if (mAppConfiguration == null) {
                mAppConfiguration = Configuration(mSinglton, appConext.getString(R.string.setting_app), Context.MODE_PRIVATE)
            }
            return mAppConfiguration as Configuration
        }

        fun isActivityVisible(): Boolean {
            return activityVisible
        }

        fun activityResumed() {
            activityVisible = true
        }

        fun activityPaused() {
            activityVisible = false
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
        MultiDex.install(this)
    }


    //to handle uncaught exception while any thread execution
    private fun handleUncaughtException(thread: Thread, throwableException: Throwable) {
        thread.name

        val crashAnalyzer = CrashAnalyzer(throwableException)
        logD(
            throwableException.stackTrace[0].className,
            throwableException.stackTrace[0].methodName,
            crashAnalyzer.getAnalysis()
        )
        // kill off the crashed app
        exitProcess(1)
    }

}











