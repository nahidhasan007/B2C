package net.sharetrip.tour.booking.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.tour.model.TourBookingParam
import net.sharetrip.tour.model.TourSummary
import java.lang.IllegalArgumentException

class TourSummaryVMF(
    private val bookingParam: TourBookingParam,
    val summary: TourSummary,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repo: TourSummaryRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourSummaryViewModel::class.java))
            return TourSummaryViewModel(bookingParam, summary, sharedPrefsHelper, repo) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
