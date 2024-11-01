package umc.everyones.lck.domain.repository.login

import okhttp3.MultipartBody
import okhttp3.RequestBody
import umc.everyones.lck.domain.model.request.login.CommonLoginRequestModel
import umc.everyones.lck.domain.model.request.login.NicknameAuthUserRequestModel
import umc.everyones.lck.domain.model.request.login.RefreshAuthUserRequestModel
import umc.everyones.lck.domain.model.response.login.LoginResponseModel

interface LoginRepository {
    suspend fun signup(signupUserData:RequestBody, profileImage: MultipartBody.Part): Result<LoginResponseModel>

    suspend fun login(request: CommonLoginRequestModel): Result<LoginResponseModel>

    suspend fun refresh(request: RefreshAuthUserRequestModel): Result<LoginResponseModel>

    suspend fun nickname(request: NicknameAuthUserRequestModel): Result<Boolean>

    suspend fun usertest(token: String): Result<Unit>
}
