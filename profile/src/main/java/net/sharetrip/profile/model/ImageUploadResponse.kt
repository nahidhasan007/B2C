package net.sharetrip.profile.model

import com.squareup.moshi.Json

data class ImageUploadResponse (
    @field:Json(name = "path")
    var path: String
)
