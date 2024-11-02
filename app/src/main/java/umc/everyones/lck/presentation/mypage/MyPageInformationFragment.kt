package umc.everyones.lck.presentation.mypage

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageInformationBinding
import umc.everyones.lck.presentation.MainActivity
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class MyPageInformationFragment : BaseFragment<FragmentMypageInformationBinding>(R.layout.fragment_mypage_information) {

    private val navigator by lazy { findNavController() }

    override fun initObserver() {

    }

    override fun initView() {

        binding.ivMypageInformationBack.setOnSingleClickListener {
            navigator.navigateUp()
        }

        binding.tvMypageInformationTosAgree1Text.setOnSingleClickListener {
            navigator.navigate(R.id.action_myPageInformationFragment_to_MypageInformationTosAgree1Fragment)
        }

        binding.tvMypageInformationTosAgree2Text.setOnSingleClickListener {
            navigator.navigate(R.id.action_myPageInformationFragment_to_MypageInformationTosAgree2Fragment)
        }

        binding.tvMypageInformationOss.setOnSingleClickListener {
            startActivity(Intent(requireContext(),OssLicensesMenuActivity::class.java))
        }
    }
}