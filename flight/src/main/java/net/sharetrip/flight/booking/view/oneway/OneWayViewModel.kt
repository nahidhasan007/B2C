package net.sharetrip.flight.booking.view.oneway

import android.content.Intent
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.booking.model.ChildrenDOB
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.TravellersInfo
import net.sharetrip.flight.booking.view.searchairport.SearchAirportFragment
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import timber.log.Timber
import java.text.ParseException
import java.util.*

class OneWayViewModel : BaseViewModel() {

    private val eventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())
    private val _navigateToTravelAdvice = MutableLiveData<Event<Boolean>>()
    private val flightEventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())

    val navigateToTravelAdvice: LiveData<Event<Boolean>>
        get() = _navigateToTravelAdvice

    val startDateInMillisecond: Long
        get() {
            return try {
                val mStartDateString = oneWayTripSearchModel.depart[0]
                DateUtil.parseDateToMillisecond(mStartDateString)
            } catch (e: ParseException) {
                Calendar.getInstance().timeInMillis
            }
        }

    val fromAirPortCode = ObservableField<String>()
    val toAirPortCode = ObservableField<String>()
    val departureDate = ObservableField<String>()
    val travelersAndClassCount = ObservableField<String>()
    val moveToTravellers = MutableLiveData<Event<TravellersInfo>>()

    var promotionalImage = ""
    var roundTripSearchModel: FlightSearch = FlightSearch()
    var oneWayTripSearchModel: FlightSearch = FlightSearch()
    var onSearchFlightClicked = MutableLiveData<Event<Boolean>>()

    init {
        roundTripSearchModel.initForRoundTrip()
        oneWayTripSearchModel.initForOneWay()
        updateInitInformation()
        travelersAndClassCount.set("${oneWayTripSearchModel.adult + oneWayTripSearchModel.child + oneWayTripSearchModel.infant} Traveller(s) - ${oneWayTripSearchModel.classType}")
    }

    private fun updateInitInformation() {
        val fromCode = oneWayTripSearchModel.origin[0]
        fromAirPortCode.set(fromCode)

        val toCode = oneWayTripSearchModel.destination[0]
        toAirPortCode.set(toCode)

        try {
            val dateInit = oneWayTripSearchModel.depart[0]
            val departureDateInit = DateUtil.parseDisplayDateFormatFromApiDateFormat(dateInit)
            departureDate.set(departureDateInit)
        } catch (e: ParseException) {
            Timber.d("ParseException %s", e.message)
        }
        travelersAndClassCount.set("${oneWayTripSearchModel.adult + oneWayTripSearchModel.child + oneWayTripSearchModel.infant} Traveller(s) - ${oneWayTripSearchModel.classType}")
    }

    private fun updateDate() {
        try {
            val mDate = oneWayTripSearchModel.depart[0]
            val mDepartureDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(mDate)
            departureDate.set(mDepartureDate)
        } catch (e: ParseException) {
            Timber.d("ParseException %s", e.message)
        }
    }

    fun onClickedTravelersAndClassCardView() {
        val travellersInfo = TravellersInfo(
            oneWayTripSearchModel.adult,
            oneWayTripSearchModel.child,
            oneWayTripSearchModel.infant,
            oneWayTripSearchModel.classType,
            oneWayTripSearchModel.depart[0],
            oneWayTripSearchModel.childDateOfBirthList
        )
        moveToTravellers.value = Event(travellersInfo)
    }

    fun onClickedTravelAdviceSearch() {
        _navigateToTravelAdvice.value = Event(true)
    }

    fun onCLickSwapAirport() {
        val codeFirst = oneWayTripSearchModel.origin[0]
        val codeSecond = oneWayTripSearchModel.destination[0]

        val oneWayTripOrigin = ArrayList<String>()
        oneWayTripOrigin.add(codeSecond)
        oneWayTripSearchModel.origin.clear()
        oneWayTripSearchModel.origin = oneWayTripOrigin

        val oneWayTripDestination = ArrayList<String>()
        oneWayTripDestination.add(codeFirst)
        oneWayTripSearchModel.destination.clear()
        oneWayTripSearchModel.destination = oneWayTripDestination

        fromAirPortCode.set(codeSecond)
        toAirPortCode.set(codeFirst)
    }

    fun onClickedSearchFlightButton() {
        eventManager.searchOneWayFlight()
        flightEventManager.searchOneWayFlight()
        onSearchFlightClicked.value = Event(true)
    }

    fun handleFromAddress(data: Intent) {
        val code = data.getStringExtra(SearchAirportFragment.ARG_AIRPORT_CODE)
        val city = data.getStringExtra(SearchAirportFragment.ARG_AIRPORT_CITY)
        val address = data.getStringExtra(SearchAirportFragment.ARG_AIRPORT_ADDRESS)

        val oneWayTripOrigin = oneWayTripSearchModel.origin
        oneWayTripOrigin.clear()
        oneWayTripOrigin.add(code!!)
        val oneWayTripOriginCity = oneWayTripSearchModel.originCity
        oneWayTripOriginCity.clear()
        oneWayTripOriginCity.add(city!!)
        val oneWayTripOriginAddress = oneWayTripSearchModel.originAddress
        oneWayTripOriginAddress.clear()
        oneWayTripOriginAddress.add(address!!)
        fromAirPortCode.set(code)

        val roundTripOrigin = roundTripSearchModel.origin
        roundTripOrigin.clear()
        roundTripOrigin.add(code)
        val roundTripOriginCity = roundTripSearchModel.originCity
        roundTripOriginCity.clear()
        roundTripOriginCity.add(city)
        val roundTripOriginAddress = roundTripSearchModel.originAddress
        roundTripOriginAddress.clear()
        roundTripOriginAddress.add(address)
    }

    fun handleToAddress(data: Intent) {
        val code = data.getStringExtra(SearchAirportFragment.ARG_AIRPORT_CODE)
        val city = data.getStringExtra(SearchAirportFragment.ARG_AIRPORT_CITY)
        val address = data.getStringExtra(SearchAirportFragment.ARG_AIRPORT_ADDRESS)
        val oneWayTripDestination = oneWayTripSearchModel.destination
        oneWayTripDestination.clear()
        oneWayTripDestination.add(code!!)

        val oneWayDestinationCity = oneWayTripSearchModel.destinationCity
        oneWayDestinationCity.clear()
        oneWayDestinationCity.add(city!!)

        val oneWayTripDestinationAddress = oneWayTripSearchModel.destinationAddress
        oneWayTripDestinationAddress.clear()
        oneWayTripDestinationAddress.add(address!!)
        toAirPortCode.set(code)

        val roundTripDestination = roundTripSearchModel.destination
        roundTripDestination.clear()
        roundTripDestination.add(code)
        val roundTripDestinationCity = roundTripSearchModel.destinationCity
        roundTripDestinationCity.clear()
        roundTripDestinationCity.add(city)
        val mmRoundTripDestinationAddress = roundTripSearchModel.destinationAddress
        mmRoundTripDestinationAddress.clear()
        mmRoundTripDestinationAddress.add(address)
    }

    fun handleDepartureDate(date: Long) {
        val startDate = DateUtil.parseApiDateFormatFromMillisecond(date)
        val oneWayTripDepart = oneWayTripSearchModel.depart
        oneWayTripDepart.clear()
        oneWayTripDepart.add(startDate)
        updateDate()
    }

    fun handleTravellerData(data: Intent) {
        oneWayTripSearchModel.adult = data.getIntExtra(EXTRA_NUMBER_OF_ADULT, 1)
        oneWayTripSearchModel.child = data.getIntExtra(EXTRA_NUMBER_OF_CHILDREN, 0)
        oneWayTripSearchModel.infant = data.getIntExtra(EXTRA_NUMBER_OF_INFANT, 0)
        oneWayTripSearchModel.classType = data.getStringExtra(EXTRA_TRIP_CLASS_TYPE).toString()

        if (oneWayTripSearchModel.child != 0)
            oneWayTripSearchModel.childDateOfBirthList =
                data.getParcelableArrayListExtra<ChildrenDOB>(EXTRA_CHILD_DOB_LIST) as ArrayList<ChildrenDOB>

        travelersAndClassCount.set(
            "${oneWayTripSearchModel.adult + oneWayTripSearchModel.child + oneWayTripSearchModel.infant} " +
                    "Traveller(s) - ${oneWayTripSearchModel.classType}"
        )
    }
}
