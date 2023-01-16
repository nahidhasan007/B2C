package com.sharetrip.base.network

import com.sharetrip.base.network.model.Status

class NetworkState {
    var status: Status
        private set
    var message: String? = null
        private set

    private constructor(status: Status, message: String) {
        this.status = status
        this.message = message
    }

    private constructor(status: Status) {
        this.status = status
    }

    companion object {
        var LOADED = NetworkState(Status.SUCCESS)
        var LOADING = NetworkState(Status.RUNNING)

        fun emptyResponse(message: String?): NetworkState {
            return NetworkState(
                Status.RESPONSE_EMPTY, message
                    ?: "No available data found. Please retry.")
        }

        fun error(message: String?): NetworkState {
            return NetworkState(Status.FAILED, message ?: "unknown error")
        }
    }
}
