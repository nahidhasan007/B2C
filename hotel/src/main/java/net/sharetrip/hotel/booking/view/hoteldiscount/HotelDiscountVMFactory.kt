package net.sharetrip.hotel.booking.view.hoteldiscount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.booking.model.HotelBookingParams
import net.sharetrip.hotel.booking.model.RoomBookingSummary
import net.sharetrip.hotel.booking.model.coupon.ListOfCoupon
import net.sharetrip.hotel.network.HotelBookingApiService

class HotelDiscountVMFactory(
    private val hotelBookingParams: HotelBookingParams,
    private val summary: RoomBookingSummary,
    private val promotionalCoupons: ListOfCoupon,
    private val apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HotelDiscountViewModel(
            hotelBookingParams,
            summary,
            promotionalCoupons,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
