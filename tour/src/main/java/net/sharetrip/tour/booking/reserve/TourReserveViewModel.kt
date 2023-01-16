package net.sharetrip.tour.booking.reserve

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.shared.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.model.PeriodX
import net.sharetrip.tour.model.TourParam

class TourReserveViewModel(private val tourOffer: PeriodX, private val tourParam: TourParam) :
    BaseViewModel() {

    private val _navigateToPickUpLocation = MutableLiveData<Event<TourParam>>()
    val navigateToPickUpLocation: LiveData<Event<TourParam>>
    get() = _navigateToPickUpLocation

    private var prices: IntArray

    val navigation = MutableLiveData<Boolean>()
    val msg = MutableLiveData<String>()

    init {
        tourParam.tourOfferId = tourOffer.id
        tourParam.departureTime = tourOffer.departureTime

        prices = intArrayOf(
            tourOffer.perPersonPax, tourOffer.price2Pax, tourOffer.price3Pax,
            tourOffer.price4Pax, tourOffer.price5Pax, tourOffer.price6Pax, tourOffer.price7Pax,
            tourOffer.price8Pax, tourOffer.price9Pax, tourOffer.price10Pax
        )
    }

    fun navigationToPickLocation(
        date: String,
        adultNo: String,
        child7to12No: String,
        child3to6No: String,
        infantNo: String
    ) {
        if (date.isNotEmpty()) {
            tourParam.apply {
                tourDate = date
                if (adultNo.isNotEmpty()) adult = adultNo.toInt()
                if (child7to12No.isNotEmpty()) child7to12 = child7to12No.toInt()
                if (child3to6No.isNotEmpty()) child3to6 = child3to6No.toInt()
                if (infantNo.isNotEmpty()) infant = infantNo.toInt()
            }

            var adultCost = prices[tourParam.adult - 1] * tourParam.adult
            if (adultCost == 0) adultCost = tourParam.adult * prices[0]

            val infantCost = tourOffer.infant0To2 * tourParam.infant
            val child3to6Cost = tourOffer.child3To6 * tourParam.child3to6
            val child7to12Cost = tourOffer.child7To12 * tourParam.child7to12
            tourParam.child3t6Amount = child3to6Cost
            tourParam.child7t12Amount = child7to12Cost
            tourParam.infantAmount = infantCost
            tourParam.adultAmount = adultCost
            tourParam.totalAmount = adultCost + infantCost + child3to6Cost + child7to12Cost

            if (tourParam.inDataValid()) {
                _navigateToPickUpLocation.value = Event(tourParam)
            } else {
                msg.value = "Fill up required info"
            }
        } else {
            msg.value = "Fill up required info"
        }
    }

    fun onClickSaveMenu() {
        navigation.value = true
    }

}
