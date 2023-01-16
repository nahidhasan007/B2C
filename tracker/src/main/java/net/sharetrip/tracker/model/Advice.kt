package net.sharetrip.tracker.model

import net.sharetrip.shared.utils.parseTextAfterColon
import com.squareup.moshi.Json

data class Advice(
        val date: String? = null,
        val requirements: Requirements? = null,
        val level: Int? = null,
        val recommendation: String? = null,
        val restrictions: Restrictions? = null,
        val lat: String? = null,
        val lon: String? = null,
        @field:Json(name = "government_response_index")
        val governmentResponseIndex: String? = null,
        @field:Json(name = "economic_support_index")
        val economicSupportIndex: String? = null,
        @field:Json(name = "containment_and_health_index")
        val containmentAndHealthIndex: String? = null,
        @field:Json(name = "stringency_index")
        val stringencyIndex: String? = null,
        @field:Json(name = "country_code")
        val countryCode: String? = null,
        @field:Json(name = "level_desc")
        val levelDesc: String = ""

) {
    fun getCustomizedLevelDescription(): String {
        return levelDesc.parseTextAfterColon().trim()
    }
}
