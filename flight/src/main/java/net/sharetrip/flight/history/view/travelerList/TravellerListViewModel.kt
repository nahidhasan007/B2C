package net.sharetrip.flight.history.view.travelerList

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.history.model.FlightHistoryResponse

class TravellerListViewModel(historyResponse: FlightHistoryResponse) : BaseViewModel() {

    val phoneNumber = ObservableField<String>()
    val emailId = ObservableField<String>()
    val gotoImagePreview = MutableLiveData<Event<String>>()
    lateinit var imageTag: String

    init {
        if (historyResponse.travellers.isNotEmpty()) {
            phoneNumber.set(historyResponse.travellers[0].mobileNumber)
            emailId.set(historyResponse.travellers[0].email)
        }
    }

    fun showImage(url: String, tag: String) {
        imageTag = tag
        gotoImagePreview.value = Event(url)
    }
}
