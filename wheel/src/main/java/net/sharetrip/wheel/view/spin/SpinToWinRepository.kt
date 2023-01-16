package net.sharetrip.wheel.view.spin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.Status
import net.sharetrip.shared.utils.MoshiUtil
import net.sharetrip.wheel.model.SpinResponse
import net.sharetrip.wheel.model.UserProfile
import net.sharetrip.wheel.network.WheelApiService
import retrofit2.HttpException
import java.io.IOException

class SpinToWinRepository(private val apiService: WheelApiService) {

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
    get() = _isDataLoading

    private val _apiStatus = MutableLiveData<Status>()
    val apiStatus: LiveData<Status>
    get() = _apiStatus

    private val _showToast = MutableLiveData<Event<String>>()
    val showToast: LiveData<Event<String>>
    get() = _showToast

    private val _sorryMessage = MutableLiveData<Event<String>>()
    val sorryMessage: LiveData<Event<String>>
    get() = _sorryMessage

    private val _spinResponse = MutableLiveData<SpinResponse>()
    val spinResponse : LiveData<SpinResponse>
    get() = _spinResponse

    private val _userProfileResponse = MutableLiveData<UserProfile>()
    val userProfileResponse: LiveData<UserProfile>
    get() = _userProfileResponse

    private val _shareUrl = MutableLiveData<String>()
    val shareUrl: LiveData<String>
    get() = _shareUrl

    suspend fun getLuckyWheel(token: String){
        _isDataLoading.value = true

        try {
            val data = apiService.fetchLuckyWheel(token)
            _spinResponse.value = data.response!!
            _apiStatus.value = Status.SUCCESS
        }
        catch (e: Exception) {
            _apiStatus.value = Status.FAILED

            if (e is HttpException) {
                try {
                    val errorMsg = e.response()?.errorBody()!!.toString()
                    val errorResponse = MoshiUtil.convertStringToErrorResponse(errorMsg)
                    _showToast.value = Event(errorResponse.message)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    _showToast.value = Event("Something went wrong")
                }
            } else {
                _showToast.value = Event("No Internet")
            }
        }

        _isDataLoading.value = false
    }

    suspend fun sendLuckWheel(key:String, spinCode: String){
        _isDataLoading.value = true

        try {
            val data = apiService.sendLuckyWheel(key, spinCode)
            _spinResponse.value = data.response!!
            _apiStatus.value = Status.SUCCESS
        }
        catch (throwable: Exception) {
            _apiStatus.value = Status.FAILED

            if (throwable is HttpException) {
                try {
                    val errorMsg = throwable.response()?.errorBody()!!.string()
                    val errorResponse =
                        MoshiUtil.convertStringToErrorResponse(errorMsg)
                    _sorryMessage.value = Event(errorResponse.message)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        _isDataLoading.value = false
    }

    suspend fun updateUserProfile(token: String, firstName: String, lastName: String){
        _isDataLoading.value = true

        try {
            val data = apiService.updateNameOfProfile(token, firstName, lastName)
            _userProfileResponse.value = data.response!!
            _apiStatus.value = Status.SUCCESS
        } catch (e: Exception) {
            _apiStatus.value = Status.FAILED
        }

        _isDataLoading.value = false
    }

    suspend fun getShareUrl(token: String, type: String, value: String){
        _isDataLoading.value = true

        try {
            apiService.getShareResponse(token, type, value)
            _shareUrl.value = "done"
            _apiStatus.value = Status.SUCCESS
        } catch (e: Exception) {
            _apiStatus.value = Status.FAILED
            _shareUrl.value = "done"
        }

        _isDataLoading.value = false
    }
}