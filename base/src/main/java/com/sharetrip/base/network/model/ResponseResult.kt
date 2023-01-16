package com.sharetrip.base.network.model

import okhttp3.ResponseBody
import java.io.IOException

sealed class ResponseResult<out R> {
    data class Success<out T>(val data: T,val key:String) : ResponseResult<T>()

    data class ApiErrorBody(val errorBody: ResponseBody?, val code: Int, val key:String) : ResponseResult<Nothing>()

    data class ApiError(val errorBody: String?, val code: Int?,val key:String) : ResponseResult<Nothing>()

    data class NetworkError(val error: IOException,val key:String) : ResponseResult<Nothing>()

    data class UnknownError(val exception: Exception?,val key:String) : ResponseResult<Nothing>()

    data class InProgress(val isLoading:Boolean,val key:String) : ResponseResult<Nothing>()
}
