package umc.everyones.lck.data.repositoryImpl.login

import okhttp3.MultipartBody
import okhttp3.RequestBody
import umc.everyones.lck.data.datasource.login.LoginDataSource
import umc.everyones.lck.domain.model.request.login.CommonLoginRequestModel
import umc.everyones.lck.domain.model.request.login.NicknameAuthUserRequestModel
import umc.everyones.lck.domain.model.request.login.RefreshAuthUserRequestModel
import umc.everyones.lck.domain.model.response.login.LoginResponseModel
import umc.everyones.lck.domain.repository.login.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginDataSource: LoginDataSource
): LoginRepository{
    override suspend fun signup(
        signupUserData: RequestBody,
        profileImage: MultipartBody.Part
    ): Result<LoginResponseModel> =
        runCatching { loginDataSource.signup(signupUserData, profileImage).data.toLoginResponseDto() }

    override suspend fun login(request:CommonLoginRequestModel): Result<LoginResponseModel> =
        runCatching { loginDataSource.login(request.toCommonLoginRequestDto()).data.toLoginResponseDto()}

    override suspend fun refresh(request: RefreshAuthUserRequestModel): Result<LoginResponseModel> =
        runCatching { loginDataSource.refresh(request.toRefreshAuthUserRequestDto()).data.toLoginResponseDto() }

    override suspend fun nickname(request: NicknameAuthUserRequestModel): Result<Boolean> =
        runCatching {loginDataSource.nickname(request.toNicknameAuthUserRequestDto()).data}

    override suspend fun usertest(token: String): Result<Unit> =
        runCatching { loginDataSource.usertest(token)
            Unit }
}
