package com.sharetrip.base.viewmodel

import androidx.lifecycle.viewModelScope
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.GenericError
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.utils.ShareTripAppConstants.errorMsg
import kotlinx.coroutines.launch

abstract class BaseOperationalViewModel : BaseViewModel() {

    protected fun executeSuspendedCodeBlock(
        operationTag: String = String(),
        codeBlock: suspend () -> GenericResponse<Any>
    ) {
        viewModelScope.launch {
            when (val data = codeBlock()) {
                is BaseResponse.Success -> {
                    onSuccessResponse(operationTag, data)
                }

                is BaseResponse.ApiError ->
                    onApiError(operationTag, data)

                is BaseResponse.NetworkError ->
                    onNetworkError(operationTag, data)

                is BaseResponse.UnknownError ->
                    onUnknownError(operationTag, data)
            }
        }
    }

    abstract fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>)

    protected fun onApiError(operationTag: String, result: BaseResponse.ApiError<GenericError>) {
        if (operationTag == String() && result.errorBody.message != "Please provide the access token.")
            showMessage(result.errorBody.message)
        onAnyError(operationTag, result.errorBody.message ?: errorMsg, ErrorType.API_ERROR)
    }

    protected fun onNetworkError(operationTag: String, result: BaseResponse.NetworkError) {
        if (operationTag == String())
            showMessage(result.error.message)
        onAnyError(operationTag, result.error.message ?: errorMsg, ErrorType.NETWORK_ERROR)
    }

    protected fun onUnknownError(operationTag: String, result: BaseResponse.UnknownError) {
        if (operationTag == String())
            showMessage(result.error.message)
        onAnyError(operationTag, result.error.message ?: errorMsg, ErrorType.UNKNOWN_ERROR)
    }

    protected open fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {}
}
