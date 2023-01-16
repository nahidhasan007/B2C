package net.sharetrip.flight.history.view.voidorrefundconfirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.flight.network.FlightHistoryApiService

class ConfirmationVIewModelFactory(
    private val apiService: FlightHistoryApiService,
    private val refundSearchId: String?,
    private val  voidSearchId : String?,
    private val  requestType : String,
    private val  token : String
):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConfirmationViewModel(apiService,refundSearchId,voidSearchId,requestType,token) as T
    }
}
