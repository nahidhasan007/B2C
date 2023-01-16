package net.sharetrip.visa.utils

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sharetrip.base.data.PrefKey
import net.sharetrip.visa.R
import net.sharetrip.visa.history.VisaHistoryActivity
import net.sharetrip.visa.network.DataManager
import java.text.NumberFormat
import java.util.*


fun Fragment.setTitleAndSubtitle(title: String) {
    (requireActivity() as AppCompatActivity).supportActionBar?.title = title
}

fun Fragment.setToolbarTripCoin() {
    var points = DataManager.getSharedPref(requireContext())[PrefKey.USER_TRIP_COIN, "0"]
    points = points.filter { it in '0'..'9' }
    if(points.isBlank()){
        points = "0"
    }
    (activity as VisaHistoryActivity).findViewById<TextView>(R.id.text_view_trip_coin).text =
        NumberFormat.getNumberInstance(Locale.US).format(points.toInt())
}
