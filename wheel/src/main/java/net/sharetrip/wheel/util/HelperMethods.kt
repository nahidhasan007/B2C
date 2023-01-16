package net.sharetrip.wheel.util

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sharetrip.base.data.PrefKey
import net.sharetrip.wheel.R
import net.sharetrip.wheel.network.DataManager
import java.text.NumberFormat
import java.util.*

fun Fragment.setTitleAndSubtitle(title: String) {
    (this.activity as AppCompatActivity).supportActionBar?.title = title
}

fun Fragment.setTripCoin(tripCoin: String? = null) {
    if (tripCoin == null){
        var points = DataManager.getSharedPref(requireContext())[PrefKey.USER_TRIP_COIN, "0"]
        points = points.filter { it in '0'..'9' }
        (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).text =
                NumberFormat.getNumberInstance(Locale.US).format(points.toInt())
    }else{
        (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).text = tripCoin
    }
}
