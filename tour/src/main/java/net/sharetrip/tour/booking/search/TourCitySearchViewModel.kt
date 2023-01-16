package net.sharetrip.tour.booking.search

import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.tour.model.TourPopCity

class TourCitySearchViewModel(private val repo: TourCitySearchRepo) : BaseOperationalViewModel() {

    val cities = MutableLiveData<List<TourPopCity>>()

    init {
        fetchPopularTourCityList()
    }

    private fun fetchPopularTourCityList() {
        dataLoading.set(true)

        executeSuspendedCodeBlock(OperationTags.GET_POP_CITY.name) {
            repo.getPopularCities()
        }
    }

    fun fetchTourCityList(cityKey: String) {
        dataLoading.set(true)

        executeSuspendedCodeBlock(OperationTags.GET_CITY_BY_KEY.name) {
            repo.getCityListByKey(cityKey)
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            OperationTags.GET_POP_CITY.name -> {
                val popCityList = (data.body as RestResponse<*>).response as List<TourPopCity>
                cities.value = popCityList
                dataLoading.set(false)
            }
            OperationTags.GET_CITY_BY_KEY.name -> {
                val cityList = (data.body as RestResponse<*>).response as List<TourPopCity>
                cities.value = cityList
                dataLoading.set(false)
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
    }

    private enum class OperationTags { GET_POP_CITY, GET_CITY_BY_KEY }
}
