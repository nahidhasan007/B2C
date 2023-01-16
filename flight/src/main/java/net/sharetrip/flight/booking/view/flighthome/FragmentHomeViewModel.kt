package net.sharetrip.flight.booking.view.flighthome

import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel

class FragmentHomeViewModel : BaseViewModel() {
    val selectedTab = MutableLiveData(Event(1))
}