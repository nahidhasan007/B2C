package net.sharetrip.profile.view.quickpick

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class QuickPickViewModel(
    private val repository: QuickPickRepository,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseViewModel() {

    private val _navigateToAddTraveler = MutableLiveData<Event<Boolean>>()
    val navigateToAddTraveler: LiveData<Event<Boolean>>
        get() = _navigateToAddTraveler

    val passengerList = repository.passengerList
    val isDataLoading = repository.isDataLoading

    init {
        fetchProfileInfo()
    }

    fun fetchProfileInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        viewModelScope.launch {
            repository.getProfileInfo(token)
        }
    }

    fun onClickAddPerson() {
        _navigateToAddTraveler.value = Event((true))
    }
}
