package umc.everyones.lck.data.datasourceImpl.community

import umc.everyones.lck.data.datasource.community.CommunityDataSource
import umc.everyones.lck.data.dto.BaseResponse
import umc.everyones.lck.data.dto.request.community.CreateCommentRequestDto
import umc.everyones.lck.data.dto.request.community.EditCommunityRequestDto
import umc.everyones.lck.data.dto.request.community.WriteCommunityRequestDto
import umc.everyones.lck.data.dto.response.NonBaseResponse
import umc.everyones.lck.data.dto.response.community.CommunityListResponseDto
import umc.everyones.lck.data.dto.response.community.EditCommunityResponseDto
import umc.everyones.lck.data.dto.response.community.ReadCommunityResponseDto
import umc.everyones.lck.data.dto.response.community.WriteCommunityResponseDto
import umc.everyones.lck.data.service.community.CommunityService
import javax.inject.Inject

class CommunityDataSourceImpl @Inject constructor(
    private val communityService: CommunityService
): CommunityDataSource {
    override suspend fun fetchCommunityList(
        postType: String,
        page: Int,
        size: Int
    ): BaseResponse<CommunityListResponseDto> =
        communityService.fetchCommunityList(postType, page, size)

    override suspend fun writeCommunityPost(
        request: WriteCommunityRequestDto
    ): BaseResponse<WriteCommunityResponseDto> =
        communityService.writeCommunityPost(request.files, request.request)

    override suspend fun fetchCommunityPost(postId: Long): BaseResponse<ReadCommunityResponseDto> =
        communityService.fetchCommunityPost(postId)

    override suspend fun deleteCommunityPost(postId: Long): NonBaseResponse =
        communityService.deleteCommunityPost(postId)

    override suspend fun editCommunityPost(
        postId: Long,
        request: EditCommunityRequestDto
    ): BaseResponse<EditCommunityResponseDto> =
        communityService.editCommunityPost(postId, request)

    override suspend fun reportCommunityPost(postId: Long): NonBaseResponse =
        communityService.reportCommunityPost(postId)

    override suspend fun reportCommunityComment(commentId: Long): NonBaseResponse =
        communityService.reportCommunityComment(commentId)

    override suspend fun createComment(postId: Long, request: CreateCommentRequestDto): NonBaseResponse =
        communityService.createComment(postId, request)

    override suspend fun deleteComment(commentId: Long): NonBaseResponse =
        communityService.deleteCommunityComment(commentId)

}