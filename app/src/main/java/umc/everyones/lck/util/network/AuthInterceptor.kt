package umc.everyones.lck.util.network

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import umc.everyones.lck.data.dto.request.login.RefreshAuthUserRequestDto
import umc.everyones.lck.data.datasource.login.LoginDataSource
import java.util.Date
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val loginDataSource: LoginDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = sharedPreferences.getString("jwt", "") ?: ""

        val jwt = JWT(accessToken)

        if (jwt.isExpired(0)) {
            Timber.d("액세스 토큰이 만료되었습니다.")
        } else {
            val expirationTime = Date(jwt.expiresAt!!.time) // 만료 시간
            val currentTime = Date() // 현재 시간
            val remainingTime = expirationTime.time - currentTime.time // 남은 시간 (밀리초)

            val remainingSeconds = remainingTime / 1000
            Timber.d("액세스 토큰은 유효합니다. 남은 시간: $remainingSeconds 초")
        }

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(request)

        if (response.code == 401) {
            val kakaoUserId = sharedPreferences.getString("kakaoUserId", null)
            val refreshToken = sharedPreferences.getString("refreshToken", "") ?: ""

            if (kakaoUserId.isNullOrEmpty()) {
                Timber.e("Kakao User ID가 없습니다. 리프레시 요청을 수행하지 않습니다.")
                return response
            }

            if (isRefreshTokenExpired(refreshToken)) {
                Timber.e("리프레시 토큰이 만료되었습니다.")
                return response
            }

            val refreshRequest = RefreshAuthUserRequestDto(kakaoUserId, refreshToken)

            val newAccessToken = runBlocking { refreshAccessToken(refreshRequest) }

            if (newAccessToken != null) {
                Timber.d("기존 액세스 토큰: $accessToken")
                Timber.d("새로운 액세스 토큰: $newAccessToken")

                sharedPreferences.edit().putString("jwt", newAccessToken).apply()

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

    private fun isRefreshTokenExpired(token: String?): Boolean {
        return token == null || JWT(token).isExpired(0)
    }

    private suspend fun refreshAccessToken(request: RefreshAuthUserRequestDto): String? {
        val refreshResponse = loginDataSource.refresh(request)

        return if (refreshResponse.success) {
            refreshResponse.data.accessToken
        } else {
            null
        }
    }
}
