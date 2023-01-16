package net.sharetrip.flight.booking.view.travellerdetails

import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.ItemTraveler
import net.sharetrip.flight.booking.model.PassengerType
import net.sharetrip.flight.booking.view.travellerdetails.adapter.TravelerAddAdapter
import net.sharetrip.flight.utils.MsgUtils.PLEASE_SELECT_ALL_TRAVELLER_INFO
import net.sharetrip.shared.model.PassengerTypeTitle

class TravelerDetailsViewModel(
    val flightSearch: FlightSearch,
    private val travelerAddAdapter: TravelerAddAdapter
) : BaseViewModel() {

    val onContinueClicked = MutableLiveData<Event<Boolean>>()
    val onAddTravelerDetailsClicked = MutableLiveData<Event<Int>>()

    init {
        initTravelerAddAdapter(flightSearch)
    }

    lateinit var passengerList: ArrayList<ItemTraveler>

    fun onContinueButtonClicked() {
        travelerAddAdapter.dataSet.forEachIndexed { index, itemTraveler ->
            if (!itemTraveler.isValidData) {
                showMessage(PLEASE_SELECT_ALL_TRAVELLER_INFO)
                return
            } else {
                if (flightSearch.travellerBaggageCodes.isNotEmpty()) {
                    itemTraveler.luggageCode =
                        flightSearch.travellerBaggageCodes[index].luggageCodeList
                }
            }
        }
        onContinueClicked.value = Event(true)
    }

    private fun initTravelerAddAdapter(flightSearch: FlightSearch) {
        val itemTravelers: MutableList<ItemTraveler> = ArrayList()
        val mNumberOfAdult = flightSearch.adult
        for (i in 1..mNumberOfAdult) {
            val givenName = if (i == 1) {
                "${PassengerTypeTitle.Adult.name} $i (Primary)"
            } else {
                "${PassengerTypeTitle.Adult.name} $i"
            }
            @PassengerType val mType =
                if (i == 1) PassengerType.PRIMARY_ADULT else PassengerType.SECONDARY_ADULT
            val traveler = ItemTraveler(mType, givenName)
            itemTravelers.add(traveler)
        }
        val numberOfChild = flightSearch.child
        for (i in 1..numberOfChild) {
            val givenName = "${PassengerTypeTitle.Child.name} $i"
            val traveler = ItemTraveler(PassengerType.CHILDREN, givenName)
            itemTravelers.add(traveler)
        }
        val numberOfInfants = flightSearch.infant
        for (i in 1..numberOfInfants) {
            val givenName = "${PassengerTypeTitle.Infant.name} $i"
            val traveler = ItemTraveler(PassengerType.INFANT, givenName)
            itemTravelers.add(traveler)
        }
        travelerAddAdapter.resetItems(itemTravelers)
    }

    fun updatePassengerList(passengersList: ArrayList<ItemTraveler>) {
        passengerList = passengersList
    }
}