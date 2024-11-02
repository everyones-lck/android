package umc.everyones.lck.presentation.mypage

import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageInformationBinding
import umc.everyones.lck.databinding.FragmentMypageInformationTosAgree2Binding
import umc.everyones.lck.databinding.FragmentMypageProfileBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class MyPageInformationTosAgree2Fragment : BaseFragment<FragmentMypageInformationTosAgree2Binding>(R.layout.fragment_mypage_information_tos_agree_2) {

    private val navigator by lazy { findNavController() }

    override fun initObserver() {

    }

    override fun initView() {

        binding.ivMypageInformationTosAgree2Back.setOnSingleClickListener {
            navigator.navigateUp()
        }

    }
}