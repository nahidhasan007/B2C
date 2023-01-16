package net.sharetrip.visa.booking.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisaItem(
    val localTime: String? = null,
    val lowestPrice: Double = 0.0,
    val exchangeRate: String? = null,
    val visaCountryCode: String? = null,
    val discount: Double = 0.0,
    val discountPrice: Double = 0.0,
    val currency: String? = "",
    val discountType: String = "",
    val visaRequirement: String? = null,
    val countryName: String? = null,
    val photos: List<String?>? = null,
    val points: Points? = null,
    val bookingCurrency: String? = "",
    val visaPrepMinDays: Int = 0
) : Parcelable {

    companion object {

        var DIFF_CALLBACK: DiffUtil.ItemCallback<VisaItem> =
            object : DiffUtil.ItemCallback<VisaItem>() {
                override fun areItemsTheSame(oldItem: VisaItem, newItem: VisaItem): Boolean {
                    return oldItem.visaCountryCode == newItem.visaCountryCode
                }

                override fun areContentsTheSame(oldItem: VisaItem, newItem: VisaItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
