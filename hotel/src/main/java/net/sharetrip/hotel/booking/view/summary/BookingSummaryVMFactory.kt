package net.sharetrip.hotel.booking.view.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.hotel.booking.model.DiscountModel
import net.sharetrip.hotel.booking.model.HotelBookingParams
import net.sharetrip.hotel.booking.model.RoomBookingSummary
import net.sharetrip.hotel.booking.model.coupon.ListOfCoupon
import net.sharetrip.hotel.network.HotelBookingApiService

class BookingSummaryVMFactory(
    private val bookingParams: HotelBookingParams,
    private val bookingSummary: RoomBookingSummary,
    private val discountModel: DiscountModel,
    private val promotionalCoupons: ListOfCoupon,
    private var apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookingSummaryViewModel(
            bookingParams,
            bookingSummary,
            discountModel,
            promotionalCoupons,
            apiService,
            sharedPrefsHelper
        ) as T
    }
}
