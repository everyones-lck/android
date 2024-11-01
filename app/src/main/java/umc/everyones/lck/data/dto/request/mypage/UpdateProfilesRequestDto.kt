package umc.everyones.lck.data.dto.request.mypage

import okhttp3.MultipartBody
import okhttp3.RequestBody
import umc.everyones.lck.data.UpdateProfileRequest

data class UpdateProfilesRequestDto(
    val profileImage: MultipartBody.Part,
    val updateProfileRequest: RequestBody
) {
    data class UpdateProfileRequestElementDto(
        val nickname: String?,
        val defaultImage: Boolean
    )
}
