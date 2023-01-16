package net.sharetrip.visa.history.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TravellersItem(
    val profession: String? = "",
    val passportNumber: String? = "",
    val surName: String? = "",
    val gender: String? = "",
    @field:Json(name = "visa_booking_uploaded_docs")
    val visaBookingUploadedDocs: List<VisaBookingUploadedDocsItem>? = ArrayList(),
    val address2: String? = "",
    val address1: String? = "",
    val mobileNumber: String? = "",
    val givenName: String? = "",
    val dateOfBirth: String? = "",
    val nationality: String? = "",
    val primaryContact: String? = "",
    val passportExpireDate: String? = "",
    val email: String? = ""
) : Parcelable