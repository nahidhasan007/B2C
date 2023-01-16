package net.sharetrip.profile.view.faq

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_COMMON
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_FLIGHT
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_HOLIDAY
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_HOTEL
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_TOUR
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_TRANSFER
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_TRAVEL_TRIVIA
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_TRIP_COIN

class FaqViewModel(): BaseViewModel() {

    private val _navigateToContentScreen = MutableLiveData<Event<Int>>()
    val navigateToContentScreen: LiveData<Event<Int>>
    get() = _navigateToContentScreen

    fun onclickCommonOverViewFaq() {
        _navigateToContentScreen.value = Event(ARG_FAQ_COMMON)
    }

    fun onclickFlightFaq() {
        _navigateToContentScreen.value = Event(ARG_FAQ_FLIGHT)
    }

    fun onclickHotelFaq() {
        _navigateToContentScreen.value = Event(ARG_FAQ_HOTEL)
    }

    fun onclickHolidayFaq() {
        _navigateToContentScreen.value = Event(ARG_FAQ_HOLIDAY)
    }

    fun onclickTripCoinFaq() {
        _navigateToContentScreen.value = Event(ARG_FAQ_TRIP_COIN)
    }

    fun onclickTravelTriviaFaq() {
        _navigateToContentScreen.value = Event(ARG_FAQ_TRAVEL_TRIVIA)
    }

    fun onclickTourFaq() {
        _navigateToContentScreen.value = Event(ARG_FAQ_TOUR)
    }

    fun onclickTransferFaq() {
        _navigateToContentScreen.value = Event(ARG_FAQ_TRANSFER)
    }
}
