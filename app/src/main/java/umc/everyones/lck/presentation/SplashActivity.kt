package umc.everyones.lck.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.presentation.login.LoginActivity
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var spf: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            val refreshToken = spf.getString("refreshToken", null) // 리프레시 토큰 가져오기
            val isLoggedIn = spf.getBoolean("isLoggedIn", false) // SharedPreferences에서 로그인 상태 가져오기

            val intent = when {
                !isLoggedIn -> Intent(this, LoginActivity::class.java) // 로그인되지 않은 상태라면 로그인 화면으로
                isRefreshTokenExpired(refreshToken) -> Intent(this, LoginActivity::class.java) // 리프레시 토큰이 만료된 경우 로그인 화면으로
                else -> Intent(this, MainActivity::class.java) // 로그인된 상태라면 홈 화면으로
            }

            startActivity(intent)
            finish()
        }, 2000) // 2초 후 전환
    }

    private fun isRefreshTokenExpired(token: String?): Boolean {
        if (token == null) return true // 토큰이 null인 경우 만료로 간주
        val jwt = JWT(token)

        return jwt.isExpired(0) // 0은 현재 시간 기준
    }
}
