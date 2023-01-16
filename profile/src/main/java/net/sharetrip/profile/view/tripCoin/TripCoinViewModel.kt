package net.sharetrip.profile.view.tripCoin

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.profile.model.TripCoinItem
import java.text.NumberFormat
import java.util.*

class TripCoinViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: TripCoinRepository
) : BaseViewModel() {

    private val numberFormatter: NumberFormat = NumberFormat.getNumberInstance(Locale.US)

    val showToast = repository.showMessage

    val tripCoinItems: LiveData<ArrayList<TripCoinItem>> =
        Transformations.map(repository.tripCoinSummaryResponse) {
            it.allPoints as ArrayList<TripCoinItem>
        }

    val availableTripCoin: LiveData<String> =
        Transformations.map(repository.tripCoinSummaryResponse) {
            numberFormatter.format(it.availableCoins)
        }

    val pendingTripCoin: LiveData<String> =
        Transformations.map(repository.tripCoinSummaryResponse) {
            numberFormatter.format(it.pendingCoin)
        }

    val expiringTripCoin: LiveData<String> =
        Transformations.map(repository.tripCoinSummaryResponse) {
            numberFormatter.format(it.expiringSixtyDays)
        }

    val isDataLoading = repository.isDataLoading

    init {
        fetchTripCoinsData()
    }

    private fun fetchTripCoinsData() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getTripCoinData(token)
        }
    }

}
