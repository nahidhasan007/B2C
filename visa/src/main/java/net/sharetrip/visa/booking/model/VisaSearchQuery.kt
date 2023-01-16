package net.sharetrip.visa.booking.model

import android.os.Parcelable
import net.sharetrip.shared.utils.DateUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class VisaSearchQuery(
    var entryDate: String = "",
    var exitDate: String = "",
    var destinationCountry: String = "",
    var visaCountryCode: String = "",
    var travellerCount: Int = 0,
    var visaType: String? = "",
    var visaPrepMinDays: Int? = 12
) : Parcelable {
    var travelDataFullInfo: String? = ""
        get() {
            return "$entryDate-$exitDate, $travellerCount Traveller(s)"
        }
    var selectedVisaType: Int? = -1
    var travellers: MutableList<VisaItemTraveler> = mutableListOf()
    var currentTravellerPosition: Int? = 1
    var totalAmount: Double? = 0.0
    var bookingCurrency = "BDT"
    var cardSeries: String? = ""
    var gateway: String? = ""
    var tripCoin = 0
    var visaSelection: VisaSelection? = null
    var productCode: String? = ""
    var nationality: String? = "BD"
    var searchID: Int? = null

    init {
        entryDate = DateUtil.getApiDateFormat(visaPrepMinDays!!)
        exitDate = DateUtil.getApiDateFormat(visaPrepMinDays!! + 7)
        destinationCountry = "Thailand"
        visaCountryCode = "ST109VSCT20200715939402549332"
        travellerCount = 1
        visaType = ""
    }

    fun travellerLabelInfo(): String {
        var text = "Traveller "
        text += if (currentTravellerPosition == 0) {
            "${currentTravellerPosition!! + 1} Primary"
        } else {
            "${currentTravellerPosition!! + 1}"
        }
        return text
    }
}
