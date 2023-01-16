package net.sharetrip.holiday.booking.view.main

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.Flow
import net.sharetrip.holiday.booking.model.HolidayItem

class HolidayMainViewModel(
    repository: HolidayMainRepository
) : BaseViewModel() {

    var pagedListHoliday: Flow<PagingData<HolidayItem>> =
        repository.getPopularHolidays().cachedIn(viewModelScope)

}
