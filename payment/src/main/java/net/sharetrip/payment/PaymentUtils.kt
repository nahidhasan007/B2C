package net.sharetrip.payment

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

private val moshi = Moshi.Builder().build()

fun String.convertPaymentUrlList() : List<net.sharetrip.payment.model.PaymentUrl>? {
    val type: Type = Types.newParameterizedType(
        MutableList::class.java,
        net.sharetrip.payment.model.PaymentUrl::class.java
    )
    val adapter = moshi.adapter<List<net.sharetrip.payment.model.PaymentUrl>>(type)
    return adapter.fromJson(this)
}

fun String.convertToUrlModel() : net.sharetrip.payment.model.BookingResponse? {
    val jsonAdapter = moshi.adapter(net.sharetrip.payment.model.BookingResponse::class.java)
    return jsonAdapter.fromJson(this)
}