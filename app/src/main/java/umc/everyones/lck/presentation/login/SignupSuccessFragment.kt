package umc.everyones.lck.presentation.login

import android.content.Intent
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentSignupSuccessBinding
import umc.everyones.lck.presentation.MainActivity
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.TeamData.signupSuccessTeamBackground
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class SignupSuccessFragment : BaseFragment<FragmentSignupSuccessBinding>(R.layout.fragment_signup_success) {

    private val viewModel: SignupViewModel by activityViewModels()

    override fun initObserver() {
        // 닉네임을 반영하여 축하 메시지 설정
        viewModel.nickName.observe(viewLifecycleOwner) { nickName ->
            binding.tvSignupSuccessCongratulation.text = if (nickName.isNullOrEmpty()) {
                ""
            } else {
                "${nickName}님 가입을 축하드립니다!"
            }
        }

        // 프로필 이미지 URI 관찰
        viewModel.profileUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.ivSignupSuccessProfile.setImageURI(uri) // 프로필 이미지 설정
            } else {
                binding.ivSignupSuccessProfile.setImageResource(R.drawable.img_signup_profile) // 기본 이미지 설정
            }
        }

        // 팀 ID 관찰 및 배경 로고 설정
        viewModel.teamId.observe(viewLifecycleOwner) { teamId ->
            val backgroundResource = getSignupSuccessTeamLogo(teamId)
            binding.ivSignupSuccessBackgroundLogo.setImageResource(backgroundResource)
        }
    }

    override fun initView() {
        // Next 버튼 클릭 처리
        binding.ivSignupSuccessNext.setOnSingleClickListener {
            // 회원 가입 데이터 전송
            viewModel.sendSignupData() // 로그인 서비스 전달

            // 메인 액티비티로 이동
            Intent(requireContext(), MainActivity::class.java).apply {
                startActivity(this)
            }
            requireActivity().finish() // 현재 액티비티 종료
        }
    }

    private fun getSignupSuccessTeamLogo(teamId: Int): Int {
        return signupSuccessTeamBackground[teamId] ?: android.R.color.transparent
    }
}
