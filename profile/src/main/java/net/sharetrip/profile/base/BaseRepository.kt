package net.sharetrip.profile.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.*
import com.sharetrip.base.utils.ShareTripAppConstants.errorMsg

abstract class BaseRepository {

    protected val setDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
        get() = setDataLoading

    private val setFileUploading = MutableLiveData<Boolean>()
    val isFileUploading: LiveData<Boolean>
        get() = setFileUploading

    protected val setApiStatus = MutableLiveData<Status>()
    val apiStatus: LiveData<Status>
        get() = setApiStatus

    val showMessage = MutableLiveData<Event<String>>()

    protected suspend fun <T> callApi(
        executableCodeBlock: suspend () -> GenericResponse<Any>,
        onSuccess: (data: T) -> Unit,
        onFailed: (errorType: ErrorType, errorMessage: String) -> Unit
    ) {
        setDataLoading.value = true

        handleApiCall(executableCodeBlock, onSuccess, onFailed)

        setDataLoading.value = false
    }

    protected suspend fun <T> callApiForFileUpload(
        executableCodeBlock: suspend () -> GenericResponse<Any>,
        onSuccess: (data: T) -> Unit,
        onFailed: (errorType: ErrorType, errorMessage: String) -> Unit
    ) {
        setFileUploading.value = true

        handleApiCall(executableCodeBlock, onSuccess, onFailed)

        setFileUploading.value = false
    }

    private suspend fun <T> handleApiCall(
        executableCodeBlock: suspend () -> GenericResponse<Any>,
        onSuccess: (data: T) -> Unit,
        onFailed: (errorType: ErrorType, errorMessage: String) -> Unit
    ) {
        when (val data = executableCodeBlock()) {
            is BaseResponse.Success -> {
                setApiStatus.value = Status.SUCCESS
                if (data.body !is RestResponse<*>) {
                    onSuccess(data.body as T)
                    return
                }
                onSuccess((data.body as RestResponse<*>).response as T)
            }

            is BaseResponse.ApiError -> {
                setApiStatus.value = Status.FAILED
                val errorMessage = data.errorBody.message
                showMessage.value = Event(errorMessage ?: errorMsg)
                onFailed(ErrorType.API_ERROR, errorMessage ?: errorMsg)
            }

            is BaseResponse.NetworkError -> {
                setApiStatus.value = Status.FAILED
                val errorMessage = data.error.message.toString()
                showMessage.value = Event("No Internet Connection")
                onFailed(ErrorType.API_ERROR, errorMessage)
            }

            is BaseResponse.UnknownError -> {
                setApiStatus.value = Status.FAILED
                onFailed(ErrorType.API_ERROR, data.error.message?: errorMsg)
            }
        }
    }
}
