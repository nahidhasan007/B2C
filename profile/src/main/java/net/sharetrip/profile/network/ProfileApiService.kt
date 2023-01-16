package net.sharetrip.profile.network

import net.sharetrip.shared.model.EmptyResponse
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.profile.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ProfileApiService {

    @GET("api/v1/user/user-info")
    suspend fun getProfileResponse(@Header("accesstoken") key: String)
            : GenericResponse<RestResponse<UserProfile>>

    @PATCH("api/v1/user/update-profile")
    suspend fun updateProfile(
        @Header("accesstoken") key: String,
        @Body updateProfileParam: UserProfile
    ): GenericResponse<RestResponse<UserProfile>>

    @POST("api/v1/travel-details/add-new-traveler")
    suspend fun updateTravelerInfo(
        @Header("accesstoken") key: String,
        @Body addTravelerParam: Traveler
    ): GenericResponse<RestResponse<EmptyResponse>>

    @PUT("api/v1/user/update-quick-pick")
    suspend fun deleteTravelerInfo(
        @Header("accesstoken") key: String,
        @Body addTravelerParam: RemoveTravelerInfo
    ): GenericResponse<RestResponse<EmptyResponse>>

    @Multipart
    @POST("api/v1/upload-file")
    suspend fun sendFile(
        @Header("accesstoken") key: String,
        @Part uploadFile: MultipartBody.Part
    ): GenericResponse<RestResponse<ImageUploadResponse>>

    @POST("api/v1/travel-details/add-new-traveler")
    suspend fun addTraveler(@Header("accesstoken") key: String, @Body addTravelerParam: Traveler)
            : GenericResponse<RestResponse<EmptyResponse>>

    @GET("api/v1/country")
    suspend fun getCountryList(@Header("accesstoken") key: String):
            GenericResponse<RestResponse<List<CountryCode>>>

    @GET("api/v1/currency")
    suspend fun getCurrencyList(@Header("accesstoken") key: String):
            GenericResponse<RestResponse<List<CountryCode>>>

    @GET("api/v1/language")
    suspend fun getLanguageList(@Header("accesstoken") key: String)
            : GenericResponse<RestResponse<List<CountryCode>>>

    @GET("api/v1/distance-unit")
    suspend fun getDistanceList(@Header("accesstoken") key: String):
            GenericResponse<RestResponse<List<CountryCode>>>

    @Multipart
    @POST("api/v1/user/update-avatar")
    suspend fun sendImageAvater(
        @Header("accesstoken") key: String,
        @Part uploadFile: MultipartBody.Part
    ): GenericResponse<RestResponse<ImageUploadResponse>>

    @GET("api/v1/user/list-card")
    suspend fun fetchSavedCards(@Header("accesstoken") header: String):
            GenericResponse<RestResponse<List<SavedCards>>>

    @HTTP(method = "DELETE", path = "api/v1/user/remove-card", hasBody = true)
    suspend fun deleteSavedCard(
        @Header("accesstoken") header: String,
        @Body removeCard: RemoveCard
    ): GenericResponse<RestResponse<List<SavedCards>>>

    @PUT("api/v1/user/change-password")
    suspend fun updatePassword(
        @Header("accesstoken") key: String,
        @Body changePassword: ChangePassword
    ): GenericResponse<RestResponse<ChangePasswordResponse>>

    @GET("api/v1/rewards/tripcoin-summary")
    suspend fun getTripSummary(@Header("accesstoken") key: String):
            GenericResponse<RestResponse<TripCoinSummaryResponse>>

    @GET("https://sharetrip-96054.firebaseio.com/flight_admin/faq.json")
    suspend fun getFAQResponse(): GenericResponse<FaqResponse>

    @GET("https://sharetrip-96054.firebaseio.com/flight_admin/toc.json")
    suspend fun getTOCResponse(): GenericResponse<TocResponse>

    @GET("api/v1/wheel/leader-board")
    suspend fun getLeaderBoardData(@Header("accesstoken") key: String)
            : GenericResponse<RestResponse<LeaderBoardResponse>>

    @GET("api/v1/wheel/rewards")
    suspend fun fetchWheelReward(@Header("accesstoken") key: String)
            : GenericResponse<RestResponse<LeaderBoardResponse>>

     @GET("api/v1/user/delete-account-reasons")
    suspend fun getAccountDeletionReasons(@Header("accesstoken") key: String)
            : GenericResponse<RestResponse<List<DeletionReason>>>

    @HTTP(method = "DELETE", path = "api/v1/user/delete-my-account", hasBody = true)
     suspend fun deleteAccount(@Header("accesstoken")key:String, @Body deleteAccount: DeleteAccount) :
            GenericResponse<RestResponse<EmptyResponse>>
}
