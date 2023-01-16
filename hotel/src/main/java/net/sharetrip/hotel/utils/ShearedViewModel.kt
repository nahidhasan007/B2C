package net.sharetrip.hotel.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sharetrip.base.event.Event
import net.sharetrip.hotel.booking.model.PriceRangeDumy

class ShearedViewModel : ViewModel() {
    val airlinesCodeSets = MutableLiveData<ArrayList<String>>()
    val layoverCodeSets = MutableLiveData<ArrayList<String>>()
    val stopCodeSets = MutableLiveData<ArrayList<String>>()
    val weightCodeSets = MutableLiveData<ArrayList<String>>()
    val outboundCodeSets = MutableLiveData<ArrayList<String>>()
    val returnCodeSets = MutableLiveData<ArrayList<String>>()
    val isSelectAll = MutableLiveData<Event<Boolean>>()

    val hotelNewPrice = MutableLiveData<PriceRangeDumy?>()
    val hotelSearchData = MutableLiveData<String?>()
    val locationRangeData = MutableLiveData<String?>()
    val neighborhoodCodeSet = MutableLiveData<ArrayList<String>?>()
    val propertyRatingCodeSet = MutableLiveData<ArrayList<String>?>()
    val mealsCodeSet = MutableLiveData<ArrayList<String>?>()
    val propertyTypeCodeSet = MutableLiveData<ArrayList<String>?>()
    val facilityCodeSet = MutableLiveData<ArrayList<String>?>()
    val pointOfInterestCodeSet = MutableLiveData<ArrayList<String>?>()
    val pointOfInterestName = MutableLiveData<ArrayList<String>?>()

    val mealTypeViewModel = MutableLiveData<Pair<String,String>>()
    val requestWheelchairViewModel =MutableLiveData<Pair<String,String>>()
    val questionBtnLiveData = MutableLiveData<Int>()
    val covidServiceDetails = MutableLiveData<String>()

    fun onClearViewModel() {
        hotelNewPrice.value = null
        hotelSearchData.value = null
        locationRangeData.value = null
        neighborhoodCodeSet.value = null
        propertyRatingCodeSet.value = null
        mealsCodeSet.value = null
        propertyTypeCodeSet.value = null
        facilityCodeSet.value = null
        pointOfInterestCodeSet.value = null
        pointOfInterestName.value = null
    }
}
