package umc.everyones.lck.presentation.mypage.community

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPageCommunityVPA(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2  // 총 2개의 탭

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyPageCommunityPostFragment()  // 첫 번째 탭에 표시할 Fragment
            1 -> MyPageCommunityCommentFragment()  // 두 번째 탭에 표시할 Fragment
            else -> throw IllegalStateException("Invalid position: $position")
        }

    }


}
