package umc.everyones.lck.domain.model.request.mypage

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import umc.everyones.lck.data.dto.request.mypage.UpdateProfilesRequestDto

data class  UpdateProfilesRequestModel(
    val profileImage: MultipartBody.Part,
    val updateProfileRequest: UpdateProfileRequestElementModel
) {
    data class UpdateProfileRequestElementModel(
        val nickname: String?,
        val defaultImage: Boolean
    ) {
        fun toUpdateProfileRequestElementDto() =
            Gson().toJson(this).toRequestBody("application/json".toMediaTypeOrNull())
    }
    fun toUpdateProfilesRequestDto() =
        UpdateProfilesRequestDto(profileImage, updateProfileRequest.toUpdateProfileRequestElementDto())
}