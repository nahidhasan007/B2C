package net.sharetrip.tour.history.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.model.TourHistoryItem

class TourHistoryListViewModel(
    sharedPrefsHelper: SharedPrefsHelper,
    repo: TourHistoryListRepo
) : BaseViewModel() {

    private val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

    val historyLiveData: LiveData<PagingData<TourHistoryItem>> =
        repo.getTourList(token).cachedIn(viewModelScope)

    init {
        dataLoading.set(false)
    }

}
