package net.sharetrip.holiday.booking.model

import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json

data class HolidayItem(
    var productCode: String? = null,
    var title: String? = null,
    var lowestPrice: Int = 0,
    var duration: Int = 0,
    var withAirfare: String? = null,
    var locations: MutableList<String>? = null,
    var currency: String = "",
    var discountPrice: Int = 0,
    var discount: Double? = 0.0,
    var discountType: String = "",

    @field:Json(name = "points")
    var point: Point = Point(),

    @field:Json(name = "images")
    var image: MutableList<String> = ArrayList<String>()
) {
    companion object {

        var DIFF_CALLBACK: DiffUtil.ItemCallback<HolidayItem> =
            object : DiffUtil.ItemCallback<HolidayItem>() {
                override fun areItemsTheSame(oldItem: HolidayItem, newItem: HolidayItem): Boolean {
                    return oldItem.productCode == newItem.productCode
                }

                override fun areContentsTheSame(
                    oldItem: HolidayItem,
                    newItem: HolidayItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
