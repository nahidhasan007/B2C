package net.sharetrip.profile.model

import com.squareup.moshi.Json

data class RemoveTravelerInfo (
    @Json(name = "code")
    var code: String = "",
    @field:Json(name = "quickPick")
    var isQuickPick: Boolean = false
)
