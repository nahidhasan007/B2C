package net.sharetrip.tracker.utils

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.sharetrip.base.data.PrefKey
import net.sharetrip.tracker.R
import net.sharetrip.tracker.network.DataManager
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

fun Fragment.makeToolbarGone() {
    (activity as AppCompatActivity).findViewById<AppBarLayout>(R.id.app_bar_layout).visibility =
        View.GONE
}

fun Fragment.makeToolbarVisible() {
    (activity as AppCompatActivity).findViewById<AppBarLayout>(R.id.app_bar_layout).visibility =
        View.VISIBLE
}
