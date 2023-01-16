package net.sharetrip.flight.history.view.rule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FlightRuleViewModelFactory(private val searchId: String, private val sequenceCode: String, private val ruleType: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FlightRuleViewModel(searchId, sequenceCode, ruleType) as T
    }
}