package umc.everyones.lck.presentation.login

import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.everyones.lck.R
import umc.everyones.lck.databinding.DialogMyteamConfirmBinding
import umc.everyones.lck.databinding.FragmentSignupMyteamBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.TeamData
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class SignupMyteamFragment : BaseFragment<FragmentSignupMyteamBinding>(R.layout.fragment_signup_myteam) {

    private val viewModel: SignupViewModel by activityViewModels()
    private val navigator by lazy { findNavController() }
    private var selectedTeamId: Int? = 1

    override fun initObserver() {

    }

    override fun initView() {

        setupTeamSelection()

        binding.ivSignupMyteamNext.setOnSingleClickListener {
            if (selectedTeamId == null) {
                showTeamConfirmDialog()
            } else {
                navigateToSuccessFragment()
            }
        }
    }

    private fun setupTeamSelection() {
        TeamData.teamLogos.forEach { (imageViewId, teamId) ->
            val imageView = binding.root.findViewById<ImageView>(imageViewId)
            imageView.setOnClickListener {
                selectedTeamId = if (selectedTeamId == teamId) {
                    null // 선택된 팀을 다시 클릭하면 선택 해제
                } else {
                    teamId // 클릭한 팀 ID로 설정
                }
                updateTeamSelectionUI()

                // 팀이 선택되지 않았을 경우 1로 설정
                val teamIdToSet = selectedTeamId ?: 1 // 선택된 팀이 없으면 기본값 1
                viewModel.setTeamId(teamIdToSet) // ViewModel에 팀 ID 설정
            }
        }
    }

    private fun updateTeamSelectionUI() {
        TeamData.teamLogos.forEach { (imageViewId, teamId) ->
            val imageView = binding.root.findViewById<ImageView>(imageViewId)
            val drawableRes = if (teamId == selectedTeamId) { // selectedTeamId로 변경
                R.drawable.shape_team_background_selected
            } else {
                R.drawable.shape_team_background
            }
            imageView.background = ContextCompat.getDrawable(requireContext(), drawableRes)
        }
    }

    private fun showTeamConfirmDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_myteam_confirm, null)
        val dialogBinding = DialogMyteamConfirmBinding.bind(dialogView)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val layoutParams = dialog.window?.attributes
        layoutParams?.dimAmount = 0.8f
        dialog.window?.attributes = layoutParams

        dialogBinding.btnChange.setOnSingleClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnSingleClickListener {
            dialog.dismiss()
            navigateToSuccessFragment()
        }
    }

    private fun navigateToSuccessFragment() {
        lifecycleScope.launch {
            navigator.navigate(R.id.action_signupMyteamFragment_to_signupSuccessFragment)
        }
    }
}