package com.sharetrip.base.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ktx.BuildConfig
import java.util.*

object ShareTripAppConstants {

    /** Apple Sign In **/

    private const val CLIENT_ID = "net.sharetrip.android"
    private const val BEFORE_REDIRECT_URI = "api/v1/auth/apple-token"
    const val REDIRECT_URI = "https://sharetrip.net?appleToken="
    private const val SCOPE = "name%20email"
    private const val RESPONSE_TYPE = "code%20id_token"
    private const val RESPONSE_MODE = "form_post"
    private const val AUTH_URL = "https://appleid.apple.com/auth/authorize"
    const val APPLE_TOKEN_KEY: String = "appleToken"
    const val NOTIFICATION_DATA: String = "notification_data"
    const val errorMsg = "Something went wrong"
    const val PASSWORD_MIN_CHAR_LEN = 8

    fun getAppleAuthUrl(): String {
        val state = UUID.randomUUID().toString()

        val baseURL = if (BuildConfig.DEBUG) {
            "https://stg-api.sharetrip.net/"
        } else {
            "https://api.sharetrip.net/"
        }

        return AUTH_URL + "?response_type=" + RESPONSE_TYPE + "&response_mode=" + RESPONSE_MODE +
                "&client_id=" + CLIENT_ID + "&scope=" + SCOPE + "&state=" + state + "&redirect_uri=" +
                baseURL + BEFORE_REDIRECT_URI
    }

    /** FLIGHT TRACKER:  **/

    const val AVIATION_EDGE_FLIGHT_TIME_TABLE_URL = "http://aviation-edge.com/v2/public/timetable"
    const val AVIATION_EDGE_LIVE_FLIGHT = "https://aviation-edge.com/v2/public/flights"
    const val AVIATION_EDGE_API_KEY = "ba6d3f-56e6bf"
    const val AIRLINES_IMAGE_FETCHING_BASE_URL = "https://tbbd-flight.s3.ap-southeast-1.amazonaws.com/airlines-logo/"
    const val FLIGHT_TRACKER_GOOGLE_MAP_ZOOM_LEVEL = 10f

    /** Blog */

    const val BLOG_SHARE_URL = "https://sharetrip.net/blog/post/"
    const val TWO_DIGIT_DECIMAL_FORMAT = "%.2f"

    const val TRAVEL_ADVICE_API = "99a8e32b-1938-4b9d-84af-685aae98b0d6"
    const val NO_BAGGAGE = "no_baggage"

    /** Crisp Chat*/

    const val CRISP_WEBSITE_ID = "14009585-79af-47ed-ab9a-4b2d8133a3bd"


    @JvmStatic
    @BindingAdapter("imageDrawableId")
    fun setImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    /** Notification Channel name **/
    const val LOCAL_NOTIFICATION = "local_channel"

    // OTP Resend Timer & formats
    const val COUNTDOWN_FORMAT = "%02d:%02d"
    const val COUNTDOWN_TIME = 60 * 1000L
    const val COUNTDOWN_INTERVAL = 1000L
}
