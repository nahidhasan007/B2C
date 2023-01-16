package net.sharetrip.tracker.view.cirium.view.search

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.model.ServiceType
import com.sharetrip.base.network.model.Status
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.tracker.view.cirium.model.CarrierInfo
import net.sharetrip.tracker.view.cirium.model.FlightTrackingDataHolder
import org.threeten.bp.LocalDate
import java.util.*

class FlightTrackingSearchViewModel(private val repository: FlightTrackingSearchRepo) :
    BaseViewModel() {

    private var departureDate: String = String()

    private val _navigateToFlightList = MutableLiveData<Event<Boolean>>()
    val navigateToFlightList: LiveData<Event<Boolean>>
        get() = _navigateToFlightList

    private val _navigateToFlightDetails = MutableLiveData<Event<Boolean>>()
    val navigateToFlightDetails: LiveData<Event<Boolean>>
        get() = _navigateToFlightDetails

    private val _navigateToCalender = MutableLiveData<Event<CalenderData>>()
    val navigateToCalender: LiveData<Event<CalenderData>>
        get() = _navigateToCalender

    private val _hideKeyBoard = MutableLiveData<Boolean>()
    val hideKeyBoard: LiveData<Boolean>
        get() = _hideKeyBoard

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    var trackingDataHolder = FlightTrackingDataHolder()
    var isDataLoading = repository.isDataLoading
    val flightNumber = ObservableField<String>()
    var dateOfDepartureText = ObservableField<String>()
    private val profileEventManager =
        AnalyticsProvider.profileEventManager(AnalyticsProvider.getFirebaseAnalytics())

    init {
        departureDate =
            DateUtil.parseApiDateFormatFromMillisecond(Calendar.getInstance().timeInMillis)
        dateOfDepartureText.set(
            DateUtil.parseDisplayDateFormatFromApiDateFormat(
                LocalDate.now().toString()
            )
        )
    }

    fun fetchFlightStatusInfo() {
        _hideKeyBoard.value = true

        val data = flightNumber.get()?.uppercase()
        if ((data?.length ?: 0) < 4) {
            _message.value = "Check your flight number"
            return
        }

        profileEventManager.clickOnFlightTracker()

        val carrierCode = data!!.substring(0, 2)
        val carrierNumber = data.substring(2, data.length)

        if (departureDate.isEmpty()) {
            _message.value = "Select Departure Date"
            return
        }

        val year = departureDate.substring(0, 4)
        val month = departureDate.substring(5, 7)
        val day = departureDate.substring(8, 10)

        viewModelScope.launch {
            repository.getFlightStatusData(
                carrierCode,
                carrierNumber,
                year,
                month,
                day
            )

            if (repository.apiStatus.value == Status.SUCCESS) {
                if (repository.flightStatusModel.value?.flightStatuses?.isNotEmpty() == true) {
                    trackingDataHolder.airports =
                        repository.flightStatusModel.value?.appendix?.airports ?: ArrayList()
                    trackingDataHolder.airlines =
                        repository.flightStatusModel.value?.appendix?.airlines ?: ArrayList()
                    trackingDataHolder.carrierInfo = CarrierInfo(carrierCode, carrierNumber)

                    repository.flightStatusModel.value?.flightStatuses?.let {
                        if (it.size > 1) {
                            trackingDataHolder.flightStatusDataList = it
                            _navigateToFlightList.value = Event(true)
                        } else {
                            trackingDataHolder.flightStatusData = it[0]
                            _navigateToFlightDetails.value = Event(true)
                        }
                    }

                } else {
                    _message.value = "No data found"
                }
            }
        }
    }

    fun selectDateOfFlight() {
        _hideKeyBoard.value = true
        val startDateInMillisecond = DateUtil.parseDateToMillisecond(DateUtil.todayApiDateFormat)
        val dateHintText = "Departure Date"
        val calendarData = CalenderData(
            mDateInMillisecond = startDateInMillisecond,
            mDateHintText = dateHintText,
            serviceType = ServiceType.TRACKER.serviceName
        )

        _navigateToCalender.value = Event(calendarData)
    }

    fun setDepartureDate(date: Long) {
        departureDate = DateUtil.parseApiDateFormatFromMillisecond(date)
        dateOfDepartureText.set(DateUtil.parseDisplayDateFormatFromApiDateFormat(departureDate))
    }

}
