package umc.everyones.lck.domain.model.request.login

import umc.everyones.lck.data.dto.request.login.CommonLoginRequestDto
import umc.everyones.lck.data.dto.request.login.RefreshAuthUserRequestDto
import java.io.Serializable

data class RefreshAuthUserRequestModel(
    val kakaoUserId: String,
    val refreshToken: String
):Serializable {
    fun toRefreshAuthUserRequestDto() =
        RefreshAuthUserRequestDto(kakaoUserId, refreshToken)
}
