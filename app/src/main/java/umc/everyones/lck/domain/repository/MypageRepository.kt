package umc.everyones.lck.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import umc.everyones.lck.data.dto.response.NonBaseResponse
import umc.everyones.lck.domain.model.request.mypage.UpdateProfilesRequestModel
import umc.everyones.lck.domain.model.request.mypage.UpdateTeamModel
import umc.everyones.lck.domain.model.response.mypage.CommentsMypageModel
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.InquiryProfilesModel
import umc.everyones.lck.domain.model.response.mypage.MyPost
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

    fun fetchPostPagingSource(category: String): Flow<PagingData<PostsMypageModel.PostsMypageElementModel>>

    fun fetchCommentPagingSource(category: String): Flow<PagingData<CommentsMypageModel.CommentsMypageElementModel>>
}