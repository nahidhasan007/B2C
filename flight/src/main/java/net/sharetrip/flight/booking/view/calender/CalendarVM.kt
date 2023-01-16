package net.sharetrip.flight.booking.view.calender

import androidx.lifecycle.MutableLiveData
import net.sharetrip.shared.model.AdvanceSearchResponse
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.network.DataManager

class CalendarVM : BaseOperationalViewModel() {

    val advanceSearchResponse = MutableLiveData<AdvanceSearchResponse>()

    fun getFlightCalenderPrice(from: String, to: String, departOne: String, departTwo: String) {
        executeSuspendedCodeBlock(CALENDER_PRICE) {
            DataManager.flightApiService.getFlightCalendarPriceInfo(
                from,
                to,
                departOne,
                departTwo
            )
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        if (operationTag == CALENDER_PRICE){
            advanceSearchResponse.value = ( data.body as RestResponse<*>).response as AdvanceSearchResponse
        }
    }

    private companion object {
        const val CALENDER_PRICE = "calender_price"
    }
}