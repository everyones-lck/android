package umc.everyones.lck.presentation.mypage

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageProfileWithdrawBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.login.LoginActivity
import umc.everyones.lck.util.extension.setOnSingleClickListener
import umc.everyones.lck.util.extension.showCustomSnackBar

@AndroidEntryPoint
class MyPageProfileWithdrawFragment : BaseFragment<FragmentMypageProfileWithdrawBinding>(R.layout.fragment_mypage_profile_withdraw) {

    private val myPageViewModel: MyPageViewModel by activityViewModels()

    override fun initObserver() {
        myPageViewModel.withdrawResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Timber.d("회원 탈퇴 후 로그인 화면으로 이동")
                navigateToLoginScreen()
            } else {
                Timber.e("회원 탈퇴 실패, 오류 처리 필요")
                Toast.makeText(requireContext(), "계정 탈퇴에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun initView() {
        val text = """
            고객님께서 계정탈퇴를 원하신다니 저희 플랫폼의 서비스가 많이 부족하고 미흡했나 봅니다. 불편하셨던 점이나 불만사항을 알려주시면 적극 반영해서 고객님의 불편을 해결해 드리도록 노력하겠습니다.
            
            
            아울러 계정탈퇴 시, 아래 사항을 숙지하시기 바랍니다.
                
                
            회원 탈퇴 후 회원정보 및 서비스 이용기록은 모두 삭제되며 복원이 불가능합니다. 회원정보 및 채팅 기록 등의 개인형 서비스 이용기록은 모두 삭제되오니, 그 내용을 확인하시고 필요한 데이터는 미리 복원을 해주시기 바랍니다.
              
              
            회원 탈퇴 후에도 게시판형 서비스에 등록한 게시물은 삭제되지 않습니다. 커뮤니티 게시글 및 댓글, 뷰잉파티 개최글 등의 게시판형 서비스 이용기록은 자동 삭제되지 않으니, 삭제를 원하는 게시글이 있다면 반드시 회원 탈퇴 전에 삭제하시기 바랍니다. 회원 탈퇴 후에는 회원정보가 삭제됨에 따라 본인 여부를 확인할 수 있는 방법이 없어, 게시글을 임의로 삭제해드릴 수 없습니다. 이 점 유의해주시기 바랍니다.
            """.trimIndent()
        binding.tvMypageProfileWithdrawText1.text = text

        // 뒤로가기 버튼 클릭 시 이동
        binding.ivMypageProfileWithdrawBack.setOnSingleClickListener {
            findNavController().navigateUp()
        }

        binding.tvMypageProfileWithdraw.setOnSingleClickListener {
            myPageViewModel.withdraw()
        }
    }

    private fun navigateToLoginScreen() {
        showCustomSnackBar(binding.root, "계정 탈퇴 되었습니다")
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }, 2000)
    }
}