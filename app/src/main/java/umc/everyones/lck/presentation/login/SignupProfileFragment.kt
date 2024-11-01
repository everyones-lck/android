package umc.everyones.lck.presentation.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.DialogMyteamConfirmBinding
import umc.everyones.lck.databinding.DialogProfileConfirmBinding
import umc.everyones.lck.databinding.FragmentSignupProfileBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


@AndroidEntryPoint
class SignupProfileFragment : BaseFragment<FragmentSignupProfileBinding>(R.layout.fragment_signup_profile) {

    private val viewModel: SignupViewModel by activityViewModels()
    private var profileImageUri: Uri? = null
    private val navigator by lazy { findNavController() }

    // PhotoPicker Launcher
    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            result.data?.data?.let { uri ->
                profileImageUri = uri
                binding.ivSignupProfilePicture.setImageURI(uri) // 선택한 이미지 미리보기
                viewModel.setProfileImageUri(uri) // ViewModel에 URI 저장
            }
        }
    }

    override fun initObserver() {
        viewModel.profileUri.observe(viewLifecycleOwner) { uri ->
            Timber.d("Observed Profile Image URI: $uri")
            uri?.let {
                binding.ivSignupProfilePicture.setImageURI(it)
            }
        }

        viewModel.nickName.observe(viewLifecycleOwner) { nickname ->
            Timber.d("닉네임: $nickname")
        }
    }

    override fun initView() {
        // UI 초기화 및 클릭 리스너 설정
        binding.ivSignupProfilePicture.setImageResource(android.R.color.transparent)

        // 프로필 이미지 추가 버튼 클릭 리스너
        binding.ivSignupProfilePlus.setOnSingleClickListener {
            openPhotoPicker() // 갤러리 열기
        }

        // 다음 버튼 클릭 리스너
        binding.ivSignupProfileNext.setOnSingleClickListener {
            if (profileImageUri != null) {
                navigateToSignupMyTeam() // 다음 화면으로 이동
            } else {
                showProfileConfirmDialog()
            }
        }

        // 프로필 이미지 클릭 리스너 (이미지 변경)
        binding.ivSignupProfilePicture.setOnSingleClickListener {
            openPhotoPicker() // 갤러리 열기
        }
    }

    private fun openPhotoPicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*" // 모든 이미지 타입 선택
        }
        photoPickerLauncher.launch(intent) // 갤러리 열기
    }


    // 선택한 이미지 URI를 처리하는 메서드
    private fun handleMediaUris(uris: List<Uri>) {
        // 첫 번째 이미지를 프로필 이미지로 설정
        val selectedUri = uris.firstOrNull()
        if (selectedUri != null) {
            profileImageUri = selectedUri // URI를 저장
            binding.ivSignupProfilePicture.setImageURI(selectedUri) // ImageView에 선택한 이미지 설정
            viewModel.setProfileImageUri(selectedUri) // ViewModel에 URI 저장
        }
    }

    private fun showProfileConfirmDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_profile_confirm, null)
        val dialogBinding = DialogProfileConfirmBinding.bind(dialogView)

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
            navigateToSignupMyTeam()
        }
    }

    private fun navigateToSignupMyTeam() {
        navigator.navigate(R.id.action_signupProfileFragment_to_signupMyteamFragment)
    }

}