package umc.everyones.lck.data.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import umc.everyones.lck.data.dto.BaseResponse
import umc.everyones.lck.data.dto.request.login.CommonLoginRequestDto
import umc.everyones.lck.data.dto.request.login.RefreshAuthUserRequestDto
import umc.everyones.lck.data.dto.response.login.LoginResponseDto

interface LoginService {

    @Multipart
    @POST("/auth/signup")
    suspend fun signup(
        @Part("signupUserData") signupUserData: RequestBody,
        @Part profileImage: MultipartBody.Part
    ): BaseResponse<LoginResponseDto>

    @POST("/auth/login")
    suspend fun login(
        @Body request:CommonLoginRequestDto
    ):BaseResponse<LoginResponseDto>

    @POST("/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshAuthUserRequestDto
    ):BaseResponse<LoginResponseDto>

    @GET("auth/nickname")
    suspend fun nickname(
        @Query("nickName") nickName: String
    ): BaseResponse<Boolean>

    @GET("/auth/users/test")
    suspend fun usertest():BaseResponse<Unit>
}