package net.sharetrip.tour.history.information

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.shared.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.history.cancellationpolicy.TourCancellationPolicyFragment.Companion.ARG_TOUR_HISTORY_HTML_STRING
import net.sharetrip.tour.history.cancellationpolicy.TourCancellationPolicyFragment.Companion.ARG_TOUR_HISTORY_TITLE
import net.sharetrip.tour.model.TourHistoryItem

class TourInfoViewModel(private val historyItem: TourHistoryItem) : BaseViewModel() {

   private val _navigateToItinerary = MutableLiveData<Event<Bundle>>()
       val navigateToItinerary: LiveData<Event<Bundle>>
           get() = _navigateToItinerary

    fun onClickItinerary() {
        _navigateToItinerary.value = Event(bundleOf(ARG_TOUR_HISTORY_HTML_STRING to historyItem.booked_transfer.itinerary, ARG_TOUR_HISTORY_TITLE to "Itinerary"))
        //TODO:: screenSwitcher.open(TourHtmlScreen(historyItem.itinerary, "Itinerary"))
    }
}
