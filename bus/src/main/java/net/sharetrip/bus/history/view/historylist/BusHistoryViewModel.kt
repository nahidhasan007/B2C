package net.sharetrip.bus.history.view.historylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagingData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import kotlinx.coroutines.flow.Flow
import net.sharetrip.bus.history.model.HistoryDetails
import net.sharetrip.bus.network.BusHistoryApiService
import net.sharetrip.bus.utils.SingleLiveEvent

class BusHistoryViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: BusHistoryListRepo,
    private val apiService: BusHistoryApiService
) : BaseOperationalViewModel() {
    val historyLiveData: Flow<PagingData<HistoryDetails>>
    val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
    val navigateToHistoryDetails = SingleLiveEvent<HistoryDetails>()
    val observableUserTripCoin = SingleLiveEvent<String>()

    val isFirstPage: LiveData<Boolean>
        get() = Transformations.switchMap(repository.hotelHistoryPagingSource) { obj: BusHistoryPagingSource -> obj.isFirstPage }

    init {
        getUserTripCoin()
        historyLiveData = repository.getFlightListFromRepository(token)
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        val response = (data.body as RestResponse<*>).response as HistoryDetails
        navigateToHistoryDetails.value = response
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    private fun getUserTripCoin() {
        var points = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }
        if (points.isBlank()) {
            points = "0"
            sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
        }
        observableUserTripCoin.value = points
    }

    fun getHistoryDetails(bookingId: String) {
        dataLoading.set(true)

        executeSuspendedCodeBlock {
            apiService.getBookingHistoryDetails(
                bookingId,
                token
            )
        }
    }
}
