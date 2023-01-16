package net.sharetrip.flight.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sharetrip.base.event.Event
import net.sharetrip.flight.booking.model.*
import net.sharetrip.flight.booking.model.filter.Refundable

class ShearedViewModel : ViewModel() {
    val isRefundableCodeSets = MutableLiveData<ArrayList<Refundable>>()
    val airlinesCodeSets = MutableLiveData<ArrayList<String>>()
    val layoverCodeSets = MutableLiveData<ArrayList<String>>()
    val stopCodeSets = MutableLiveData<ArrayList<String>>()
    val weightCodeSets = MutableLiveData<ArrayList<String>>()
    var weightString = String()
    val outboundCodeSets = MutableLiveData<ArrayList<String>>()
    val returnCodeSets = MutableLiveData<ArrayList<String>>()
    val newPrice = MutableLiveData<Price>()
    val isSelectAll = MutableLiveData<Boolean>()

    val hotelNewPrice = MutableLiveData<PriceRangeDumy?>()
    val hotelSearchData = MutableLiveData<String?>()
    val locationRangeData = MutableLiveData<String?>()
    val neighborhoodCodeSet = MutableLiveData<ArrayList<String>?>()
    val propertyRatingCodeSet = MutableLiveData<ArrayList<String>?>()
    val mealsCodeSet = MutableLiveData<ArrayList<String>?>()
    val propertyTypeCodeSet = MutableLiveData<ArrayList<String>?>()
    val facilityCodeSet = MutableLiveData<ArrayList<String>?>()
    val pointOfInterestCodeSet = MutableLiveData<ArrayList<String>?>()

    val mealTypeViewModel = MutableLiveData<Pair<String, String>>()
    val requestWheelchairViewModel = MutableLiveData<Pair<String, String>>()
    val questionBtnLiveData = MutableLiveData<Int>()
    val covidTestOption = MutableLiveData<CovidTestOption>()
    val travelInsuranceOption = MutableLiveData<Event<TravelInsuranceOption>>()
    val baggageInsuranceOption = MutableLiveData<Event<BaggageInsuranceOption>>()
    val covidServiceDetails = MutableLiveData<String>()
    val travelInsuranceDetails = MutableLiveData<String>()
    val baggageInsuranceDetails = MutableLiveData<String>()
    val onScheduleSelected = MutableLiveData<String>()

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
        requestWheelchairViewModel.value = null
        mealTypeViewModel.value = null
        questionBtnLiveData.value = null
    }
}
