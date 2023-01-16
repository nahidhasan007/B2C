package net.sharetrip.tracker.view.search

import android.app.Activity
import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.Status
import com.sharetrip.base.utils.ShareTripAppConstants.TRAVEL_ADVICE_API
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.tracker.utils.PLEASE_SELECT_A_COUNTRY
import net.sharetrip.tracker.utils.SELECT_DESTINATION
import net.sharetrip.tracker.view.search.TravelAdviceSearchFragment.Companion.EXTRA_COUNTRY_CODE
import net.sharetrip.tracker.view.search.TravelAdviceSearchFragment.Companion.EXTRA_COUNTRY_NAME

class TravelAdviceSearchViewModel(private val repository: TravelAdviceSearchRepo) :
    BaseViewModel() {

    val navigateToCountryCurrency = MutableLiveData<Event<Boolean>>()
    val countryName = ObservableField<String>()
    val countryCode = ObservableField<String>()
    val travelInfoResponse = repository.travelAdvice
    val isDataLoading = repository.isDataLoading
    val isTermsConditionVisible = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> = Transformations.map(repository.apiStatus) {
        it == Status.SUCCESS
    }
    val isRestrictionDetailsExpanded = ObservableBoolean(false)
    val isRecommendationArrowExpanded = ObservableBoolean(true)
    val isCovidGuideAvailable = ObservableBoolean(true)
    val showToast = repository.showMessage

    var restrictionDetailsList = MutableLiveData<ArrayList<String>>()
    var travelInfo = repository.travelAdvice

    init {
        countryName.set(SELECT_DESTINATION)
    }

    fun onClickedDestinationCountry() {
        navigateToCountryCurrency.value = Event(true)
    }

    fun onClickedSearchFlightButton() {
        if (countryName.get()!!.isEmpty() ||
            countryName.get()!! == SELECT_DESTINATION
        ) {
            showToast.value = PLEASE_SELECT_A_COUNTRY
        } else {
            getTravelInfo()
        }
    }

    fun onClickTravelAdvisoryLevel() {
        isTermsConditionVisible.value = true
    }

    fun onRestrictionArrowClicked() {
        isRestrictionDetailsExpanded.set(!isRestrictionDetailsExpanded.get())
    }

    fun onRecommendationArrowClicked() {
        isRecommendationArrowExpanded.set(!isRecommendationArrowExpanded.get())
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, mData: Intent?) {
        if (Activity.RESULT_OK != resultCode)
            return

        when (requestCode) {
            PICK_COUNTRY -> handleFromData(mData!!)
        }
    }

    fun handleFromData(data: Intent) {
        val code = data.getStringExtra(EXTRA_COUNTRY_CODE)
        val name = data.getStringExtra(EXTRA_COUNTRY_NAME)

        if (name != null && code != null) {
            countryName.set(name)
            countryCode.set(code)
        } else {
            countryName.set(SELECT_DESTINATION)
        }
    }

    private fun getTravelInfo() {
        viewModelScope.launch {
            repository.fetchTravelInfo(TRAVEL_ADVICE_API, countryCode.get().toString())
        }
    }

    companion object {
        const val PICK_COUNTRY = 210
    }

}
