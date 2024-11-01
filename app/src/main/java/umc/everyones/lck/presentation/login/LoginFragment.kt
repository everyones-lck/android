package umc.everyones.lck.presentation.login

import android.content.Context
import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentLoginBinding
import umc.everyones.lck.domain.model.response.login.LoginResponseModel
import umc.everyones.lck.presentation.MainActivity
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: SignupViewModel by activityViewModels()
    private val navigator by lazy { findNavController() }

    override fun initObserver() {
        // 관찰자 설정 코드 필요 시 추가
    }

    override fun initView() {
        with(binding) {
            ivLoginKakao.setOnSingleClickListener {
                val context: Context = requireContext()
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    loginWithKakaoTalk()
                } else {
                    loginWithKakaoAccount()
                }
            }
        }
    }

    private fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
            if (error != null) {
                Timber.e("카카오톡으로 로그인 실패", error)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    // 사용자가 로그인 과정을 취소한 경우
                    return@loginWithKakaoTalk
                }
                // 카카오 계정으로 로그인 시도
                loginWithKakaoAccount()
            } else if (token != null) {
                Timber.i("카카오톡으로 로그인 성공 ${token.accessToken}")
                handleLoginSuccess()
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(requireContext()) {token, error ->
            if (error != null) {
                Timber.e("카카오 계정으로 로그인 실패", error)
            } else if (token != null) {
                Timber.i("카카오 계정으로 로그인 성공 ${token.accessToken}")
                handleLoginSuccess()
            }
        }
    }

    private fun handleLoginSuccess() {
        UserApiClient.instance.me { user, error ->
            user?.let {
                val kakaoUserId = it.id.toString()
                Timber.d("로그인 결과: $kakaoUserId") // 결과 로깅

                // ViewModel에 사용자 ID 설정
                viewModel.setKakaoUserId(kakaoUserId)

                // 로그인 요청
                viewModel.loginWithKakao(kakaoUserId)

                // 로그인 결과 관찰
                viewModel.loginResult.observe(viewLifecycleOwner) { loginResponse ->
                    val userInfo = convertToCommonLoginResponseModel(loginResponse)
                    handleLoginResult(userInfo) // 로그인 결과에 따라 화면 전환
                }
            } ?: run {
                Timber.e("사용자 정보가 null입니다.")
                navigateToSignupNicknameScreen() // 회원가입 화면으로 이동
            }
        }
    }

    // LoginResponseModel을 CommonLoginResponseModel으로 변환하는 메서드
    private fun convertToCommonLoginResponseModel(response: LoginResponseModel?): LoginResponseModel? {
        return response?.let {
            LoginResponseModel(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken,
                accessTokenExpirationTime = it.accessTokenExpirationTime,
                refreshTokenExpirationTime = it.refreshTokenExpirationTime,
                nickName = it.nickName
            )
        }
    }


    private fun handleLoginResult(userInfo: LoginResponseModel?) {
        if (userInfo != null) {
            navigateToMainScreen()
        } else {
            navigateToSignupNicknameScreen()
        }
    }

    private fun navigateToMainScreen() {
        Intent(requireContext(), MainActivity::class.java).apply {
            startActivity(this)
            requireActivity().finish() // 현재 액티비티 종료
        }
    }

    private fun navigateToSignupNicknameScreen() {
        navigator.navigate(R.id.action_loginFragment_to_signupTosFragment)
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}
