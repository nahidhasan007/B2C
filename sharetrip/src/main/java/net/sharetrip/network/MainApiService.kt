package net.sharetrip.network

import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.holiday.booking.model.HolidayCity
import net.sharetrip.holiday.booking.model.HolidayListResponse
import net.sharetrip.model.*
import net.sharetrip.shared.model.user.User
import retrofit2.http.*

interface MainApiService {

    @GET("api/v1/user/user-info")
    suspend fun getUserInformation(@Header("accesstoken") key: String): GenericResponse<RestResponse<User>>

    @POST("https://notifier.sharetrip.net/api/v1/token")
    suspend fun uploadFirebaseToken(@Body fcmTokenModel: FcmTokenModel): GenericResponse<RestResponse<FcmTokenModel>>

    @GET("api/v1/package/list")
    suspend fun fetchPopularHoliday(@Query("limit") limit: Int, @Query("offset") offset: Int)
            : GenericResponse<RestResponse<HolidayListResponse>>

    @GET("api/v1/package/city/popular")
    suspend fun fetchPopularCityForHoliday(): GenericResponse<RestResponse<List<HolidayCity>>>

    @GET("api/v1/rewards/earn-coin-treasure-box")
    suspend fun getTreasureResponse(@Header("accesstoken") key: String): GenericResponse<RestResponse<TreasureResponse>>

    @GET("https://notifier.sharetrip.net/api/v1/notifications")
    suspend fun fetchDealsList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): RestDealsResponse

    @GET("api/v1/blog/slider")
    suspend fun getBlogSlider(): GenericResponse<RestResponse<BlogSliderResponse>>

    @GET("api/v1/blog/top-post")
    suspend fun getTopBlogPost(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): GenericResponse<RestResponse<BlogSliderResponse>>

    @GET("api/v1/blog/trending")
    suspend fun getTrendingBlogPost(): GenericResponse<RestResponse<BlogSliderResponse>>

    @GET("api/v1/blog/booking")
    suspend fun getBlogBooking(): GenericResponse<RestResponse<BlogBookingResponse>>

    @GET("api/v1/blog/post/search")
    suspend fun getBlogSearchResults(
        @Query("text") text: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): GenericResponse<RestResponse<BlogSliderResponse>>

    @GET("api/v1/blog/post/detail")
    suspend fun getBlogDetails(
        @Query("slug") slug: String
    ): GenericResponse<RestResponse<BlogDetailsResponse>>

    @GET("api/v1/rewards/tripcoin-summary")
    suspend fun getTripSummary(@Header("accesstoken") key: String): GenericResponse<RestResponse<TripCoinSummaryResponse>>
}
