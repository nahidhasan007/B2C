package net.sharetrip.visa.history.view.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.NetworkState
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.Flow
import net.sharetrip.visa.history.model.VisaHistoryItem

class VisaHistoryListViewModel(
    repository: VisaHistoryListRepo,
    var sharedPrefsHelper: SharedPrefsHelper
) : BaseViewModel() {

    val networkState = MutableLiveData<NetworkState>()
    var token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
    val historyLiveData: Flow<PagingData<VisaHistoryItem>> =
        repository.getVisaHistoryList(token, networkState).cachedIn(viewModelScope)

}
