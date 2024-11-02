package umc.everyones.lck.presentation.mypage

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageMainBinding
import umc.everyones.lck.presentation.MainActivity
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.TeamData
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMypageMainBinding>(R.layout.fragment_mypage_main) {

    private val viewModel: MyPageViewModel by activityViewModels()
    private val teamLogos = TeamData.mypageTeamBackground
    private val navigator by lazy { findNavController() }

    override fun initObserver() {
        viewModel.profileData.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.tvMypageMainNickname.text = it.nickname // 닉네임 설정
                binding.tvMypageMainTier.text = it.tier // 티어 설정

                // 팀 로고 설정
                val teamBackgroundResId = teamLogos[it.teamId] ?: R.drawable.img_mypage_empty_background
                binding.ivMypageMainTeamBackground.setImageResource(teamBackgroundResId)

                loadProfileImage(it.profileImageUrl) // 프로필 이미지 로드

                updateTierUI(it.tier)
            }
        }
    }

    override fun initView() {

        binding.ivMypageMainBack.setOnSingleClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            navigator.navigateUp()
        }

        binding.tvMypageMainMyprofileText.setOnSingleClickListener {
            navigator.navigate(R.id.action_myPageFragment_to_myPageProfileFragment)
        }

        binding.tvMypageMainMyteamText.setOnSingleClickListener {
            navigator.navigate(R.id.action_myPageFragment_to_myPageMyteamFragment)
        }

        binding.tvMypageMainCommunityText.setOnSingleClickListener {
            navigator.navigate(R.id.action_myPageFragment_to_myPageCommunityFragment)
        }

        binding.tvMypageMainViewingPartyText.setOnSingleClickListener {
            navigator.navigate(R.id.action_myPageFragment_to_myPageViewingPartyFragment)
        }

        binding.ivMypageMainSetting.setOnSingleClickListener {
            navigator.navigate(R.id.action_myPageFragment_to_myPageInformationFragment)
        }

        viewModel.inquiryProfile()
    }

    private fun loadProfileImage(uri: String?) {
        uri?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.img_signup_profile) // 기본 이미지
                .into(binding.ivMypageMainProfile) // 프로필 이미지 뷰에 로드
        } ?: run {
            binding.ivMypageMainProfile.setImageResource(R.drawable.img_signup_profile) // 기본 이미지 설정
        }
    }

    private fun updateTierUI(tier: String) {

        val tierStyles = mapOf(
            "bronze" to R.style.TextAppearance_Bronze,
            "Silver" to R.style.TextAppearance_Silver,
            "Gold" to R.style.TextAppearance_Gold,
            "Master" to R.style.TextAppearance_Master,
            "Challenger" to R.style.TextAppearance_Challenger
        )

        binding.tvMypageMainTier.setTextAppearance(requireContext(), tierStyles[tier]!!)

        binding.tvMypageMainTier.text = tier
    }
}
