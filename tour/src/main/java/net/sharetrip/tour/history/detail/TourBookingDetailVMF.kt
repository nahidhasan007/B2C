package net.sharetrip.tour.history.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class TourBookingDetailVMF(
    private val bookingCode: String,
    private val repo: TourBookingDetailRepo,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourBookingDetailViewModel::class.java))
            return TourBookingDetailViewModel(bookingCode, repo, sharedPrefsHelper) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
