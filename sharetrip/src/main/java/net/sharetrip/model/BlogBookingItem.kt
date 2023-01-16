package net.sharetrip.model

import com.squareup.moshi.Json

data class BlogBookingItem(
	@field:Json(name = "sub_title")
	val subTitle: String,
	@field:Json(name = "button_text")
	val buttonText: String,
	@field:Json(name = "img_src")
	val imgSrc: String,
	val title: String,
	val link: String
)
