package net.sharetrip.hotel.booking.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class HotelBookingMainViewModelFactory(private val sharedPrefsHelper: SharedPrefsHelper) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotelBookingMainViewModel::class.java))
            return HotelBookingMainViewModel(sharedPrefsHelper) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
