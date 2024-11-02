package umc.everyones.lck.presentation.mypage.community

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageCommunityBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class MyPageCommunityFragment : BaseFragment<FragmentMypageCommunityBinding>(R.layout.fragment_mypage_community) {
    private val viewModel: MyPageCommunityViewModel by activityViewModels()

    override fun initObserver() {
        // 관찰자가 필요 없으면 여전히 비워둘 수 있습니다.
    }

    override fun initView() {
        // 뒤로가기 버튼 클릭 리스너 설정
        binding.ivMypageCommunityBack.setOnSingleClickListener {
            findNavController().navigateUp()
        }

        // 뷰페이저 및 탭 레이아웃 설정 함수 호출
        initViewPager()
    }

    private fun initViewPager() {
        // 뷰페이저에 어댑터 설정
        val pagerAdapter = MyPageCommunityPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // 탭 레이아웃과 뷰페이저를 연결
        TabLayoutMediator(binding.tabMypageCommunityPostComment, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "My Post"
                1 -> "My Comment"
                else -> throw IllegalStateException("Invalid position: $position")
            }
        }.attach()

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            if (posts.isEmpty()) {
                binding.ivMypageCommunityStar.visibility = View.VISIBLE
            }
        }
    }
}
