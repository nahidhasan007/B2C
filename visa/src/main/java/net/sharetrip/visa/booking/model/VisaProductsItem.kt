package net.sharetrip.visa.booking.model

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class VisaProductsItem(
    val visaFee: Double = 0.0,
    val visaPrepMinDays: Double? = null,
    val title: String? = null,
    val type: String? = null,
    val processingFee: Double = 0.0,
    val productCode: String? = null,
    val cancelTime: Double? = null,
    val specialNote: String? = null,
    val courierCharge: Double = 0.0,
    val discountType: String? = null,
    val discount: Double? = 0.0,
    val discountPrice: Double? = 0.0,
    val allowedProfessions: String? = null,
    val cancelCharge: Double? = null,
    val maxStayDays: Int? = 0,
    val validityDays: Int? = 0,
    val requiredDocs: String = "",
    val bookingCurrency: String = ""
) : Parcelable {

    val requiredDocList: List<ReqDocsItem>?
        get() = getData()

    private fun getData(): List<ReqDocsItem>? {
        val parser = JsonParser()
        val elem: JsonElement = parser.parse(requiredDocs)

        val gson = Gson().newBuilder().create()
        val token: TypeToken<List<ReqDocsItem>> = object : TypeToken<List<ReqDocsItem>>() {}

        return gson.fromJson(elem, token.type)
    }

    var professionSelection = 0
}
