package net.sharetrip.visa.booking.view.home

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.Flow
import net.sharetrip.visa.booking.model.VisaItem
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.SingleLiveEvent

class VisaHomeViewModel(
    sharedPrefsHelper: SharedPrefsHelper,
    apiService: VisaBookingApiService
) : BaseViewModel() {
    private val visaSearchQuery = VisaSearchQuery()
    var countryName = ObservableField<String>()
    val observableUserTripCoin = SingleLiveEvent<String>()
    val navigateToHomeExtended = SingleLiveEvent<VisaSearchQuery>()
    var repository: VisaListRepository =
        VisaListRepository(apiService, visaSearchQuery.nationality!!)

    var pagedListVisa: Flow<PagingData<VisaItem>> =
        repository.getVisaListFromRepository().cachedIn(viewModelScope)

    val isFirstPage: LiveData<Boolean>
        get() = Transformations.switchMap(repository.visaPagingSource) { obj: VisaPagingSource -> obj.isFirstPage }

    init {
        observableUserTripCoin.value = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
        countryName.set(visaSearchQuery.destinationCountry)
    }

    fun navigateToDetail(item: VisaItem) {
        visaSearchQuery.destinationCountry = item.countryName!!
        visaSearchQuery.visaCountryCode = item.visaCountryCode!!
        visaSearchQuery.visaPrepMinDays = item.visaPrepMinDays
        countryName.set(item.countryName)
        navigateToHomeExtended.value = visaSearchQuery
    }

    fun onDestinationFieldClicked() {
        navigateToHomeExtended.value = visaSearchQuery
    }
}
