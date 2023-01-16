package net.sharetrip.tour.history.detail

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.tour.history.cancellationpolicy.TourCancellationPolicyFragment.Companion.ARG_TOUR_HISTORY_HTML_STRING
import net.sharetrip.tour.history.cancellationpolicy.TourCancellationPolicyFragment.Companion.ARG_TOUR_HISTORY_TITLE
import net.sharetrip.tour.history.contact.TourContactInfoFragment.Companion.ARG_TOUR_BOOKING_CONTACT_INFO_PRIMARY
import net.sharetrip.tour.history.information.TourInfoFragment.Companion.ARG_TOUR_HISTORY_ITEM
import net.sharetrip.tour.model.TourHistoryItem

class TourBookingDetailViewModel(
    val bookingCode: String,
    private val repo: TourBookingDetailRepo,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {

    private val _historyItem = MutableLiveData<TourHistoryItem>()
        val historyItem: LiveData<TourHistoryItem>
            get() = _historyItem

    private val _navigateToDestinations =
        MutableLiveData<Event<Pair<TourHistoryDetailNavDestinations, Bundle?>>>()
    val navigateToDestinations: LiveData<Event<Pair<TourHistoryDetailNavDestinations, Bundle?>>>
        get() = _navigateToDestinations

    init {
        getTourBookingDetails()
    }

    fun getTourBookingDetails() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock {
            repo.getTourBookingDetail(token, bookingCode)
        }
    }

    fun onClickContactInformation() {
        _navigateToDestinations.value = Event(
            Pair(
                TourHistoryDetailNavDestinations.TO_CONTACT,
                bundleOf(ARG_TOUR_BOOKING_CONTACT_INFO_PRIMARY to historyItem.value?.primaryContact)
            )
        )
    }

    fun onClickTourInformation() {
        _navigateToDestinations.value = Event(Pair(TourHistoryDetailNavDestinations.TO_INFO, bundleOf(ARG_TOUR_HISTORY_ITEM to historyItem.value)))
    }

    fun onClickCancellationPolicy() {
        _navigateToDestinations.value = Event(
            Pair(
                TourHistoryDetailNavDestinations.TO_CANCELLATION_POLICY,
                bundleOf(
                    ARG_TOUR_HISTORY_HTML_STRING to historyItem.value?.booked_transfer?.cancellationPolicy,
                    ARG_TOUR_HISTORY_TITLE to "Cancellation Policy"
                )
            )
        ) }

    fun onClickPricingInfo() {
        _navigateToDestinations.value = Event(Pair(TourHistoryDetailNavDestinations.TO_PRICING, bundleOf(ARG_TOUR_HISTORY_ITEM to historyItem.value)))
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        val tourHistoryItemData = (data.body as RestResponse<*>).response as TourHistoryItem
        _historyItem.value = tourHistoryItemData
    }

    enum class TourHistoryDetailNavDestinations { TO_CONTACT, TO_INFO, TO_CANCELLATION_POLICY, TO_PRICING }
}
