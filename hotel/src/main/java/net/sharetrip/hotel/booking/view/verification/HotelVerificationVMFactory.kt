package net.sharetrip.hotel.booking.view.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.booking.model.DiscountModel
import net.sharetrip.hotel.booking.model.HotelBookingParams
import net.sharetrip.hotel.network.HotelBookingApiService

class HotelVerificationVMFactory(
    private val bookingParams: HotelBookingParams?,
    private val bookingSummary: String?,
    private val discountModel: DiscountModel,
    private var apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HotelInfoVerificationViewModel(
            bookingParams,
            bookingSummary,
            discountModel,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
