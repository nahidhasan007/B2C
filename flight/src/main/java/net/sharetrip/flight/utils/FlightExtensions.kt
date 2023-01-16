package net.sharetrip.flight.utils

import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.FlightBookingActivity

fun Fragment.setTripCoin(coin: String) {
    (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).text = coin
}

fun Fragment.setTitleSubTitle(title: String? = "", subTitle: String? = "") {
    (activity as AppCompatActivity).supportActionBar?.title = title
    (activity as AppCompatActivity).supportActionBar?.subtitle = subTitle
}

fun Fragment.setTitleSubTitle(title: StringBuilder?, subTitle: StringBuilder?) {
    (activity as AppCompatActivity).supportActionBar?.title = title
    (activity as AppCompatActivity).supportActionBar?.subtitle = subTitle
}

fun Fragment.setTitleSubTitle(title: SpannableStringBuilder?, subTitle: StringBuilder?) {
    (activity as AppCompatActivity).supportActionBar?.title = title
    (activity as AppCompatActivity).supportActionBar?.subtitle = subTitle
}

fun Fragment.getTripCoin(): String {
    return (activity as FlightBookingActivity).findViewById<TextView>(R.id.text_view_trip_coin).text.filter { it in '0'..'9' }
        .toString()
}