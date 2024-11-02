package umc.everyones.lck.presentation.login

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.DialogMyteamConfirmBinding
import umc.everyones.lck.databinding.DialogNicknameConfirmBinding
import umc.everyones.lck.databinding.FragmentSignupNicknameBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class SignupNicknameFragment : BaseFragment<FragmentSignupNicknameBinding>(R.layout.fragment_signup_nickname) {

    private val viewModel: SignupViewModel by activityViewModels()
    private val navigator by lazy { findNavController() }

    override fun initObserver() {
        viewModel.isNicknameAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                binding.viewSignupNicknameBar.setBackgroundResource(R.drawable.shape_rect_4_green_line)
                binding.tvSignupNicknameDuplication.setTextColor(requireContext().getColor(R.color.success))
                binding.tvSignupNicknameDuplication.setBackgroundResource(R.drawable.shape_rect_12_green_line)
                binding.layoutSignupNicknameWarning4.visibility = View.GONE
                binding.layoutSignupNicknameValid.visibility = View.VISIBLE
                showNicknameConfirmDialog()
            } else {
                binding.layoutSignupNicknameWarning4.visibility = View.VISIBLE // 중복
                binding.viewSignupNicknameBar.setBackgroundResource(R.drawable.shape_rect_4_red_line)
                binding.tvSignupNicknameDuplication.setTextColor(requireContext().getColor(R.color.warning))
                binding.tvSignupNicknameDuplication.setBackgroundResource(R.drawable.shape_rect_12_red_line)
            }
        }
    }

    override fun initView() {
        setInitialState() // 초기 상태 설정

        binding.etSignupNicknameName.doOnTextChanged { text, _, _, _ ->
            val nickname = text.toString()
            val isDuplicated = false // 여기에 실제 중복 확인 로직을 추가해야 합니다.
            val isValid = validateNickname(nickname) // 유효성 검사 추가

            if (isValid) {
                // 유효한 닉네임일 경우
                if (!isDuplicated) {
                    binding.tvSignupNicknameDuplication.setTextColor(requireContext().getColor(R.color.white)) // 기본 색상
                    binding.tvSignupNicknameDuplication.setBackgroundResource(R.drawable.shape_rect_12_white_line) // 기본 배경
                    binding.tvSignupNicknameDuplication.setOnClickListener {
                        viewModel.checkNicknameAvailability(nickname) // 중복 확인 로직 호출
                    }
                    binding.layoutSignupNicknameWarning4.visibility = View.GONE // 중복 아님
                    binding.tvSignupNicknameDuplication.isEnabled = true // 버튼 활성화
                } else {
                    setNicknameUnavailableState() // 중복 상태 설정
                    binding.tvSignupNicknameDuplication.isEnabled = false // 버튼 비활성화
                }
            } else {
                // 유효하지 않은 경우
                setNicknameUnavailableState() // 닉네임이 비어 있을 때 상태 설정
                binding.tvSignupNicknameDuplication.isEnabled = false // 버튼 비활성화
            }
        }
    }

    private fun showNicknameConfirmDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nickname_confirm, null)
        val dialogBinding = DialogNicknameConfirmBinding.bind(dialogView)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val layoutParams = dialog.window?.attributes
        layoutParams?.dimAmount = 0.8f
        dialog.window?.attributes = layoutParams

        dialogBinding.btnConfirm.setOnClickListener {
            val nickname = binding.etSignupNicknameName.text.toString() // 닉네임 가져오기
            if (nickname.isNotEmpty()) {
                viewModel.setNickName(nickname)
            }
            dialog.dismiss()
            navigateToSignupProfile()
        }
    }

    private fun setInitialState() {
        binding.tvSignupNicknameDuplication.setTextColor(requireContext().getColor(R.color.nickname_gray)) // 회색
        binding.tvSignupNicknameDuplication.setBackgroundResource(R.drawable.shape_rect_12_gray_line) // 회색 배경
        binding.layoutSignupNicknameWarning4.visibility = View.GONE // 초기 경고 숨기기
    }

    private fun setNicknameUnavailableState() {
        binding.tvSignupNicknameDuplication.setTextColor(requireContext().getColor(R.color.nickname_gray)) // 회색
        binding.tvSignupNicknameDuplication.setBackgroundResource(R.drawable.shape_rect_12_gray_line) // 회색 배경
    }

    private fun validateNickname(nickname: String): Boolean {
        var isValid = true
        // 닉네임 유효성 검사
        if (nickname.isEmpty()) {
            binding.layoutSignupNicknameWarning1.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.layoutSignupNicknameWarning1.visibility = View.GONE
        }
        if (nickname.length > 10) {
            binding.layoutSignupNicknameWarning2.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.layoutSignupNicknameWarning2.visibility = View.GONE
        }
        if (nickname.contains(" ")) {
            binding.layoutSignupNicknameWarning3.visibility = View.VISIBLE
            isValid = false
        } else {
            binding.layoutSignupNicknameWarning3.visibility = View.GONE
        }
        return isValid
    }

    private fun navigateToSignupProfile() {
        navigator.navigate(R.id.action_signupNicknameFragment_to_signupProfileFragment)
    }
}