package net.sharetrip.tour.booking.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class TourDetailVMF(
    private val code: String,
    private val isInternetAvailable: Boolean,
    private val repo: TourDetailRepo,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourDetailViewModel::class.java))
            return TourDetailViewModel(code, isInternetAvailable, repo, sharedPrefsHelper) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
