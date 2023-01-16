package net.sharetrip.visa.booking.view.checkout

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.booking.model.VisaItemTraveler
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.utils.MsgUtils.CHECK_ALL_INFORMATION
import net.sharetrip.visa.utils.SingleLiveEvent

class VisaCheckoutViewModel(private val visaSearchQuery: VisaSearchQuery) : BaseViewModel() {
    var travellerList = MutableLiveData<MutableList<VisaItemTraveler>>()
    val navigateToTravellerInfo = SingleLiveEvent<VisaSearchQuery>()
    val navigateToBookingSummary = SingleLiveEvent<VisaSearchQuery>()

    init {
        prepareList()
    }

    private fun prepareList() {
        travellerList.value?.clear()

        if (visaSearchQuery.travellers.size != visaSearchQuery.travellerCount) {
            for (i in 1..(visaSearchQuery.travellerCount)) {
                val visaTraveller = VisaItemTraveler()
                visaSearchQuery.travellers.add(visaTraveller)
            }
        }

        updateTravellers()
    }

    fun updateTravellers() {
        travellerList.value = visaSearchQuery.travellers
    }

    fun onTravellerInfoUpdate(position: Int) {
        visaSearchQuery.currentTravellerPosition = position
        navigateToTravellerInfo.value = visaSearchQuery
    }

    fun makeJson(value: MutableList<VisaItemTraveler>): String {
        val gson = Gson().newBuilder().setPrettyPrinting().create()
        val data = gson.toJson(value)
        println("Traveler :> $data")
        return data
    }

    fun onNext() {
        if (checkTravelerData()) {
            navigateToBookingSummary.value = visaSearchQuery
        } else {
             showMessage(CHECK_ALL_INFORMATION)
        }
    }

    private fun checkTravelerData(): Boolean {
        visaSearchQuery.travellers.forEach {
            if (it.givenName!!.isEmpty())
                return false
        }
        return true
    }

    fun onDestroy() {
        travellerList.value?.clear()
        visaSearchQuery.travellers.clear()
    }
}
