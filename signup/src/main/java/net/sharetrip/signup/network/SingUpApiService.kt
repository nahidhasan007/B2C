package net.sharetrip.signup.network

import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.signup.model.AppleToken
import net.sharetrip.signup.model.SignUpInfo
import net.sharetrip.signup.model.UserCredential
import net.sharetrip.shared.model.UserInfo
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SingUpApiService {

    @POST("api/v1/auth/signup")
    suspend fun getEmailSignUpResult(@Body requestParams: SignUpInfo): GenericResponse<RestResponse<UserInfo>>

    @POST("api/v1/auth/login")
    suspend fun getEmailLoginResponse(@Body credential: UserCredential): GenericResponse<RestResponse<UserInfo>>

    @FormUrlEncoded
    @POST("api/v1/auth/facebook")
    suspend fun getFacebookLoginResponse(@Field("facebookToken") fbToken: String): GenericResponse<RestResponse<UserInfo>>

    @FormUrlEncoded
    @POST("api/v1/auth/google")
    suspend fun getGoogleLoginResponse(@Field("token") googleToken: String): GenericResponse<RestResponse<UserInfo>>

    @POST("api/v1/auth/apple")
    suspend fun signInWithApple(@Body appleToken: AppleToken): GenericResponse<RestResponse<UserInfo>>

    @FormUrlEncoded
    @POST("api/v1/user/forgot-password")
    suspend fun getForgetPasswordResponse(@Field("email") emailId: String): GenericResponse<RestResponse<Any>>
}