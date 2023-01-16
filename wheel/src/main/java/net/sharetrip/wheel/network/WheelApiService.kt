package net.sharetrip.wheel.network

import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.wheel.model.SpinResponse
import net.sharetrip.wheel.model.UserProfile
import retrofit2.http.*

interface WheelApiService {

    @GET("api/v1/wheel/play")
    suspend fun sendLuckyWheel(
        @Header("accesstoken") key: String,
        @Query("code") code: String
    ): RestResponse<SpinResponse>

    @FormUrlEncoded
    @POST("api/v1/get-share-link")
    suspend fun getShareResponse(
        @Header("accesstoken") key: String, @Field("type") type: String,
        @Field("value") value: String
    ): RestResponse<String>

    @FormUrlEncoded
    @PATCH("api/v1/user/update-profile")
    suspend fun updateNameOfProfile(
        @Header("accesstoken") key: String, @Field("givenName") firstName: String,
        @Field("surName") lastName: String
    ): RestResponse<UserProfile>

    @GET("api/v1/wheel")
    suspend fun fetchLuckyWheel(@Header("accesstoken") key: String): RestResponse<SpinResponse>
}
