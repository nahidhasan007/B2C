package com.sharetrip.base.network

import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

class UserAgentInterceptor(private val userAgent: String) : Interceptor {

    constructor(appName: String?, appVersion: String?) : this(String.format(Locale.US,
            "%s/%s (Android %s; %s; %s; %s;)",
            appName,
            appVersion,
            Build.VERSION.RELEASE,
            Build.MODEL,
            Build.BRAND,
            Build.DEVICE))

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val userAgentRequest = chain.request()
                .newBuilder()
                .header("User-Agent", userAgent)
                .addHeader("st-access", "7C8ABC7DFB20B0D4528E532DA4DA664C7028E4AF")
                .build()
        return chain.proceed(userAgentRequest)
    }
}
