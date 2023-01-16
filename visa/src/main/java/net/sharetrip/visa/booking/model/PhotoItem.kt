package net.sharetrip.visa.booking.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class PhotoItem(
    var id: Int? = null,
    var name: String = "",
    var docTitle: String = "",
    var uri: File? = null,
    var url: String = ""
) : Parcelable
