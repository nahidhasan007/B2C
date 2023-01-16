package net.sharetrip.hotel.booking.view.travellerlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.booking.model.DiscountModel
import net.sharetrip.hotel.booking.model.HotelBookingParams

class TravellerListVMFactory(
    private val bookingParams: HotelBookingParams,
    private val roomSummary: String,
    private val discountModel: DiscountModel,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TravellerListViewModel(
            bookingParams,
            roomSummary,
            discountModel,
            sharedPrefsHelper
        ) as T
    }
}
