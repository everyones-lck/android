package umc.everyones.lck.presentation.mypage.viewingparty

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageViewingPartyBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

class MyPageViewingPartyFragment : BaseFragment<FragmentMypageViewingPartyBinding>(R.layout.fragment_mypage_viewing_party) {

    private val viewModel: MyPageViewingPartyViewModel by viewModels()

    override fun initView() {
        setupViewPager()
        setupTabLayout()

        binding.ivMypageViewingPartyBack.setOnSingleClickListener {
            findNavController().navigateUp()
        }
    }

    override fun initObserver() {

    }

    private fun setupViewPager() {
        val adapter = MyPageViewingPartyPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabMypageViewingPartyGuestHost, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Guest"
                1 -> "Host"
                else -> throw IllegalStateException("Unexpected position: $position")
            }
        }.attach()
    }
}
