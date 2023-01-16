package net.sharetrip.bus.utils

import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.view.BusBookingActivity
import java.text.NumberFormat
import java.util.*

fun Fragment.setSpannedTitle(title: Any?) {
    (this.activity as AppCompatActivity).supportActionBar?.title = if (title == null) title else
        when (title) {
            is StringBuilder -> title
            is SpannableStringBuilder -> title
            else -> title as String
        }
}

fun Fragment.setFragmentSubtitle(subtitle: Any?) {
    (this.activity as BusBookingActivity).supportActionBar?.subtitle =
        if (subtitle == null) subtitle else
            when (subtitle) {
                is StringBuilder -> subtitle
                is SpannableStringBuilder -> subtitle
                else -> subtitle as String
            }
}

fun Fragment.setTripCoin() {
    val sharedPref = SharedPrefsHelper(requireContext())
    var points = sharedPref[PrefKey.USER_TRIP_COIN, ""]
    points = points.filter { it in '0'..'9' }

    if (points.isBlank()) {
        points = "0"
        sharedPref.put(PrefKey.USER_TRIP_COIN, points)
    }

    val tripCoinTextView =
        (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin)
    tripCoinTextView.visibility = View.VISIBLE
    tripCoinTextView.text = NumberFormat.getNumberInstance(Locale.US).format(points.toInt())
}

fun Fragment.setTripCoinGone() {
    val tripCoinTextView =
        (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin)
    tripCoinTextView.visibility = View.GONE
}