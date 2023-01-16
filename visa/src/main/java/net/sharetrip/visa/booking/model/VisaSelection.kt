package net.sharetrip.visa.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Type

@Parcelize
data class VisaSelection(
    @field:Json(name = "visa_products")
    val visaProducts: List<VisaProductsItem>? = null,
    val localTime: String? = null,
    val exchangeRate: String? = null,
    val visaCountryCode: String? = null,
    val currency: String? = null,
    val countryName: String? = null,
    val importantNotes: String = "",
    val faq: String? = "",
    val photos: List<String>? = null,
    val searchID: Int?,
    val points: Points = Points()
) : Parcelable {
    val faqList: List<VisaFaq>?
        get() = getFAQ()

    private fun getFAQ(): List<VisaFaq>? {
        val moshi = Moshi.Builder().build()
        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            VisaFaq::class.java
        )
        val adapter: JsonAdapter<List<VisaFaq>> =
            moshi.adapter(type)
        return adapter.fromJson(faq)
    }
}
