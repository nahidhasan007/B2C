package net.sharetrip.tour.booking.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import net.sharetrip.shared.event.Event
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.model.TourItem
import net.sharetrip.tour.model.TourPopCity

class TourListViewModel(city: TourPopCity, repo: TourListRepo) : BaseViewModel() {

    private val _navigateToCitySearch = MutableLiveData<Event<Boolean>>()
        val navigateToCitySearch: LiveData<Event<Boolean>>
            get() = _navigateToCitySearch

    val cityName = MutableLiveData<String>()
    val cityCode = MutableLiveData<String>()

    val pagedListTour: LiveData<PagingData<TourItem>> =
        Transformations.switchMap(cityCode){
           repo.getTourList(it).cachedIn(viewModelScope)
        }

    init {
        this.cityName.value = city.name
        this.cityCode.value = city.cityCode
    }

    fun onClickSearchBar() {
        _navigateToCitySearch.value = Event(true)
    }

    fun onClickFilter() {}

}
