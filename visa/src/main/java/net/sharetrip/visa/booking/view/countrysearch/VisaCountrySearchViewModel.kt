package net.sharetrip.visa.booking.view.countrysearch

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.visa.booking.model.CountrySearchApiCallingKey
import net.sharetrip.visa.booking.model.VisaCountry
import net.sharetrip.visa.booking.view.countrysearch.VisaCountrySearchFragment.Companion.EXTRA_VISA_PROPERTY
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.SingleLiveEvent

class VisaCountrySearchViewModel(private val apiService: VisaBookingApiService) :
    BaseOperationalViewModel() {
    val countryList = MutableLiveData<List<VisaCountry>>()
    val navigateBack = SingleLiveEvent<Any>()
    val navigateBackWithData = SingleLiveEvent<Intent>()

    init {
        fetchPopularCountryList()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)

        when (operationTag) {
            CountrySearchApiCallingKey.PopularCountry.name -> {
                val response = (data.body as RestResponse<*>).response as List<VisaCountry>
                countryList.postValue(response)
            }
            CountrySearchApiCallingKey.VisaCountryList.name -> {
                val response = (data.body as RestResponse<*>).response as List<VisaCountry>
                countryList.postValue(response)
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    val actionExpandListener: MenuItem.OnActionExpandListener =
        object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                navigateBack.call()
                return false
            }
        }

    val onQueryTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.length >= 3) {
                    fetchVisaCountryList(newText)
                }
                return false
            }
        }

    private fun fetchPopularCountryList() {
        dataLoading.set(true)

        executeSuspendedCodeBlock(CountrySearchApiCallingKey.PopularCountry.name) {
            apiService.fetchVisaPopularCountry()
        }
    }

    private fun fetchVisaCountryList(cityKey: String) {
        dataLoading.set(true)

        executeSuspendedCodeBlock(CountrySearchApiCallingKey.VisaCountryList.name) {
            apiService.fetchVisaCountrySearch(
                cityKey
            )
        }
    }

    fun onClickCountryItem(position: Int) {
        val intent = Intent()
        intent.putExtra(EXTRA_VISA_PROPERTY, countryList.value!![position])
        navigateBackWithData.value = intent
    }
}
