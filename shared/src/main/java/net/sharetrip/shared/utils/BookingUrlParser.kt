package net.sharetrip.shared.utils

import org.json.JSONObject

object BookingUrlParser {

    fun parseStringToJson(paymentLink: String): JSONObject {
        var urls: List<String> =
            paymentLink.replace("{", "").replace("}", "").replace(",", "").split("declineUrl=")

        val declineUrl = urls[1]
        urls = urls[0].split("cancelUrl=".toRegex()).toTypedArray().toList()
        val cancelUrl = urls[1]
        urls = urls[0].split("successUrl=".toRegex()).toTypedArray().toList()
        val successUrl = urls[1]
        urls = urls[0].split("paymentUrl=".toRegex()).toTypedArray().toList()
        val paymentUrl = urls[1]

        val jsonObject = JSONObject()
        jsonObject.put("paymentUrl", paymentUrl.trim { it <= ' ' })
        jsonObject.put("successUrl", successUrl.trim { it <= ' ' })
        jsonObject.put("cancelUrl", cancelUrl.trim { it <= ' ' })
        jsonObject.put("declineUrl", declineUrl.trim { it <= ' ' })

        return jsonObject
    }

    fun parseToJSON(paymentLink: String): JSONObject {
        val jsonObject = JSONObject()
        val urls: List<String> =
            paymentLink.replace("{", "").replace("}", "").replace(",", " ").split(" ")
        for (url in urls) {

            when {
                url.contains("paymentUrl".toRegex()) -> {
                    val paymentUrl: List<String> = url.split("paymentUrl=")
                    jsonObject.put("paymentUrl", paymentUrl[1])
                }
                url.contains("successUrl".toRegex()) -> {
                    val successUrl: List<String> = url.split("successUrl=")
                    jsonObject.put("successUrl", successUrl[1])
                }
                url.contains("cancelUrl".toRegex()) -> {
                    val cancelUrl: List<String> = url.split("cancelUrl=")
                    jsonObject.put("cancelUrl", cancelUrl[1])
                }
                url.contains("declineUrl".toRegex()) -> {
                    val declineUrl: List<String> = url.split("declineUrl=")
                    jsonObject.put("declineUrl", declineUrl[1])
                }
                else -> {
                    //do nothing
                }
            }
        }
        return jsonObject
    }

}