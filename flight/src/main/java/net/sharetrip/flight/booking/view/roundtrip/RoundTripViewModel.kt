package net.sharetrip.flight.booking.view.roundtrip

import android.content.Intent
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.booking.model.ChildrenDOB
import net.sharetrip.flight.booking.model.FlightPromotion
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.TravellersInfo
import net.sharetrip.flight.booking.view.searchairport.SearchAirportFragment
import net.sharetrip.flight.network.DataManager
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import timber.log.Timber
import java.text.ParseException
import java.util.*

class RoundTripViewModel : BaseOperationalViewModel() {
    private val _navigateToTravelAdvice = MutableLiveData<Event<Boolean>>()
    private var oneWayTripSearchModel: FlightSearch = FlightSearch()
    private val flightEventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())

    val navigateToTravelAdvice: LiveData<Event<Boolean>>
        get() = _navigateToTravelAdvice

    lateinit var travellers: TravellersInfo
    var roundTripSearchModel: FlightSearch = FlightSearch()
    val fromAirPortCode = ObservableField<String>()
    val toAirPortCode = ObservableField<String>()
    val roundDepartureDate = ObservableField<String>()
    val travelersAndClassCount = ObservableField<String>()
    val promotionalImage = ObservableField("")
    val onSearchClicked = MutableLiveData<Event<Boolean>>()

    val startDateInMillisecond: Long
        get() {
            val mStartDateInMillisecond: Long = try {
                val mStartDateString = roundTripSearchModel.depart[0]
                DateUtil.parseDateToMillisecond(mStartDateString)
            } catch (e: ParseException) {
                Calendar.getInstance().timeInMillis
            }
            return mStartDateInMillisecond
        }

    val endDateInMillisecond: Long
        get() {
            val mEndDateInMillisecond = try {
                val mEndDateString = roundTripSearchModel.depart[1]
                DateUtil.parseDateToMillisecond(mEndDateString)
            } catch (e: ParseException) {
                DateUtil.dayAfterTomorrowDateInMillisecond
            }

            return mEndDateInMillisecond
        }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        try {
            val result = (data.body as RestResponse<*>).response as FlightPromotion
            if (result.image.isNotBlank())
                promotionalImage.set(result.image)
        } catch (e: Exception) {
            showMessage("Something Went Wrong.")
        }
    }

    init {
        initUi()
    }

    private fun initUi() {
        roundTripSearchModel.initForRoundTrip()
        oneWayTripSearchModel.initForOneWay()

        fromAirPortCode.set(roundTripSearchModel.origin[0])
        toAirPortCode.set(roundTripSearchModel.destination[0])

        try {
            var date = roundTripSearchModel.depart[0]
            val toDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(date)
            date = roundTripSearchModel.depart[1]
            val fromDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(date)
            roundDepartureDate.set("$toDate - $fromDate")
            travellers = TravellersInfo(
                1, 0, 0, roundTripSearchModel.classType, roundTripSearchModel.depart[0],
                arrayListOf()
            )
        } catch (e: ParseException) {
            Timber.d("ParseException %s", e.message)
        }

        travelersAndClassCount.set("${roundTripSearchModel.adult + roundTripSearchModel.child + roundTripSearchModel.infant} Traveller(s) - ${roundTripSearchModel.classType}")
        getFlightPromotion()
    }

    private fun updateFromAddress() {
        val mCode = roundTripSearchModel.origin[0]
        fromAirPortCode.set(mCode)
    }

    private fun updateDate() {
        try {
            var mDate = roundTripSearchModel.depart[0]
            val mToDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(mDate)

            mDate = roundTripSearchModel.depart[1]
            val mFromDate = DateUtil.parseDisplayDateFormatFromApiDateFormat(mDate)

            roundDepartureDate.set("$mToDate - $mFromDate")
        } catch (e: ParseException) {
            Timber.d("ParseException %s", e.message)
        }
    }

    private fun updateToAddress() {
        val mCode = roundTripSearchModel.destination[0]
        toAirPortCode.set(mCode)
    }

    fun onSearchFlightButtonClicked() {
        flightEventManager.searchReturnFlight()
        onSearchClicked.value = Event(true)
    }

    fun onClickedTravelAdviceSearch() {
        _navigateToTravelAdvice.value = Event(true)
    }

    fun onCLickSwapAirport() {
        val codeFirst = roundTripSearchModel.origin[0]
        val codeSecond = roundTripSearchModel.destination[0]

        val mOneWayTripOrigin = ArrayList<String>()
        mOneWayTripOrigin.add(codeSecond)

        roundTripSearchModel.origin.clear()
        roundTripSearchModel.origin = mOneWayTripOrigin

        val mOneWayTripDestination = ArrayList<String>()
        mOneWayTripDestination.add(codeFirst)

        roundTripSearchModel.destination.clear()
        roundTripSearchModel.destination = mOneWayTripDestination

        fromAirPortCode.set(codeSecond)
        toAirPortCode.set(codeFirst)
    }

    fun handleFromAddress(mData: Intent) {
        val mCode = mData.getStringExtra(SearchAirportFragment.ARG_AIRPORT_CODE)
        val mCity = mData.getStringExtra(SearchAirportFragment.ARG_AIRPORT_CITY)
        val mAddress = mData.getStringExtra(SearchAirportFragment.ARG_AIRPORT_ADDRESS)

        val mRoundTripOrigin = roundTripSearchModel.origin
        mRoundTripOrigin.clear()
        mRoundTripOrigin.add(mCode!!)

        val mRoundTripOriginCity = roundTripSearchModel.originCity
        mRoundTripOriginCity.clear()
        mRoundTripOriginCity.add(mCity!!)

        val mRoundTripOriginAddress = roundTripSearchModel.originAddress
        mRoundTripOriginAddress.clear()
        mRoundTripOriginAddress.add(mAddress!!)

        updateFromAddress()

        val mOneWayTripOrigin = oneWayTripSearchModel.origin
        mOneWayTripOrigin.clear()
        mOneWayTripOrigin.add(mCode)

        val mOneWayTripOriginCity = oneWayTripSearchModel.originCity
        mOneWayTripOriginCity.clear()
        mOneWayTripOriginCity.add(mCity)

        val mOneWayTripOriginAddress = oneWayTripSearchModel.originAddress
        mOneWayTripOriginAddress.clear()
        mOneWayTripOriginAddress.add(mAddress)
    }

    fun handleToAddress(mData: Intent) {
        val mCode = mData.getStringExtra(SearchAirportFragment.ARG_AIRPORT_CODE)
        val mCity = mData.getStringExtra(SearchAirportFragment.ARG_AIRPORT_CITY)
        val mAddress = mData.getStringExtra(SearchAirportFragment.ARG_AIRPORT_ADDRESS)

        val mRoundTripDestination = roundTripSearchModel.destination
        mRoundTripDestination.clear()
        mRoundTripDestination.add(mCode!!)

        val mRoundTripDestinationCity = roundTripSearchModel.destinationCity
        mRoundTripDestinationCity.clear()
        mRoundTripDestinationCity.add(mCity!!)

        val mmRoundTripDestinationAddress = roundTripSearchModel.destinationAddress
        mmRoundTripDestinationAddress.clear()
        mmRoundTripDestinationAddress.add(mAddress!!)

        updateToAddress()

        val mOneWayTripDestination = oneWayTripSearchModel.destination
        mOneWayTripDestination.clear()
        mOneWayTripDestination.add(mCode)

        val mOneWayDestinationCity = oneWayTripSearchModel.destinationCity
        mOneWayDestinationCity.clear()
        mOneWayDestinationCity.add(mCity)

        val mOneWayTripDestinationAddress = oneWayTripSearchModel.destinationAddress
        mOneWayTripDestinationAddress.clear()
        mOneWayTripDestinationAddress.add(mAddress)

        updateFromAddress()
    }

    fun handleDepartureAndReturnDate(data: Intent) {
        val startDateInMillisecond = data.getLongExtra(
            EXTRA_START_DATE_IN_MILLISECOND,
            Calendar.getInstance().timeInMillis
        )
        val endDateInMillisecond = data.getLongExtra(
            EXTRA_END_DATE_IN_MILLISECOND,
            Calendar.getInstance().timeInMillis
        )

        val mStartDate = DateUtil.parseApiDateFormatFromMillisecond(startDateInMillisecond)
        val mEndDate = DateUtil.parseApiDateFormatFromMillisecond(endDateInMillisecond)
        val mRoundTripDepart = roundTripSearchModel.depart

        mRoundTripDepart.clear()
        mRoundTripDepart.add(mStartDate)
        mRoundTripDepart.add(mEndDate)

        updateDate()

        travellers.tripDate = roundTripSearchModel.depart[0]

        val mOneWayTripDepart = oneWayTripSearchModel.depart
        mOneWayTripDepart.clear()
        mOneWayTripDepart.add(mStartDate)
    }

    fun handleTravellerData(data: Intent) {
        roundTripSearchModel.adult = data.getIntExtra(EXTRA_NUMBER_OF_ADULT, 1)
        roundTripSearchModel.child = data.getIntExtra(EXTRA_NUMBER_OF_CHILDREN, 0)
        roundTripSearchModel.infant = data.getIntExtra(EXTRA_NUMBER_OF_INFANT, 0)
        roundTripSearchModel.classType = data.getStringExtra(EXTRA_TRIP_CLASS_TYPE).toString()

        if (roundTripSearchModel.child > 0) {
            try {
                roundTripSearchModel.childDateOfBirthList =
                    (data.getParcelableArrayListExtra<ChildrenDOB>(EXTRA_CHILD_DOB_LIST) as ArrayList<ChildrenDOB>)
            } catch (e: Exception) {
            }
        }

        travelersAndClassCount.set("${roundTripSearchModel.adult + roundTripSearchModel.child + roundTripSearchModel.infant} Traveller(s) - ${roundTripSearchModel.classType}")
        travellers = TravellersInfo(
            roundTripSearchModel.adult,
            roundTripSearchModel.child,
            roundTripSearchModel.infant,
            roundTripSearchModel.classType,
            roundTripSearchModel.depart[0],
            roundTripSearchModel.childDateOfBirthList
        )
    }

    private fun getFlightPromotion() {
        executeSuspendedCodeBlock("") {
            DataManager.flightApiService.getFlightPromotionBanner()
        }
    }
}
