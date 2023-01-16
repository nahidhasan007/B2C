package net.sharetrip.flight.history.view.rule

import androidx.core.text.HtmlCompat
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.Strings
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.history.model.AirFareResponseOfRule
import net.sharetrip.flight.history.model.ApiCallingKey
import net.sharetrip.flight.history.model.RuleType
import net.sharetrip.flight.history.model.localdatasource.UIMessageData.Companion.AIR_FARE_RULE_NOT_FOUND
import net.sharetrip.flight.history.model.localdatasource.UIMessageData.Companion.FARE_DETAILS_NOT_FOUND
import net.sharetrip.flight.history.model.localdatasource.UIMessageData.Companion.NETWORK_ERROR
import net.sharetrip.flight.network.DataManager

class FlightRuleViewModel(
    private val searchId: String,
    private val sequenceCode: String,
    private val ruleType: Int
) : BaseOperationalViewModel() {
    var data = MutableLiveData<CharSequence>()

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        if (operationTag == ApiCallingKey.Rule.name) {
            val response = (data.body as RestResponse<*>).response as AirFareResponseOfRule
            parseData(response, ruleType)
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        if (operationTag == ApiCallingKey.Rule.name) {
             showMessage(NETWORK_ERROR)
        }
    }

    init {
        if (ruleType != RuleType.BAGGAGE) {
            fetchData()
        }
    }

    private fun parseData(airFareResponse: AirFareResponseOfRule, ruleType: Int) {
        when {
            RuleType.AIR_FARE_RULE == ruleType ->
                data.postValue(getAirFareRulesDetails(airFareResponse))

            RuleType.FARE_DETAILS == ruleType ->
                data.postValue(getFareDetails(airFareResponse))
        }
    }

    private fun getAirFareRulesDetails(airFareResponse: AirFareResponseOfRule): String {
        val airFareRules = airFareResponse.airFareRules
        val builder = StringBuilder()
        if (airFareRules != null) {
            for ((type, rules) in airFareRules) {
                builder.append(type)
                    .append(Strings.LINE_BREAK)
                    .append(Strings.LINE_BREAK)
                    for ((_,type1, text) in rules!!) {
                        builder.append(type1).append(Strings.LINE_BREAK).append(Strings.LINE_BREAK)
                        builder.append(text).append(Strings.LINE_BREAK)
                }
                builder.append(Strings.LINE_BREAK).append(Strings.LINE_BREAK)
            }
        }

        if (builder.isEmpty())
            builder.append(AIR_FARE_RULE_NOT_FOUND)

        return builder.toString()
    }

    private fun getFareDetails(response: AirFareResponseOfRule): CharSequence {
        var message = response.fareDetails
        if (Strings.isBlank(message)) {
            message = FARE_DETAILS_NOT_FOUND
        }
        var ruleData = ""
        if (message != null) {
            ruleData = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        }
        return ruleData
    }

    private fun fetchData() {
        executeSuspendedCodeBlock(ApiCallingKey.Rule.name) {
            DataManager.flightHistoryApiService.getAirFareRules(searchId, sequenceCode)
        }
    }
}