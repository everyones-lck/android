package umc.everyones.lck.util.network

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import umc.everyones.lck.data.dto.request.login.RefreshAuthUserRequestDto
import umc.everyones.lck.data.service.LoginService
import retrofit2.Retrofit
import umc.everyones.lck.data.datasource.login.LoginDataSource
import umc.everyones.lck.domain.repository.login.LoginRepository
import java.util.Date
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val loginDataSource: LoginDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 현재 저장된 액세스 토큰을 가져옵니다.
        val accessToken = sharedPreferences.getString("jwt", "") ?: ""

        // JWT 디코딩
        val jwt = JWT(accessToken)

        // 액세스 토큰의 유효 기간 확인
        if (jwt.isExpired(0)) { // 0은 현재 시간 기준
            Timber.d("액세스 토큰이 만료되었습니다.")
        } else {
            // 토큰이 유효한 경우 남은 시간 계산
            val expirationTime = Date(jwt.expiresAt!!.time) // 만료 시간
            val currentTime = Date() // 현재 시간
            val remainingTime = expirationTime.time - currentTime.time // 남은 시간 (밀리초)

            // 남은 시간을 초 단위로 변환
            val remainingSeconds = remainingTime / 1000
            Timber.d("액세스 토큰은 유효합니다. 남은 시간: $remainingSeconds 초")
        }

        // 요청을 새로 만듭니다.
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        // 요청을 진행합니다.
        var response = chain.proceed(request)

        // 401 Unauthorized 에러가 발생했을 경우
        if (response.code == 401) {
            // kakaoUserId를 SharedPreferences에서 가져옵니다.
            val kakaoUserId = sharedPreferences.getString("kakaoUserId", null)
            val refreshToken = sharedPreferences.getString("refreshToken", "") ?: ""

            // kakaoUserId가 없으면 refresh 요청을 하지 않습니다.
            if (kakaoUserId.isNullOrEmpty()) {
                Timber.e("Kakao User ID가 없습니다. 리프레시 요청을 수행하지 않습니다.")
                return response // 원래의 응답을 그대로 반환
            }

            // 리프레시 요청을 위한 DTO를 생성합니다.
            val refreshRequest = RefreshAuthUserRequestDto(kakaoUserId, refreshToken)

            // 리프레시 토큰을 사용하여 새로운 액세스 토큰을 요청합니다.
            val newAccessToken = runBlocking { refreshAccessToken(refreshRequest) }

            // 새로운 액세스 토큰이 성공적으로 발급되면 업데이트합니다.
            if (newAccessToken != null) {
                Timber.d("기존 액세스 토큰: $accessToken")
                Timber.d("새로운 액세스 토큰: $newAccessToken")

                // 새로운 액세스 토큰을 SharedPreferences에 저장합니다.
                sharedPreferences.edit().putString("jwt", newAccessToken).apply()

                // 새로운 토큰을 사용하여 원래 요청을 재시도합니다.
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $newAccessToken")
                    .build()
                response = chain.proceed(newRequest)
            } else {
                Timber.e("리프레시 토큰 요청 실패")
            }
        }
        return response
    }

    private suspend fun refreshAccessToken(request: RefreshAuthUserRequestDto): String? {
        // Retrofit을 함수로 통해 LoginService를 생성합니다.
        //val loginService = retrofitProvider().create(LoginService::class.java)
        val refreshResponse = loginDataSource.refresh(request)
        //loginDataSource.refresh(request)

        // 성공적으로 리프레시 되었는지 확인하고 액세스 토큰을 반환합니다.
        return if (refreshResponse.success) {
            refreshResponse.data.accessToken
        } else {
            null
        }
    }
}