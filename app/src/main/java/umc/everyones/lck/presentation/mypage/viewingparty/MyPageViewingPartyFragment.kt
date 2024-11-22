package umc.everyones.lck.presentation.mypage.viewingparty

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageViewingPartyBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class MyPageViewingPartyFragment : BaseFragment<FragmentMypageViewingPartyBinding>(R.layout.fragment_mypage_viewing_party) {

    private val viewModel: MyPageViewingPartyViewModel by activityViewModels()
    private var _mypageViewingPartyListVPA:MyPageViewingPartyVPA?=null
    private val mypageViewingPartyListVPA get() = _mypageViewingPartyListVPA

    override fun initObserver() {
    }

    override fun initView() {
        binding.ivMypageViewingPartyBack.setOnSingleClickListener {
            findNavController().navigateUp()
        }
        initViewPager()
    }

    private fun initViewPager() {
        _mypageViewingPartyListVPA = MyPageViewingPartyVPA(this)
        with(binding){
            viewPager.adapter = mypageViewingPartyListVPA

            TabLayoutMediator(tabMypageViewingPartyGuestHost, viewPager){tab,position ->
                tab.text = tabTitles[position]
            }.attach()

            tabMypageViewingPartyGuestHost.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.refreshCategoryPage(tab?.text?.toString() ?: "HOST")
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabReselected(p0: TabLayout.Tab?) {

                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mypageViewingPartyListVPA = null
    }

    companion object {
        private val tabTitles = listOf("HOST", "GUEST")
    }

}
