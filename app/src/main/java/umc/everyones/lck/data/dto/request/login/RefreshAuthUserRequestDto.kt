package umc.everyones.lck.data.dto.request.login

data class RefreshAuthUserRequestDto(
    val kakaoUserId: String,
    val refreshToken: String
)