package umc.everyones.lck.data.dto.request.community

import okhttp3.MultipartBody
import okhttp3.RequestBody


data class WriteCommunityRequestDto(
    val files: List<MultipartBody.Part?>,
    val request: RequestBody
) {
    data class WriteRequestDto(
        val postType: String,
        val postTitle: String,
        val postContent: String
    )
}