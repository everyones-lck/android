package umc.everyones.lck.presentation.login

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import umc.everyones.lck.data.dto.request.login.RefreshAuthUserRequestDto
import umc.everyones.lck.data.dto.request.login.SignupAuthUserRequestDto
import umc.everyones.lck.domain.model.request.login.CommonLoginRequestModel
import umc.everyones.lck.domain.model.request.login.NicknameAuthUserRequestModel
import umc.everyones.lck.domain.model.response.login.LoginResponseModel
import umc.everyones.lck.domain.repository.login.LoginRepository
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val spf: SharedPreferences,
    application: Application,
    private val repository: LoginRepository
) : AndroidViewModel(application) {

    private val _kakaoUserId = MutableLiveData<String>()
    val kakaoUserId: LiveData<String> get() = _kakaoUserId

    private val _nickName = MutableLiveData<String>()
    val nickName: LiveData<String> get() = _nickName

    // 중복 체크 결과를 위한 LiveData 추가
    private val _isNicknameAvailable = MutableLiveData<Boolean>()
    val isNicknameAvailable: LiveData<Boolean> get() = _isNicknameAvailable

    private val _profileUri = MutableLiveData<Uri?>()
    val profileUri: LiveData<Uri?> get() = _profileUri

    private val _teamId = MutableLiveData<Int>()
    val teamId: LiveData<Int> get() = _teamId

    private val _signupResponse = MutableLiveData<Result<LoginResponseModel>?>()

    val signupResponse: LiveData<Result<LoginResponseModel>?> get() = _signupResponse

    private val _loginResult = MutableLiveData<LoginResponseModel?>()
    val loginResult: LiveData<LoginResponseModel?> get() = _loginResult

    private val context: Context = application

    fun setKakaoUserId(kakaoUserId: String) {
        _kakaoUserId.value = kakaoUserId
    }

    fun setNickName(nickName: String) {
        _nickName.value = nickName
    }

    fun setProfileImageUri(uri: Uri) {
        _profileUri.value = uri
    }

    fun setTeamId(teamId: Int) {
        _teamId.value = teamId
    }

    fun checkNicknameAvailability(nickName: String) {
        viewModelScope.launch {
            _isNicknameAvailable.value = false
            repository.nickname(NicknameAuthUserRequestModel(nickName))
                .onSuccess { isAvailable ->
                    _isNicknameAvailable.value = isAvailable // 가용성 결과 업데이트
                    _nickName.value = if (isAvailable) {
                        "Nickname is available"
                    } else {
                        "Nickname is already taken"
                    }
                }.onFailure { error ->
                    Timber.e(error) // 오류 로그
                    _nickName.value = "Failed to check nickname availability"
                }
        }
    }


    fun loginWithKakao(kakaoUserId: String) {
        viewModelScope.launch {
            if (kakaoUserId.isNullOrEmpty()) {
                Timber.d("Kakao User ID is missing, proceeding to signup.")
                sendSignupData() // 회원가입 로직으로 진행
                return@launch
            }

            _kakaoUserId.value = kakaoUserId
            val requestModel = CommonLoginRequestModel(kakaoUserId)
            repository.login(requestModel).onSuccess { response ->
                Timber.d(response.toString())
                spf.edit().apply {
                    putString("jwt", response.accessToken)
                    putString("refreshToken", response.refreshToken)
                    putBoolean("isLoggedIn", true)
                    putString("nickName", response.nickName)
                    apply()
                }
                _loginResult.value = response
            }.onFailure { error ->
                Timber.d(error.message.toString())
                Timber.d("$requestModel")
                _loginResult.value = null // 실패 시 null 설정
            }
        }
    }

    fun sendSignupData() {
        viewModelScope.launch {
            val signupRequest = prepareSignupRequest()

            Timber.d("Sending API request with data: ${signupRequest.signupUserData}")

            val gson = Gson()
            val signupUserDataJson = gson.toJson(signupRequest.signupUserData)
            repository.signup(
                signupUserDataJson.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()),
                signupRequest.profileImage
            ).onSuccess {
                spf.edit().apply {
                    putString("nickName", signupRequest.signupUserData.nickName)
                    putString("profileImage", _profileUri.value?.toString() ?: "") // URI가 null일 경우 빈 문자열로 저장
                    putString("role", signupRequest.signupUserData.role)
                    putInt("teamId", signupRequest.signupUserData.teamId)
                    putString("tier", signupRequest.signupUserData.tier)
                    putBoolean("isLoggedIn", true) // 로그인 상태 저장
                    apply()
                }
            }.onFailure {
                Timber.e("API call failed: ${it.message}")
            }
        }
    }

    private fun prepareSignupRequest(): SignupAuthUserRequestDto {
        val kakaoUserId = _kakaoUserId.value ?: throw IllegalArgumentException("Kakao User ID is required.")
        val nickName = _nickName.value ?: throw IllegalArgumentException("Nickname is required.")
        val role = "ROLE_USER"
        val teamId = _teamId.value ?: 1
        val tier = "Silver"

        val profileImagePart = try {
            val profileImageUri = _profileUri.value ?: throw IllegalArgumentException("Profile image URI is required.")
            val inputStream = context.contentResolver.openInputStream(profileImageUri)
            val selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
            val defaultImageStream = ByteArrayOutputStream().apply {
                selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
            }
            val defaultImageBytes = defaultImageStream.toByteArray()

            val requestBody = defaultImageBytes.toRequestBody("image/png".toMediaTypeOrNull()) // MIME 타입 구체화
            MultipartBody.Part.createFormData(
                "profileImage", "profile_image.png", requestBody
            )
        } catch (e: Exception) {
            Timber.e("Error creating MultipartBody.Part for profile image: ${e.message}")
            val emptyImageRequestBody = ByteArray(0).toRequestBody("image/png".toMediaTypeOrNull()) // 빈 이미지 요청
            MultipartBody.Part.createFormData(
                "profileImage", "profile_image.png", emptyImageRequestBody
            )
        }

        return SignupAuthUserRequestDto(
            profileImage = profileImagePart,
            signupUserData = SignupAuthUserRequestDto.SignupUserDataRequestDto(
                kakaoUserId = kakaoUserId,
                nickName = nickName,
                role = role,
                tier = tier,
                teamId = teamId
            )
        )
    }
}