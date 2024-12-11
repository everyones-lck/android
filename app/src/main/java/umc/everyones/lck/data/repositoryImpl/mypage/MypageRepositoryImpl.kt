package umc.everyones.lck.data.repositoryImpl.mypage

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import umc.everyones.lck.data.datasource.MypageDataSource
import umc.everyones.lck.data.datasourceImpl.mypage.HostPagingSource
import umc.everyones.lck.data.datasourceImpl.mypage.MyCommentPagingSource
import umc.everyones.lck.data.datasourceImpl.mypage.MyPostPagingSource
import umc.everyones.lck.data.datasourceImpl.mypage.ParticipatePagingSource
import umc.everyones.lck.data.dto.request.mypage.UpdateProfilesRequestDto
import umc.everyones.lck.data.dto.response.NonBaseResponse
import umc.everyones.lck.data.service.MypageService
import umc.everyones.lck.domain.model.request.mypage.UpdateProfilesRequestModel
import umc.everyones.lck.domain.model.request.mypage.UpdateTeamModel
import umc.everyones.lck.domain.model.response.mypage.CommentsMypageModel
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.InquiryProfilesModel
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.domain.model.response.mypage.ParticipateViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.PostsMypageModel
import umc.everyones.lck.domain.model.response.mypage.UpdateProfilesResponseModel
import umc.everyones.lck.domain.repository.MypageRepository
import javax.inject.Inject

class MypageRepositoryImpl  @Inject constructor(
    private val mypageDataSource: MypageDataSource,
    private val mypageService: MypageService
): MypageRepository {
    override suspend fun inquiryProfiles(): Result<InquiryProfilesModel> =
        runCatching { mypageDataSource.inquiryProfiles().data.toInquiryProfilesModel() }

    override suspend fun postsMypage(page: Int, size: Int): Result<PostsMypageModel> =
        runCatching { mypageDataSource.postsProfiles(page, size).data.toPostsMypageModel() }

    override suspend fun commentsMypage(page: Int, size: Int): Result<CommentsMypageModel> =
        runCatching { mypageDataSource.commentsProfiles(page, size).data.toCommentsMypageModel() }

    override suspend fun participateViewingPartyMypage(
        page: Int,
        size: Int
    ): Result<ParticipateViewingPartyMypageModel> =
        runCatching {
            mypageDataSource.participateViewingPartyMypage(
                page,
                size
            ).data.toParticipateViewingPartyMypageModel()
        }

    override suspend fun hostViewingPartyMypage(
        page: Int,
        size: Int
    ): Result<HostViewingPartyMypageModel> =
        runCatching {
            mypageDataSource.hostViewingPartyMypage(
                page,
                size
            ).data.toHostViewingPartyMypageModel()
        }

    override suspend fun cancelParticipateViewingPartyMypage(viewingPartyId: Long): Result<Boolean> =
        runCatching { mypageDataSource.cancelParticipateViewingPartyMypage(viewingPartyId).data }

    override suspend fun cancelHostViewingPartyMypage(viewingPartyId: Long): Result<Boolean> =
        runCatching { mypageDataSource.cancelHostViewingPartyMypage(viewingPartyId).data }

    override suspend fun logout(refreshToken: String): Result<NonBaseResponse> =
        runCatching { mypageDataSource.logout(refreshToken) }

    override suspend fun withdraw(): Result<NonBaseResponse> =
        runCatching { mypageDataSource.withdraw() }

    override suspend fun updateProfiles(
        profileImage: MultipartBody.Part,
        requestModel: UpdateProfilesRequestModel
    ): Result<UpdateProfilesResponseModel> =
        runCatching {
            mypageDataSource.updateProfiles(
                profileImage,
                requestModel.toUpdateProfilesRequestDto()
            ).data.toUpdateProfilesResponseModel()
        }

    override suspend fun updateTeam(request: UpdateTeamModel): Result<Boolean> =
        runCatching { mypageDataSource.updateTeam(request.toUpdateTeamRequestDto()).data }

    override fun fetchPostPagingSource(category: String): Flow<PagingData<PostsMypageModel.PostsMypageElementModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { MyPostPagingSource(mypageService, category) }
        ).flow

    override fun fetchCommentPagingSource(category: String): Flow<PagingData<CommentsMypageModel.CommentsMypageElementModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { MyCommentPagingSource(mypageService, category) }
        ).flow

    override fun fetchHostPagingSource(category: String): Flow<PagingData<HostViewingPartyMypageModel.HostViewingPartyMypageElementModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { HostPagingSource(mypageService, category) }
        ).flow

    override fun fetchParticipatePagingSource(category: String): Flow<PagingData<ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {ParticipatePagingSource(mypageService, category)}
        ).flow
}