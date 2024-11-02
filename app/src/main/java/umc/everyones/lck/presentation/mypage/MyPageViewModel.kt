package umc.everyones.lck.presentation.mypage

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.data.UpdateProfileRequest
import umc.everyones.lck.domain.model.request.mypage.UpdateProfilesRequestModel
import umc.everyones.lck.domain.model.request.mypage.UpdateTeamModel
import umc.everyones.lck.domain.model.response.mypage.InquiryProfilesModel
import umc.everyones.lck.domain.model.response.mypage.UpdateProfilesResponseModel
import umc.everyones.lck.domain.repository.MypageRepository
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val spf: SharedPreferences,
    application: Application,
    private val repository: MypageRepository
) : AndroidViewModel(application) {

    private val context: Context = getApplication<Application>().applicationContext

    private val _profileData = MutableLiveData<InquiryProfilesModel?>()
    val profileData: LiveData<InquiryProfilesModel?> get() = _profileData

    private val _withdrawResult = MutableLiveData<Boolean>()
    val withdrawResult: LiveData<Boolean> get() = _withdrawResult

    private val _logoutResult = MutableLiveData<Boolean>()
    val logoutResult: LiveData<Boolean> get() = _logoutResult

    private val _updateProfileResult = MutableLiveData<UpdateProfilesResponseModel?>()
    val updateProfileResult: LiveData<UpdateProfilesResponseModel?> get() = _updateProfileResult

    private val _nickName = MutableLiveData<String>()
    val nickName: LiveData<String> get() = _nickName

    private val _isNicknameAvailable = MutableLiveData<Boolean>()
    val isNicknameAvailable: LiveData<Boolean> get() = _isNicknameAvailable

    private val _profileUri = MutableLiveData<Uri>()
    val profileUri: LiveData<Uri> get() = _profileUri

    private val _existingNickname = MutableLiveData<String>()
    val existingNickname: LiveData<String> get() = _existingNickname

    private val _teamId = MutableLiveData<Int>()
    val teamId: LiveData<Int> get() = _teamId

    fun setProfileImageUri(uri: Uri) {
        _profileUri.value = uri
    }

    fun inquiryProfile() {
        viewModelScope.launch {
            repository.inquiryProfiles().onSuccess { response ->
                _profileData.value = response
                _nickName.value = response.nickname // 닉네임을 LiveData에 저장
                _teamId.value = response.teamId
                Timber.d(response.toString())
            }.onFailure {
                Timber.d(it.stackTraceToString())
            }
        }
    }

    fun withdraw() {
        viewModelScope.launch {
            repository.withdraw().onSuccess {response ->
                _withdrawResult.value = true
                Timber.d("회원 탈퇴 성공: $response")
                spf.edit().putBoolean("isLoggedIn", false).apply()
                clearAppCache()
            }.onFailure { error ->
                Timber.e("회원 탈퇴 실패: ${error.message}")
                _withdrawResult.value = false
            }
        }
    }

    fun logout() {
        val refreshToken = spf.getString("refreshToken", null)
        if (refreshToken != null) {
            viewModelScope.launch {
                repository.logout(refreshToken).onSuccess { response ->
                    _logoutResult.value = true
                    Timber.d("로그아웃 성공: $response")
                    spf.edit().putBoolean("isLoggedIn", false).apply()
                    clearAppCache()
                }.onFailure { error ->
                    _logoutResult.value = false
                    Timber.e("로그아웃 실패: ${error.message}")
                }
            }
        } else {
            Timber.e("refreshToken이 없습니다.")
            _logoutResult.value = false
        }
    }

    fun updateProfile(nickName: String?, profileImageUri: Uri?) {
        viewModelScope.launch {
            val currentProfileImageUri = _profileUri.value
                ?: Uri.parse("android.resource://${getApplication<Application>().packageName}/${R.drawable.img_signup_profile}") // 기본 이미지 URI
            val currentNickName = _nickName.value ?: ""

            val isProfileImageChanged = profileImageUri != null && profileImageUri != currentProfileImageUri

            val profileImagePart: MultipartBody.Part = if (isProfileImageChanged) {
                createProfileImagePart(profileImageUri!!) // !! 연산자를 사용하여 null이 아님을 보장
            } else {
                createProfileImagePart(currentProfileImageUri) // 기존 이미지를 사용
            }

            val updateProfileRequestModel = UpdateProfilesRequestModel(
                profileImage = profileImagePart,
                updateProfileRequest = UpdateProfilesRequestModel.UpdateProfileRequestElementModel(
                    nickname = nickName ?: null, // 닉네임이 null이면 null로 보내줌
                    defaultImage = !isProfileImageChanged // 이미지가 수정되지 않았으면 기본 이미지로 설정
                )
            )

            // 프로필 업데이트 호출 및 결과 처리
            repository.updateProfiles(updateProfileRequestModel.profileImage, updateProfileRequestModel)
                .onSuccess { response ->
                    Timber.d("Profile updated successfully: $response")
                    spf.edit().apply {
                        putString("nickName", updateProfileRequestModel.updateProfileRequest.nickname)
                        val imageToSave = if (profileImageUri != null && profileImageUri.toString().isNotEmpty()) {
                            profileImageUri.toString()
                        } else {
                            currentProfileImageUri.toString() // 기존 URI 저장
                        }
                        putString("profileImage", imageToSave)
                        apply()
                    }
            }.onFailure { error ->
                Timber.e("Error updating profile: ${error.message}")
            }
        }
    }
    private fun createProfileImagePart(uri: Uri): MultipartBody.Part {
        val inputStream = getApplication<Application>().contentResolver.openInputStream(uri)
        val selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream().apply {
            selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
        }
        val imageBytes = byteArrayOutputStream.toByteArray()

        val requestBody = RequestBody.create(null, imageBytes) // MediaType 필요 없음
        return MultipartBody.Part.createFormData("profileImage", "profile_image.png", requestBody)
    }



    fun updateTeam(teamId: Int) {
        viewModelScope.launch {
            repository.updateTeam(UpdateTeamModel(teamId)).onSuccess { response ->
                Timber.d("팀 변경 성공: $response")
                _teamId.value = teamId
            }.onFailure { error ->
                Timber.d("팀 변경 실패: $error")
            }
        }
    }

    private fun clearAppCache() {
        val cacheDir = context.cacheDir
        val profileImageFile = File(context.filesDir, "profileImage.jpg")
        if (cacheDir.isDirectory) {
            deleteDir(cacheDir)
        }
        if (profileImageFile.exists()) {
            profileImageFile.delete()
        }
    }

    private fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    deleteDir(File(dir, child))
                }
            }
        }
        return dir.delete()
    }
}