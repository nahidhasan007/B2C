package net.sharetrip.flight.booking.view.multicity

import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.booking.model.ChildrenDOB
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.MultiCityModel
import net.sharetrip.flight.booking.model.TravellersInfo
import net.sharetrip.flight.booking.view.searchairport.SearchAirportFragment.Companion.ARG_AIRPORT_ADDRESS
import net.sharetrip.flight.booking.view.searchairport.SearchAirportFragment.Companion.ARG_AIRPORT_CITY
import net.sharetrip.flight.booking.view.searchairport.SearchAirportFragment.Companion.ARG_AIRPORT_CODE
import net.sharetrip.shared.utils.*
import java.util.*

class MultiCityViewModel(
    private var searchQueryHint: String,
    private val searchQueryForTo: String,
    private val searchQueryForFrom: String,
    private val searchQueryForDate: String
) : BaseViewModel() {
    private var multiCityTripSearchModel = FlightSearch()
    private val flightEventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())
    private val _navigateToTravelAdvice = MutableLiveData<Event<Boolean>>()
    private var travellers: TravellersInfo
    private var startDateInMillisecond = 0L

    var isAddButtonEnabled = ObservableBoolean(true)
    var isRemoveButtonEnabled = ObservableBoolean()
    val navigateToTravelAdvice: LiveData<Event<Boolean>>
        get() = _navigateToTravelAdvice
    var travelersAndClassCount = ObservableField<String>()
    var promotionalImage = ""
    var isAirportLayoutClicked = MutableLiveData<Event<Boolean>>()
    val isRemoveItem = MutableLiveData<Event<Boolean>>()
    val changeItemAtPos = MutableLiveData<Pair<Int, MultiCityModel>>()
    var clickedPos = -1

    init {
        multiCityTripSearchModel.initForMultiCity()

        travelersAndClassCount.set("${multiCityTripSearchModel.adult + multiCityTripSearchModel.child + multiCityTripSearchModel.infant} Traveller(s) - ${multiCityTripSearchModel.classType}")

        travellers = TravellersInfo(
            1, 0, 0, multiCityTripSearchModel.classType, multiCityTripSearchModel.depart[0],
            arrayListOf()
        )
    }

    private fun checkAddRemoveButtonVisibility() {
        isRemoveButtonEnabled.set(multiCityTripSearchModel.multiCityModels.size > 2)
        isAddButtonEnabled.set(multiCityTripSearchModel.multiCityModels.size < 5)
    }

    fun onFromItemClick(position: Int) {
        clickedPos = position
        searchQueryHint = searchQueryForFrom
        isAirportLayoutClicked.value = Event(true)
    }

    fun onToItemClick(position: Int) {
        clickedPos = position
        searchQueryHint = searchQueryForTo
        isAirportLayoutClicked.value = Event(false)
    }

    fun onDateItemClick(position: Int, origin: String, destination: String) {
        clickedPos = position
        startDateInMillisecond = getStartDateInMillisecond(position)
        searchQueryHint = searchQueryForDate

        if (multiCityTripSearchModel.multiCityModels[clickedPos].origin.isNotEmpty() && multiCityTripSearchModel.multiCityModels[clickedPos].destination.isNotEmpty())
            navigateWithArgument(
                PICK_DEPARTURE_DATE_REQUEST,
                CalenderData(
                    startDateInMillisecond,
                    mDateHintText = "Departure Date",
                    fromAirportCode = multiCityTripSearchModel.multiCityModels[clickedPos].origin,
                    toAirportCode = multiCityTripSearchModel.multiCityModels[clickedPos].destination
                )
            )
        else
            showMessage(PLEASE_SELECT_ORIGIN_DESTINATION)
    }

    fun onRemoveCityButtonClicked() {
        checkAddRemoveButtonVisibility()
        multiCityTripSearchModel.multiCityModels.removeLast()
        isRemoveItem.value = Event(true)
        checkAddRemoveButtonVisibility()
    }

    fun onAddCityButtonClicked() {
        checkAddRemoveButtonVisibility()
        multiCityTripSearchModel.multiCityModels.add(MultiCityModel())
        isRemoveItem.value = Event(false)
        checkAddRemoveButtonVisibility()
    }

    fun onTravelersAndClassCardViewClicked() {
        navigateWithArgument(GOTO_TRAVELLER, travellers)
    }

    fun onSearchFlightButtonClicked() {
        flightEventManager.searchMultiCity()
        val dataSet = multiCityTripSearchModel.multiCityModels
        var error = false
        for (model in dataSet) {
            if (Strings.isBlank(model.origin)) {
                showMessage("Invalid Flying from")
                error = true
                break
            } else if (Strings.isBlank(model.destination)) {
                showMessage("Invalid Flying to")
                error = true
                break
            } else if (Strings.isBlank(model.departDate)) {
                showMessage("Invalid Date")
                error = true
                break
            }
        }

        if (!error) {
            navigateWithArgument(GOTO_FLIGHT_LIST, multiCityTripSearchModel)
        }
    }

    private fun getStartDateInMillisecond(position: Int): Long {
        val item = multiCityTripSearchModel.multiCityModels[position]
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val mStartDateInMillisecond: Long
        val mStartDateString = item.departDate
        mStartDateInMillisecond = if (Strings.isBlank(mStartDateString)) {
            calendar.timeInMillis
        } else {
            calendar.timeInMillis
        }
        return mStartDateInMillisecond
    }

    fun handleTravellerData(data: Intent) {
        multiCityTripSearchModel.adult = data.getIntExtra(EXTRA_NUMBER_OF_ADULT, 1)
        multiCityTripSearchModel.child = data.getIntExtra(EXTRA_NUMBER_OF_CHILDREN, 0)
        multiCityTripSearchModel.infant = data.getIntExtra(EXTRA_NUMBER_OF_INFANT, 0)
        multiCityTripSearchModel.classType = data.getStringExtra(EXTRA_TRIP_CLASS_TYPE).toString()

        if (multiCityTripSearchModel.child != 0)
            multiCityTripSearchModel.childDateOfBirthList =
                data.getParcelableArrayListExtra<ChildrenDOB>(EXTRA_CHILD_DOB_LIST) as ArrayList<ChildrenDOB>

        travelersAndClassCount.set("${multiCityTripSearchModel.adult + multiCityTripSearchModel.child + multiCityTripSearchModel.infant} " +
                "Traveller(s) - ${multiCityTripSearchModel.classType}")

        travellers = TravellersInfo(
            multiCityTripSearchModel.adult,
            multiCityTripSearchModel.child,
            multiCityTripSearchModel.infant,
            multiCityTripSearchModel.classType,
            multiCityTripSearchModel.depart[0],
            multiCityTripSearchModel.childDateOfBirthList
        )
    }

    fun handleFromAddress(data: Intent) {
        if (clickedPos in 0 until multiCityTripSearchModel.multiCityModels.size) {
            val mCode = data.getStringExtra(ARG_AIRPORT_CODE)
            val mCity = data.getStringExtra(ARG_AIRPORT_CITY)
            val mAddress = data.getStringExtra(ARG_AIRPORT_ADDRESS)
            val item = multiCityTripSearchModel.multiCityModels[clickedPos]
            item.origin = mCode!!
            item.originCity = mCity
            item.originAddress = mAddress
            changeItemAtPos.value = Pair(clickedPos, item)
            updateModelData()
        }
    }

    fun handleToAddress(data: Intent) {
        if (clickedPos in 0 until multiCityTripSearchModel.multiCityModels.size) {
            val mCode = data.getStringExtra(ARG_AIRPORT_CODE)
            val mCity = data.getStringExtra(ARG_AIRPORT_CITY)
            val mAddress = data.getStringExtra(ARG_AIRPORT_ADDRESS)
            val item = multiCityTripSearchModel.multiCityModels[clickedPos]
            item.destination = mCode!!
            item.destinationCity = mCity
            item.destinationAddress = mAddress
            changeItemAtPos.value = Pair(clickedPos, item)
            updateModelData()
        }
    }

    fun handleDepartureDate(data: Long) {
        if (clickedPos in 0 until multiCityTripSearchModel.multiCityModels.size) {
            val mStartDate = DateUtil.parseApiDateFormatFromMillisecond(data)
            val item = multiCityTripSearchModel.multiCityModels[clickedPos]
            item.departDate = mStartDate
            changeItemAtPos.value = Pair(clickedPos, item)
            updateModelData()
        }
    }

    private fun updateModelData() {
        val dataSet = multiCityTripSearchModel.multiCityModels

        val origin = multiCityTripSearchModel.origin
        origin.clear()

        val originCity = multiCityTripSearchModel.originCity
        originCity.clear()

        val originAddress = multiCityTripSearchModel.originAddress
        originAddress.clear()

        val destination = multiCityTripSearchModel.destination
        destination.clear()

        val destinationCity = multiCityTripSearchModel.destinationCity
        destinationCity.clear()

        val destinationAddress = multiCityTripSearchModel.destinationAddress
        destinationAddress.clear()

        val depart = multiCityTripSearchModel.depart
        depart.clear()

        for (model in dataSet) {
            origin.add(model.origin)
            originCity.add(model.originCity!!)
            originAddress.add(model.originAddress!!)
            destination.add(model.destination)
            destinationCity.add(model.destinationCity!!)
            destinationAddress.add(model.destinationAddress!!)
            depart.add(model.departDate)
        }
    }

    fun onClickedTravelAdviceSearch() {
        _navigateToTravelAdvice.value = Event(true)
    }

    companion object {
        const val PLEASE_SELECT_ORIGIN_DESTINATION = "PLease select origin and destination airport."
        const val GOTO_TRAVELLER = "GOTO_TRAVELLER"
        const val PICK_DEPARTURE_DATE_REQUEST = "PICK_DEPARTURE_DATE_REQUEST"
        const val ARG_FLIGHT_LIST_DATA = "ARG_FLIGHT_LIST_DATA"
        const val GOTO_FLIGHT_LIST = "GOTO_FLIGHT_LIST"
    }
}
