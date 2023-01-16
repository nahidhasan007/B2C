package net.sharetrip.holiday.booking.view.list

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.Flow
import net.sharetrip.holiday.booking.model.HolidayItem

class HolidayListViewModel(
    cityCode: String, repository: HolidayListRepository
): BaseViewModel() {

    val tripCount = ObservableField<String>()
    val pagedListHoliday: Flow<PagingData<HolidayItem>> = repository.getHolidaysLiveData(cityCode, tripCount).cachedIn(viewModelScope)

}
