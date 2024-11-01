package umc.everyones.lck.data.datasourceImpl

import okhttp3.MultipartBody
import okhttp3.RequestBody
import umc.everyones.lck.data.datasource.login.LoginDataSource
import umc.everyones.lck.data.dto.BaseResponse
import umc.everyones.lck.data.dto.request.login.CommonLoginRequestDto
import umc.everyones.lck.data.dto.request.login.NicknameAuthUserRequestDto
import umc.everyones.lck.data.dto.request.login.RefreshAuthUserRequestDto
import umc.everyones.lck.data.dto.response.login.LoginResponseDto
import umc.everyones.lck.data.service.LoginService
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(
    private val loginService: LoginService
): LoginDataSource {
    override suspend fun signup(
        signupUserData: RequestBody,
        profileImage: MultipartBody.Part
    ): BaseResponse<LoginResponseDto> =
        loginService.signup(signupUserData, profileImage)

    override suspend fun login(requestDto: CommonLoginRequestDto):BaseResponse<LoginResponseDto> = loginService.login(requestDto)

    override suspend fun refresh(requestDto: RefreshAuthUserRequestDto):BaseResponse<LoginResponseDto> = loginService.refresh(requestDto)

    override suspend fun nickname(requestDto: NicknameAuthUserRequestDto): BaseResponse<Boolean> = loginService.nickname(requestDto.nickName)

    override suspend fun usertest(token: String): BaseResponse<Unit> = loginService.usertest()
}