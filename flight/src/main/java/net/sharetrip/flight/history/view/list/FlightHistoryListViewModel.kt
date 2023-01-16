package net.sharetrip.flight.history.view.list

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sharetrip.base.viewmodel.BaseViewModel

class FlightHistoryListViewModel(
    private val token: String,
    private val repo: FlightHistoryListRepo
) : BaseViewModel() {

    var historyList = repo.getHistoryPagingData(token).cachedIn(viewModelScope)

}
