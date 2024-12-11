package umc.everyones.lck.presentation.mypage.viewingparty

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPageViewingPartyVPA(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2 // 탭의 수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyPageViewingPartyHostFragment()
            1 -> MyPageViewingPartyGuestFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}
