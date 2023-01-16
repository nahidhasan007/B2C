package com.sharetrip.base.common

class CrashLibrary private constructor() {
    companion object {
        fun log(priority: Int, tag: String?, message: String?) {
        }

        fun logWarning(t: Throwable?) {
        }

        fun logError(t: Throwable?) {
        }
    }

    init {
        throw AssertionError("No instances.")
    }
}