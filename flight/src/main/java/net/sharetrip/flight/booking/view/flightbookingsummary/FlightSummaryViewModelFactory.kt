package net.sharetrip.flight.booking.view.flightbookingsummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.ItemTraveler
import net.sharetrip.flight.booking.model.coupon.CouponResponse
import net.sharetrip.flight.booking.model.flightresponse.DiscountModel
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights

class FlightSummaryViewModelFactory(
    private val flightsInfo: Flights,
    private val mFlightSearch: FlightSearch,
    private val discountModel: DiscountModel,
    private val couponResponse: CouponResponse,
    private val itemTravellers: List<ItemTraveler>,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightSummaryViewModel::class.java))
            return FlightSummaryViewModel(
                flightsInfo,
                mFlightSearch,
                discountModel,
                couponResponse,
                itemTravellers,
                sharedPrefsHelper,
            ) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
