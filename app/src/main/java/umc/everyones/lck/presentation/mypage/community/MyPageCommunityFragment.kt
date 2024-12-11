package umc.everyones.lck.presentation.mypage.community

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageCommunityBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.community.adapter.PostListVPA
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class MyPageCommunityFragment : BaseFragment<FragmentMypageCommunityBinding>(R.layout.fragment_mypage_community) {
    private val viewModel: MyPageCommunityViewModel by activityViewModels()
    private var _mypagePostListVPA: MyPageCommunityVPA? = null
    private val mypagePostListVPA get() = _mypagePostListVPA
    override fun initObserver() {
    }

    override fun initView() {
        binding.ivMypageCommunityBack.setOnSingleClickListener {
            findNavController().navigateUp()
        }
        initViewPager()
    }

    private fun initViewPager() {
        _mypagePostListVPA = MyPageCommunityVPA(this)
        with(binding) {
            viewPager.adapter = mypagePostListVPA

            TabLayoutMediator(tabMypageCommunityPostComment, viewPager) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()

            tabMypageCommunityPostComment.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.refreshCategoryPage(tab?.text?.toString() ?: "MY POST")
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
        _mypagePostListVPA = null
    }

    companion object {
        private val tabTitles = listOf("MY POST", "MY COMMENT")
    }
}
