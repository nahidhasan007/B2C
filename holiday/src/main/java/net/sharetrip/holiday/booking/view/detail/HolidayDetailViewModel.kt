package net.sharetrip.holiday.booking.view.detail

import androidx.core.os.bundleOf
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.ResponseStatus
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.holiday.booking.model.HolidayDetailResponse
import net.sharetrip.holiday.booking.model.HolidayOffer
import net.sharetrip.holiday.booking.model.HolidayParam
import net.sharetrip.holiday.utils.ARG_HIGHLIGHT_DETAIL
import net.sharetrip.holiday.utils.ARG_HIGHLIGHT_TITLE

class HolidayDetailViewModel(
    private val productCode: String,
    isInternetAvailable: Boolean,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val holidayDetailsRepository: HolidayDetailsRepository
) : BaseViewModel() {
    //These LiveData get updates from holidayDetailsRepository directly and updates the UI
    /*N.B If you're using LiveData directly in layout. Make sure to assign the LifeCycleOwner to the ViewDataBinding.
    E.g bindingView.lifecycleOwner = this
    Otherwise the livedata updates will not be observed and the UI will not be updated*/
    val progressBar: LiveData<Boolean> = holidayDetailsRepository.dataLoading
    val holidayDetail: LiveData<HolidayDetailResponse> = holidayDetailsRepository.holidayDetails
    val status: LiveData<ResponseStatus> = holidayDetailsRepository.responseStatus
    val offers: LiveData<ArrayList<HolidayOffer>> = holidayDetailsRepository.holidayOffers
    val imageLink: LiveData<List<String>> = holidayDetailsRepository.imageLink
    //Live data linked to repo ends here

    val shareUrl = MutableLiveData<String>()
    val realPrice = ObservableField<String>()
    val discountAmount = ObservableField<String>()
    lateinit var holidayParam: HolidayParam
    lateinit var holidayOfferParam: HolidayOffer

    init {
        if (isInternetAvailable)
            fetchTourDetail()
        else
            holidayDetailsRepository.updateResponseStatus(ResponseStatus.INTERNET_ERROR)
    }

    private fun fetchTourDetail() {
        viewModelScope.launch {
            holidayDetailsRepository.getHolidayDetailsFromRemoteSource(productCode)
        }
    }

    //this function is currently not being used
    fun fetchShareUrl() { //todo
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
//        progressBar.set(true)
//        executeSuspendedCodeBlock(HolidayApiCallingKey.SHARE_RESPONSE.name) {
//            holidayBookingApiService.getShareResponse(token, "HOLIDAY", "OK")
//        }
    }

    fun setHolidayParam(position: Int) {
        holidayParam = HolidayParam(
            holidayId = holidayDetail.value?.id!!,
            offerId = holidayDetail.value?.offers!![position].id,
            currency = holidayDetail.value?.currencyCode!!,
            gatewayCurrency = holidayDetail.value?.gatewayCurrency!!,
            earnPoint = holidayDetail.value?.point?.earningPoint!!,
            sharedPoint = holidayDetail.value?.point?.sharedPoint!!,
            cancelPolicy = holidayDetail.value?.cxlPolicy!!,
            releaseTime = holidayDetail.value?.releaseTime!!,
            bankGatewayList = holidayDetail.value?.bankGatewayList!!,
            searchID = holidayDetail.value?.searchID!!
        )

        if (holidayDetail.value?.withAirfare.equals("YES")) {
            holidayParam.apply {
                withAirFare = true
                arrivalType = "Airlines"
                arrivalTransportName = holidayDetail.value?.arrivalTransportName!!
                arrivalTransportCode = holidayDetail.value?.arrivalTransportCode!!
                arrivalPickupTime = holidayDetail.value?.arrivalTime!!

                departureType = "Airlines"
                departureTransportName = holidayDetail.value?.departureTransportName!!
                departureTransportCode = holidayDetail.value?.departureTransportCode!!
                departureTime = holidayDetail.value?.departureTime!!
                pickupTime = holidayDetail.value?.departurePickupTime!!
            }
        }
    }

    fun onClickItinerary() {
        val bundle = bundleOf(
            ARG_HIGHLIGHT_TITLE to "Itinerary",
            ARG_HIGHLIGHT_DETAIL to holidayDetail.value?.itinerary
        )
        navigateWithArgument(HolidayDetailsDestinations.ITINERARY.name, bundle)
    }

    fun onClickPickupNote() {
        val bundle = bundleOf(
            ARG_HIGHLIGHT_TITLE to "Pickup Note",
            ARG_HIGHLIGHT_DETAIL to holidayDetail.value?.pickupNote
        )
        navigateWithArgument(HolidayDetailsDestinations.PICKUP_NOTE.name, bundle)
    }

    fun onClickSpecialNote() {
        val bundle = bundleOf(
            ARG_HIGHLIGHT_TITLE to "Special Notes",
            ARG_HIGHLIGHT_DETAIL to holidayDetail.value?.notes
        )
        navigateWithArgument(HolidayDetailsDestinations.SPECIAL_NOTE.name, bundle)
    }

    fun onClickTax() {
        val bundle = bundleOf(
            ARG_HIGHLIGHT_TITLE to "Tax",
            ARG_HIGHLIGHT_DETAIL to holidayDetail.value?.tax
        )
        navigateWithArgument(HolidayDetailsDestinations.TAX.name, bundle)
    }

    fun onClickGeneralCondition() {
        val bundle = bundleOf(
            ARG_HIGHLIGHT_TITLE to "General Conditions",
            ARG_HIGHLIGHT_DETAIL to holidayDetail.value?.generalCondition
        )
        navigateWithArgument(HolidayDetailsDestinations.GENERAL_CONDITION.name, bundle)
    }

    fun onClickGeneralCancellationPolicy() {
        val bundle = bundleOf(
            ARG_HIGHLIGHT_TITLE to "Cancellation Policy",
            ARG_HIGHLIGHT_DETAIL to holidayDetail.value?.cancellationPolicy
        )
        navigateWithArgument(HolidayDetailsDestinations.CANCELLATION_POLICY.name, bundle)
    }

    fun onIncludedService() {
        val bundle = bundleOf(
            ARG_HIGHLIGHT_TITLE to "Included Service",
            ARG_HIGHLIGHT_DETAIL to holidayDetail.value?.includedService
        )
        navigateWithArgument(HolidayDetailsDestinations.INCLUDED_SERVICE.name, bundle)
    }

    fun onExcludedService() {
        val bundle = bundleOf(
            ARG_HIGHLIGHT_TITLE to "Excluded Service",
            ARG_HIGHLIGHT_DETAIL to holidayDetail.value?.excludedService
        )
        navigateWithArgument(HolidayDetailsDestinations.EXCLUDED_SERVICE.name, bundle)
    }

    enum class HolidayDetailsDestinations { ITINERARY, PICKUP_NOTE, SPECIAL_NOTE, TAX, GENERAL_CONDITION, CANCELLATION_POLICY, INCLUDED_SERVICE, EXCLUDED_SERVICE }
}
