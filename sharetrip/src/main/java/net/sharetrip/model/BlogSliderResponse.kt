package net.sharetrip.model

import com.squareup.moshi.Json

data class BlogSliderResponse(
	@field:Json(name = "blog_posts")
	val blogPostList: List<BlogPost> = ArrayList()
)