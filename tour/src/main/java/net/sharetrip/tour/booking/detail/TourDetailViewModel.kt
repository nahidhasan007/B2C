package net.sharetrip.tour.booking.detail

import androidx.core.os.bundleOf
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.shared.model.ResponseStatus
import net.sharetrip.tour.booking.highlight.HighLightFragment.Companion.ARG_HIGHLIGHT_DETAIL_ARG
import net.sharetrip.tour.booking.highlight.HighLightFragment.Companion.ARG_HIGHLIGHT_TITLE_ARG
import net.sharetrip.tour.booking.reserve.TourReserveFragment.Companion.ARG_TOUR_OFFER_MODEL
import net.sharetrip.tour.booking.reserve.TourReserveFragment.Companion.ARG_TOUR_PARAM_MODEL
import net.sharetrip.tour.model.PeriodX
import net.sharetrip.tour.model.TourDetailResponseNew
import net.sharetrip.tour.model.TourParam

class TourDetailViewModel(
    private val code: String,
    isInternetAvailable: Boolean,
    private val repo: TourDetailRepo,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {

    private val _navigateToDestinations =
        MutableLiveData<Event<Pair<TourDetailNavDestinations, Any>>>()
    val navigateToDestinations: LiveData<Event<Pair<TourDetailNavDestinations, Any>>>
        get() = _navigateToDestinations

    val loading: ObservableBoolean
    val progressBar: ObservableBoolean
    val tourDetail: ObservableField<TourDetailResponseNew>
    val pickUpNotes = MutableLiveData<String>()
    val offers = MutableLiveData<List<PeriodX>>()
    val imageLink = MutableLiveData<ArrayList<String>>()
    val shareUrl = MutableLiveData<String>()
    val status: ObservableField<ResponseStatus>

    init {
        loading = ObservableBoolean(true)
        progressBar = ObservableBoolean(false)
        status = ObservableField()
        tourDetail = ObservableField()
        if (isInternetAvailable)
            fetchTourDetail()
        else
            status.set(ResponseStatus.INTERNET_ERROR)
    }

    private fun fetchTourDetail() {
        executeSuspendedCodeBlock(OperationTags.GET_TOUR_DETAIL.name) {
            repo.getTourDetail(code)
        }
    }

    fun fetchShareUrl() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(OperationTags.GET_SHARE_URL.name) {
            repo.getShareUrl(token, "TOUR", "OK")
        }
    }

    fun navigateToReserve(position: Int) {
        val tourParam = TourParam()
        tourParam.apply {
            tourId = tourDetail.get()?.id!!
            releaseTime = tourDetail.get()?.releaseTime!!
            countryCode = tourDetail.get()!!.countryCode
            bookingCurrency = tourDetail.get()!!.currency
            cityName = tourDetail.get()!!.cityName
            countryName = tourDetail.get()!!.countryName
            offerNo = position + 1
            earnCoin = tourDetail.get()?.points?.earning!!
            tourDuration = tourDetail.get()?.duration + ' ' + tourDetail.get()?.durationType
            cxlPolicy = tourDetail.get()?.cxlPolicy.toString()
        }
        _navigateToDestinations.value = Event(
            Pair(
                TourDetailNavDestinations.TO_RESERVE, bundleOf(
                    ARG_TOUR_OFFER_MODEL to tourDetail.get()?.periods!![position],
                    ARG_TOUR_PARAM_MODEL to tourParam
                )
            )
        )
    }

    fun onClickItinerary() {
        _navigateToDestinations.value = Event(
            Pair(
                TourDetailNavDestinations.TO_HIGHLIGHT, bundleOf(
                    ARG_HIGHLIGHT_TITLE_ARG to "Itinerary",
                    ARG_HIGHLIGHT_DETAIL_ARG to tourDetail.get()?.itinerary!!
                )
            )
        )
    }

    fun onClickPickupNote() {
        _navigateToDestinations.value = Event(
            Pair(
                TourDetailNavDestinations.TO_HIGHLIGHT, bundleOf(
                    ARG_HIGHLIGHT_TITLE_ARG to "Pickup Note",
                    ARG_HIGHLIGHT_DETAIL_ARG to tourDetail.get()?.pickupNotes!!
                )
            )
        )
    }

    fun onClickSpecialNote() {
        _navigateToDestinations.value = Event(
            Pair(
                TourDetailNavDestinations.TO_HIGHLIGHT, bundleOf(
                    ARG_HIGHLIGHT_TITLE_ARG to "Special Note",
                    ARG_HIGHLIGHT_DETAIL_ARG to tourDetail.get()?.notes!!
                )
            )
        )
    }

    fun onClickTax() {
        _navigateToDestinations.value = Event(
            Pair(
                TourDetailNavDestinations.TO_HIGHLIGHT, bundleOf(
                    ARG_HIGHLIGHT_TITLE_ARG to "Tax",
                    ARG_HIGHLIGHT_DETAIL_ARG to tourDetail.get()?.tax!!
                )
            )
        )
    }

    fun onClickGeneralCondition() {
        _navigateToDestinations.value = Event(
            Pair(
                TourDetailNavDestinations.TO_HIGHLIGHT, bundleOf(
                    ARG_HIGHLIGHT_TITLE_ARG to "General condition",
                    ARG_HIGHLIGHT_DETAIL_ARG to tourDetail.get()?.generalCondition!!
                )
            )
        )
    }

    fun onClickCancellationPolicy() {
        _navigateToDestinations.value = Event(
            Pair(
                TourDetailNavDestinations.TO_HIGHLIGHT, bundleOf(
                    ARG_HIGHLIGHT_TITLE_ARG to "Cancellation Policy",
                    ARG_HIGHLIGHT_DETAIL_ARG to tourDetail.get()?.cancellationPolicy!!
                )
            )
        )
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            OperationTags.GET_TOUR_DETAIL.name -> {
                val tourResponse = (data.body as RestResponse<*>).response as TourDetailResponseNew
                tourDetail.set(tourResponse)
                offers.postValue(tourResponse.periods
                    .filter {
                        it.child3To6 != 0 || it.child7To12 != 0 || it.infant0To2 != 0 ||
                                it.max10Pax != 0 || it.perPersonPax != 0 || it.price2Pax != 0 ||
                                it.price3Pax != 0 || it.price4Pax != 0 || it.price5Pax != 0 ||
                                it.price6Pax != 0 || it.price7Pax != 0 || it.price8Pax != 0 ||
                                it.price9Pax != 0 || it.price10Pax != 0
                    })
                pickUpNotes.value = tourResponse.pickupNotes!!
                loading.set(false)
                status.set(ResponseStatus.SUCCESS)
                if (tourResponse.images!!.isNotEmpty())
                    imageLink.postValue(tourResponse.images?.map { it.srcLarge } as java.util.ArrayList<String>?)
            }
            OperationTags.GET_SHARE_URL.name -> {
                progressBar.set(false)
                val shareResponse = data.body as RestResponse<String>
                shareUrl.value = shareResponse.response!!
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            OperationTags.GET_TOUR_DETAIL.name -> {
                loading.set(false)
                status.set(if (type == ErrorType.API_ERROR) ResponseStatus.SERVER_ERROR else ResponseStatus.INTERNET_ERROR)
            }
            OperationTags.GET_SHARE_URL.name -> {
                progressBar.set(false)
            }
        }
    }

    enum class OperationTags { GET_TOUR_DETAIL, GET_SHARE_URL }
    enum class TourDetailNavDestinations { TO_RESERVE, TO_HIGHLIGHT }
}
