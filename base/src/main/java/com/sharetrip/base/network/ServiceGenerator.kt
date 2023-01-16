package com.sharetrip.base.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.sharetrip.base.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {

    private val shareTripApiUrl = if (BuildConfig.DEBUG) {
        "https://stg-api.sharetrip.net/".toHttpUrlOrNull()
    } else {
        "https://api.sharetrip.net/".toHttpUrlOrNull()
    }

    private val logger: HttpLoggingInterceptor = if (BuildConfig.DEBUG) {
        val data = HttpLoggingInterceptor()
        data.setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
        val data = HttpLoggingInterceptor()
        data.setLevel(HttpLoggingInterceptor.Level.NONE)
    }

     private val client = OkHttpClient.Builder()
         .addNetworkInterceptor(StethoInterceptor())
         .addInterceptor(UserAgentInterceptor("ShareTrip", "version name")) //Todo
         .addInterceptor(logger)
         .connectTimeout(2, TimeUnit.MINUTES)
         .readTimeout(1, TimeUnit.MINUTES)
         .writeTimeout(1, TimeUnit.MINUTES)
         .build()

     private val retrofit = Retrofit.Builder()
         .client(client)
         .baseUrl(shareTripApiUrl!!)
         .addCallAdapterFactory(NetworkResponseAdapterFactory())
         .addConverterFactory(MoshiConverterFactory.create())
         .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
         .build()

    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }
}
