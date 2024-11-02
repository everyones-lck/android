package umc.everyones.lck.data.datasource

import okhttp3.MultipartBody
import okhttp3.RequestBody
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

interface MypageDataSource {
    suspend fun inquiryProfiles(): BaseResponse<InquiryProfilesResponseDto>

    suspend fun postsProfiles(size: Int, page: Int): BaseResponse<PostsMypageResponseDto>

    suspend fun commentsProfiles(size: Int, page: Int): BaseResponse<CommentsMypageResponseDto>

    suspend fun participateViewingPartyMypage(size: Int, page: Int): BaseResponse<ParticipateViewingPartyMypageResponseDto>

    suspend fun hostViewingPartyMypage(size: Int, page: Int): BaseResponse<HostViewingPartyMypageResponseDto>

    suspend fun cancelParticipateViewingPartyMypage(viewingPartyId: Int): BaseResponse<Boolean>

    suspend fun cancelHostViewingPartyMypage(viewingPartyId: Int): BaseResponse<Boolean>

    suspend fun logout(refreshToken: String): NonBaseResponse

    suspend fun withdraw(): NonBaseResponse

    suspend fun updateProfiles(profileImage: MultipartBody.Part, request: UpdateProfilesRequestDto): BaseResponse<UpdateProfilesResponseDto>

    suspend fun updateTeam(requestDto: UpdateTeamRequestDto ): BaseResponse<Boolean>
}