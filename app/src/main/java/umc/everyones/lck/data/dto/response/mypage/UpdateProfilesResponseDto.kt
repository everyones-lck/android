package umc.everyones.lck.data.dto.response.mypage

import umc.everyones.lck.domain.model.response.mypage.UpdateProfilesResponseModel

data class UpdateProfilesResponseDto(
    val updatedProfileImageUrl: String,
    val updatedNickname: String
){
    fun toUpdateProfilesResponseModel() =
        UpdateProfilesResponseModel(updatedProfileImageUrl, updatedNickname)
}
