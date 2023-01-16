package net.sharetrip.holiday.history.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.NetworkState
import com.sharetrip.base.viewmodel.BaseViewModel

class HolidayHistoryViewModel(
    sharedPrefsHelper: SharedPrefsHelper,
    private val repository: HolidayHistoryListRepo
) : BaseViewModel() {

    private val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

    val historyPagingData = repository.getHistoryListData(token)
    val networkState: LiveData<NetworkState>
    get() = Transformations.switchMap(repository.holidayHistoryPagingSource){ it.networkState }

}
