package net.sharetrip.tour.booking.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import net.sharetrip.shared.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.tour.model.TourItem
import net.sharetrip.tour.model.TourPopCity

class TourMainViewModel(
    private val repository: TourMainRepo,
) : BaseOperationalViewModel() {

    private val _navigateToDestinations = MutableLiveData<Event<Pair<TourMainNavDestinations, Any?>>>()
        val navigateToDestinations: LiveData<Event<Pair<TourMainNavDestinations, Any?>>>
            get() = _navigateToDestinations

    val pagedListTour: kotlinx.coroutines.flow.Flow<PagingData<TourItem>> =
        repository.getTourList().cachedIn(viewModelScope)

    val liveCities = MutableLiveData<List<TourPopCity>>()

    init {
        fetchPopularTourCity()
    }

    private fun fetchPopularTourCity() {
        executeSuspendedCodeBlock(OperationTags.GET_POPULAR_TOUR_CITY.name) {
            repository.getPopularTourCity()
        }
    }

    fun onClickSearchBar() {
        _navigateToDestinations.value = Event(Pair(TourMainNavDestinations.TO_CITY_SEARCH, null))
    }

    fun navigateToTourList(position: Int) {
        _navigateToDestinations.value = Event(Pair(TourMainNavDestinations.TO_TOUR_LIST, position))
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when(operationTag){
            OperationTags.GET_POPULAR_TOUR_CITY.name ->{
                val response = data.body as RestResponse<List<TourPopCity>>
                liveCities.postValue(response.response!!)
            }
        }
    }

    enum class OperationTags{ GET_POPULAR_TOUR_CITY }
    enum class TourMainNavDestinations { TO_CITY_SEARCH, TO_TOUR_LIST }
}
