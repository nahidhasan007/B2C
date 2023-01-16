package net.sharetrip.hotel.history.view.historylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagingData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.Flow
import net.sharetrip.hotel.history.model.HotelHistoryItem

class HotelHistoryListViewModel(
    val sharedPrefsHelper: SharedPrefsHelper,
    val repository: HotelHistoryListRepo,
) : BaseViewModel() {
    val historyLiveData: Flow<PagingData<HotelHistoryItem>>

    val isFirstPage: LiveData<Boolean>
        get() = Transformations.switchMap(repository.hotelHistoryPagingSource) { obj: HotelHistoryPagingSource -> obj.isFirstPage }

    init {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        historyLiveData = repository.getFlightListFromRepository(token)
    }

}
