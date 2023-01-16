package com.sharetrip.base

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.sharetrip.base.common.CrashLibrary
import timber.log.Timber

open class BaseApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            val mdDebugTree = Timber.DebugTree()
            Timber.plant(mdDebugTree)
        } else {
            val mCrashReportingTree = CrashReportingTree()
            Timber.plant(mCrashReportingTree)
        }
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(mPriority: Int, mTag: String?,
                         message: String, mThrowable: Throwable?) {
            if (mPriority == Log.VERBOSE || mPriority == Log.DEBUG) {
                return
            }
            CrashLibrary.log(mPriority, mTag, message)
            if (mThrowable != null) {
                if (mPriority == Log.ERROR) {
                    CrashLibrary.logError(mThrowable)
                } else if (mPriority == Log.WARN) {
                    CrashLibrary.logWarning(mThrowable)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun getContext(context: Context): BaseApplication {
            return context.applicationContext as BaseApplication
        }
    }
}
