package umc.everyones.lck.presentation.login

import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.DialogNicknameConfirmBinding
import umc.everyones.lck.databinding.DialogSignupTosDetailsAgree1Binding
import umc.everyones.lck.databinding.DialogSignupTosDetailsAgree2Binding
import umc.everyones.lck.databinding.FragmentSignupTosBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class SignupTosFragment : BaseFragment<FragmentSignupTosBinding>(R.layout.fragment_signup_tos) {
    //
    private val navigator by lazy { findNavController() }

    private var isAgree0Checked = false
    private var isAgree1Checked = false
    private var isAgree2Checked = false

    override fun initObserver() {

    }

    override fun initView() {

        binding.tvSignupTosDetailsAgree1.setOnSingleClickListener {
            showDetailsDialog1()
        }

        binding.tvSignupTosDetailsAgree2.setOnSingleClickListener {
            showDetailsDialog2()
        }

        binding.ivSignupTosNext.setOnSingleClickListener {
            if (!isAgree1Checked || !isAgree2Checked) {
                Toast.makeText(requireContext(), "모든 동의 항목을 체크해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                navigateToSignupNickname()
            }
        }

        binding.viewSignupTosAgreeAllCircle.setOnSingleClickListener {
            toggleAgreement(0)
        }

        binding.viewSignupTosAgree1Circle.setOnSingleClickListener {
            toggleAgreement(1)
        }

        binding.viewSignupTosAgree2Circle.setOnSingleClickListener {
            toggleAgreement(2)
        }
    }

    private fun showDetailsDialog1() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_signup_tos_details_agree_1, null)

        val dialogBinding = DialogSignupTosDetailsAgree1Binding.bind(dialogView)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val layoutParams = dialog.window?.attributes
        layoutParams?.dimAmount = 0.8f
        dialog.window?.attributes = layoutParams

        dialogBinding.btnConfirm.setOnSingleClickListener {
            dialog.dismiss()
            isAgree1Checked = false
            toggleAgreement(1)
        }
    }

    private fun showDetailsDialog2() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_signup_tos_details_agree_2, null)

        val dialogBinding = DialogSignupTosDetailsAgree2Binding.bind(dialogView)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val layoutParams = dialog.window?.attributes
        layoutParams?.dimAmount = 0.8f
        dialog.window?.attributes = layoutParams

        dialogBinding.btnConfirm.setOnSingleClickListener {
            dialog.dismiss()
            isAgree2Checked = false
            toggleAgreement(2)
        }
    }

    private fun navigateToSignupNickname() {
        navigator.navigate(R.id.action_signupTosFragment_to_signupNicknameFragment)
    }

    private fun toggleAgreement(agreementNumber: Int) {
        if (agreementNumber == 0) {
            val newState = !isAgree0Checked
            isAgree0Checked = newState
            isAgree1Checked = newState
            isAgree2Checked = newState
        } else if (agreementNumber == 1) {
            isAgree1Checked = !isAgree1Checked
        } else if (agreementNumber == 2) {
            isAgree2Checked = !isAgree2Checked
        }
        isAgree0Checked = isAgree1Checked && isAgree2Checked

        updateCircleView(binding.viewSignupTosAgreeAllCircle, isAgree0Checked)
        updateCircleView(binding.viewSignupTosAgree1Circle, isAgree1Checked)
        updateCircleView(binding.viewSignupTosAgree2Circle, isAgree2Checked)
    }

    private fun updateCircleView(view: View, isChecked: Boolean) {
        view.background = if (isChecked) {
            ContextCompat.getDrawable(requireContext(), R.drawable.shape_selected_oval)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.shape_oval)
        }
    }
}