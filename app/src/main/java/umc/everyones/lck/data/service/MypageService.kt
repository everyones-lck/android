package umc.everyones.lck.data.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Query
import umc.everyones.lck.data.UpdateProfileRequest
import umc.everyones.lck.data.dto.BaseResponse
import umc.everyones.lck.data.dto.request.mypage.CancelHostViewingPartyMypageRequestDto
import umc.everyones.lck.data.dto.request.mypage.CancelParticipateViewingPartyMypageRequestDto
import umc.everyones.lck.data.dto.request.mypage.UpdateProfilesRequestDto
import umc.everyones.lck.data.dto.request.mypage.UpdateTeamRequestDto
import umc.everyones.lck.data.dto.response.NonBaseResponse
import umc.everyones.lck.data.dto.response.mypage.CommentsMypageResponseDto
import umc.everyones.lck.data.dto.response.mypage.HostViewingPartyMypageResponseDto
import umc.everyones.lck.data.dto.response.mypage.InquiryProfilesResponseDto
import umc.everyones.lck.data.dto.response.mypage.ParticipateViewingPartyMypageResponseDto
import umc.everyones.lck.data.dto.response.mypage.PostsMypageResponseDto
import umc.everyones.lck.data.dto.response.mypage.UpdateProfilesResponseDto

interface MypageService {

    @GET("my-pages/profiles")
    suspend fun inquiryProfiles(): BaseResponse<InquiryProfilesResponseDto>

    @GET("my-pages/posts")
    suspend fun postsMypage(
        @Query("size") size: Int = 6, // 기본값 6
        @Query("page") page: Int = 0 // 기본값 0
    ): BaseResponse<PostsMypageResponseDto>

    @GET("my-pages/comments")
    suspend fun commentsMypage(
        @Query("size") size: Int = 6, // 기본값 6
        @Query("page") page: Int = 0 // 기본값 0
    ):BaseResponse<CommentsMypageResponseDto>

    @GET("my-pages/viewing-parties/participate")
    suspend fun participateViewingPartyMypage(
        @Query("size") size: Int = 6, // 기본값 6
        @Query("page") page: Int = 0 // 기본값 0
    ):BaseResponse<ParticipateViewingPartyMypageResponseDto>

    @GET("my-pages/viewing-parties/host")
    suspend fun hostViewingPartyMypage(
        @Query("size") size: Int = 6, // 기본값 6
        @Query("page") page: Int = 0 // 기본값 0
    ):BaseResponse<HostViewingPartyMypageResponseDto>

    @DELETE("my-pages/viewing-parties/participate")
    suspend fun cancelParticipateViewingPartyMypage(
        @Query ("viewingPartyId") viewingPartyId: Int
    ):BaseResponse<Boolean>

    @DELETE("my-pages/viewing-parties/host")
    suspend fun cancelHostViewingPartyMypage(
        @Query ("viewingPartyId") viewingPartyId: Int
    ):BaseResponse<Boolean>

    @DELETE("my-pages/logout")
    suspend fun logout(
        @Header("Refresh") refreshToken: String
    ): NonBaseResponse

    @DELETE("my-pages/withdrawal")
    suspend fun withdraw(): NonBaseResponse

    @Multipart
    @PATCH("my-pages/profiles")
    suspend fun updateProfiles(
        @Part profileImage: MultipartBody.Part,
        @Part("updateProfileRequest") request: RequestBody
    ): BaseResponse<UpdateProfilesResponseDto>

    @PATCH("my-pages/my-team")
    suspend fun updateTeam(
        @Body request: UpdateTeamRequestDto
    ): BaseResponse<Boolean>
}