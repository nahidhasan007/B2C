package net.sharetrip.shared.utils

import net.sharetrip.shared.model.ErrorResponse
import net.sharetrip.shared.model.UserInfo
import net.sharetrip.shared.model.UserNotification
import com.squareup.moshi.Moshi

private val moshi = Moshi.Builder().build()

fun String.convertErrorMsg(): String {
    val jsonAdapter = moshi.adapter(ErrorResponse::class.java)
    val errorObj = jsonAdapter.fromJson(this)

    return errorObj?.message ?: "Something wrong"
}

fun UserNotification.convertString() : String {
    val jsonAdapter = moshi.adapter(UserNotification::class.java)
    return jsonAdapter.toJson(this)
}

fun String.getUserNotification() : UserNotification? {
    val jsonAdapter = moshi.adapter(UserNotification::class.java)
    return jsonAdapter.fromJson(this)
}

fun String.getUserInfo() : UserInfo? {
    val jsonAdapter = moshi.adapter(UserInfo::class.java)
    return jsonAdapter.fromJson(this)
}