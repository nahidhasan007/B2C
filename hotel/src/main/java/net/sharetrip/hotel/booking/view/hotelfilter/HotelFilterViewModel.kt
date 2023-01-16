package net.sharetrip.hotel.booking.view.hotelfilter

import android.view.View
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.utils.SingleLiveEvent
import net.sharetrip.shared.utils.analytics.AnalyticsProvider

class HotelFilterViewModel : BaseViewModel() {
    private val hotelEventManager =
        AnalyticsProvider.hotelEventManager(AnalyticsProvider.getFirebaseAnalytics())
    val clickOnSearch = SingleLiveEvent<Any>()
    val clickOnReset = MutableLiveData<Boolean>()
    val liveData = MutableLiveData<@HotelFilterTypeEnum Int>()

    fun onClickSearchOnFilter() {
        clickOnSearch.call()
    }

    fun onClickFilterReset() {
        clickOnReset.value = true
    }

    fun onClickFilterIcon(view: View) {
        if (view is CardView) {
            when (view.id) {
                R.id.card_view_price -> liveData.value = PRICE
                R.id.card_view_hotel_search -> liveData.value = SEARCH
                R.id.card_view_neighborhood -> {
                    hotelEventManager.clickOnNeighborHood()
                    liveData.value = NEIGHBORHOOD
                }
                R.id.card_view_location_range -> {
                    hotelEventManager.clickOnLocationRange()
                    liveData.value = LOCATION_RANGE
                }
                R.id.card_view_property_rating -> {
                    hotelEventManager.clickOnPropertyRating()
                    liveData.value = PROPERTY_RATING
                }
                R.id.card_view_meal -> liveData.value = MEAL
                R.id.card_view_property_type -> liveData.value = PROPERTY_TYPE
                R.id.card_view_facility -> liveData.value = FACILITIES
                R.id.card_view_interest -> {
                    hotelEventManager.clickOnPOI()
                    liveData.value = POINT_OF_INTEREST
                }
            }
        }
    }
}
