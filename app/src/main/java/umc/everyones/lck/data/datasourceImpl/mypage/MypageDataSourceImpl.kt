package umc.everyones.lck.data.datasourceImpl.mypage

import okhttp3.MultipartBody
import okhttp3.RequestBody
import umc.everyones.lck.data.datasource.MypageDataSource
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
import umc.everyones.lck.data.service.MypageService
import javax.inject.Inject

class MypageDataSourceImpl @Inject constructor(
    private val mypageService: MypageService
): MypageDataSource {
    override suspend fun inquiryProfiles(): BaseResponse<InquiryProfilesResponseDto> =
        mypageService.inquiryProfiles()

    override suspend fun postsProfiles(page: Int, size: Int): BaseResponse<PostsMypageResponseDto> =
        mypageService.postsMypage(page, size)

    override suspend fun commentsProfiles(page: Int, size: Int): BaseResponse<CommentsMypageResponseDto> =
        mypageService.commentsMypage(page, size)

    override suspend fun participateViewingPartyMypage(page: Int, size: Int): BaseResponse<ParticipateViewingPartyMypageResponseDto> =
        mypageService.participateViewingPartyMypage(page, size)

    override suspend fun hostViewingPartyMypage(page: Int, size: Int): BaseResponse<HostViewingPartyMypageResponseDto> =
        mypageService.hostViewingPartyMypage(page, size)

    override suspend fun cancelParticipateViewingPartyMypage(viewingPartyId: Int): BaseResponse<Boolean> =
        mypageService.cancelParticipateViewingPartyMypage(viewingPartyId)

    override suspend fun cancelHostViewingPartyMypage(viewingPartyId: Int): BaseResponse<Boolean> =
        mypageService.cancelHostViewingPartyMypage(viewingPartyId)

    override suspend fun logout(refreshToken: String): NonBaseResponse =
        mypageService.logout(refreshToken)

    override suspend fun withdraw(): NonBaseResponse =
        mypageService.withdraw()

     override suspend fun updateProfiles(profileImage: MultipartBody.Part, updateProfileRequest: UpdateProfilesRequestDto): BaseResponse<UpdateProfilesResponseDto> =
         mypageService.updateProfiles(profileImage, updateProfileRequest.updateProfileRequest)

    override suspend fun updateTeam(requestDto: UpdateTeamRequestDto): BaseResponse<Boolean> =
        mypageService.updateTeam(requestDto)
}
