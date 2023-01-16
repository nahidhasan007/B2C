package net.sharetrip.holiday.utils

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.holiday.R
import com.sharetrip.base.data.PrefKey
import net.sharetrip.holiday.booking.HolidayBookingActivity
import net.sharetrip.holiday.history.HolidayHistoryActivity
import net.sharetrip.holiday.network.DataManager
import java.text.NumberFormat
import java.util.*

fun Fragment.setTitleAndSubtitle(title: String) {
    (this.activity as AppCompatActivity).supportActionBar?.title = title
}

fun Fragment.setFragmentSubtitle(subtitle: String) {
    (this.activity as AppCompatActivity).supportActionBar?.subtitle = subtitle
}

fun Fragment.setTripCoin() {
    var points = DataManager.getSharedPref(requireContext())[PrefKey.USER_TRIP_COIN, "0"]
    points = points.filter { it in '0'..'9' }
    (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).text =
        NumberFormat.getNumberInstance(Locale.US).format(points.toInt())
}

fun Fragment.hideTripCoin() {
    (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).visibility =
        View.GONE
}
