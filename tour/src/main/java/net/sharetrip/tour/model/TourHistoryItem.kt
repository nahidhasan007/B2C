package net.sharetrip.tour.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import net.sharetrip.shared.utils.DateUtil
import kotlinx.parcelize.Parcelize
import java.text.ParseException

@Parcelize
data class TourHistoryItem(
    val VATOnConvenienceFee: Int,
    val adultsCount: Int,
    val booked_transfer: BookedTransfer,
    val bookingCode: String,
    val bookingCurrency: String,
    val bookingStatus: String,
    val child3To6Count: Int,
    val child7To12Count: Int,
    val convenienceFee: Int,
    val couponValue: Int,
    val departureTime: String,
    val infantCount: Int,
    val paymentStatus: String,
    val primaryContact: PrimaryContact,
    val totalAmount: Int,
    val tourDate: String,
    val tripCoin: Int
) : Parcelable {

    val childCount: String
        get() = (child3To6Count + child7To12Count + infantCount).toString()

    val travellers: String
        get() = "$adultsCount Adt $childCount Chd"

    val dateOnly: String
        get() {

            var date = ""
            try {
                date = DateUtil.parseDisplayDateFormatFromApiDateFormat(tourDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return date
        }

    val dateFigure: String
        get() {
            val parts = dateOnly.split(" ".toRegex(), 2).toTypedArray()
            return parts[0]
        }

    val monthAndYear: String
        get() {
            val parts = dateOnly.split(" ".toRegex(), 2).toTypedArray()
            return parts[1]
        }

    companion object{
        val DIFF_UTIL: DiffUtil.ItemCallback<TourHistoryItem> =
            object : DiffUtil.ItemCallback<TourHistoryItem>() {
                override fun areItemsTheSame(
                    oldItem: TourHistoryItem,
                    newItem: TourHistoryItem
                ): Boolean {
                    return oldItem.bookingCode == newItem.bookingCode
                }

                override fun areContentsTheSame(
                    oldItem: TourHistoryItem,
                    newItem: TourHistoryItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}