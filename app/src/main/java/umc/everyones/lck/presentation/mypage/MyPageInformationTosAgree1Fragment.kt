package umc.everyones.lck.presentation.mypage

import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageInformationTosAgree1Binding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class MyPageInformationTosAgree1Fragment : BaseFragment<FragmentMypageInformationTosAgree1Binding>(R.layout.fragment_mypage_information_tos_agree_1) {

    private val navigator by lazy { findNavController() }

    override fun initObserver() {

    }

    override fun initView() {

        binding.ivMypageInformationTosAgree1Back.setOnSingleClickListener {
            navigator.navigateUp()
        }

    }

}