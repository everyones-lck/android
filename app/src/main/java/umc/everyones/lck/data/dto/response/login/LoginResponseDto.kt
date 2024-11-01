package umc.everyones.lck.data.dto.response.login

import umc.everyones.lck.domain.model.response.login.LoginResponseModel

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpirationTime: String,
    val refreshTokenExpirationTime: String,
    val nickName: String
){
    fun toLoginResponseDto() =
        LoginResponseModel(accessToken, refreshToken, accessTokenExpirationTime, refreshTokenExpirationTime, nickName)
}