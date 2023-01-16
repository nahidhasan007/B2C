package net.sharetrip.tour.utils

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sharetrip.base.data.PrefKey
import net.sharetrip.tour.R
import net.sharetrip.tour.network.TourDataManager
import java.text.NumberFormat
import java.util.*

fun Fragment.setTitleAndSubtitle(title: String) {
    (this.activity as AppCompatActivity).supportActionBar?.title = title
}

fun Fragment.setFragmentSubtitle(subtitle: String) {
    (this.activity as AppCompatActivity).supportActionBar?.subtitle = subtitle
}

fun Fragment.setTripCoin() {
    var points = TourDataManager.getSharedPref(requireContext())[PrefKey.USER_TRIP_COIN, "0"]
    points = points.filter { it in '0'..'9' }
    (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).text =
        NumberFormat.getNumberInstance(Locale.US).format(points.toInt())
}

fun Fragment.hideTripCoin() {
    (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).visibility =
        View.GONE
}
