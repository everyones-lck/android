package umc.everyones.lck.domain.repository

import okhttp3.MultipartBody
import umc.everyones.lck.data.dto.response.NonBaseResponse
import umc.everyones.lck.domain.model.request.mypage.UpdateProfilesRequestModel
import umc.everyones.lck.domain.model.request.mypage.UpdateTeamModel
import umc.everyones.lck.domain.model.response.mypage.CommentsMypageModel
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.InquiryProfilesModel
import umc.everyones.lck.domain.model.response.mypage.ParticipateViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.PostsMypageModel
import umc.everyones.lck.domain.model.response.mypage.UpdateProfilesResponseModel

interface MypageRepository {
    suspend fun inquiryProfiles(): Result<InquiryProfilesModel>

    suspend fun postsMypage(size: Int, page: Int): Result<PostsMypageModel>

    suspend fun commentsMypage(size: Int, page: Int): Result<CommentsMypageModel>

    suspend fun participateViewingPartyMypage(size: Int, page: Int): Result<ParticipateViewingPartyMypageModel>

    suspend fun hostViewingPartyMypage(size: Int, page: Int): Result<HostViewingPartyMypageModel>

    suspend fun cancelParticipateViewingPartyMypage(viewingPartyId: Int): Result<Boolean>

    suspend fun cancelHostViewingPartyMypage(viewingPartyId: Int): Result<Boolean>

    suspend fun logout(refreshToken: String): Result<NonBaseResponse>

    suspend fun withdraw(): Result<NonBaseResponse>

    suspend fun updateProfiles(profileImage: MultipartBody.Part, requestModel: UpdateProfilesRequestModel): Result<UpdateProfilesResponseModel>

    suspend fun updateTeam(request: UpdateTeamModel): Result<Boolean>

}