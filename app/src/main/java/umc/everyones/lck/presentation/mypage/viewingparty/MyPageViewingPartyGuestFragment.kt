// MyPageViewingPartyGuestFragment.kt
package umc.everyones.lck.presentation.mypage.viewingparty

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageViewingPartyGuestBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.extension.repeatOnStarted

@AndroidEntryPoint
class MyPageViewingPartyGuestFragment : BaseFragment<FragmentMypageViewingPartyGuestBinding>(R.layout.fragment_mypage_viewing_party_guest) {

    private val viewModel: MyPageViewingPartyViewModel by activityViewModels()
    private var _myViewingPartyParticipateRVA : MyViewingPartyParticipateRVA?=null
    private val myViewingPartyParticipateRVA get() = _myViewingPartyParticipateRVA

    private var readResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data?.getBooleanExtra("isReadMenuDone", false) == true) {
                myViewingPartyParticipateRVA?.refresh() // Refresh the list after reading a post
                binding.rvMypageViewingPartyGuest.scrollToPosition(0) // Scroll to the top
            }
        }
    }

    override fun initObserver() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.myPageParticipatePage.collectLatest { pagingData ->
                 myViewingPartyParticipateRVA?.submitData(pagingData) // PagingData를 어댑터에 제출
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            viewModel.categoryMypageRefresh.collect { categoryMypageRefresh ->
                Timber.d("GUEST", categoryMypageRefresh)
                if (categoryMypageRefresh == CATEGORY) {
                    myViewingPartyParticipateRVA?.refresh()
                    binding.rvMypageViewingPartyGuest.scrollToPosition(0)
                }
            }
        }
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvMypageViewingPartyGuest.layoutManager = layoutManager // RecyclerView에 레이아웃 매니저 설정
        initPostListRVA()
    }

    private fun initPostListRVA() {
        _myViewingPartyParticipateRVA = MyViewingPartyParticipateRVA { id ->
            val action = MyPageViewingPartyFragmentDirections.actionMyPageViewingPartyFragmentToReadViewingPartyFragment(id, "shortLocationValue")
            findNavController().navigate(action) // NavController를 사용하여 Fragment로 이동
        }
        binding.rvMypageViewingPartyGuest.adapter = myViewingPartyParticipateRVA

        _myViewingPartyParticipateRVA?.addLoadStateListener { combinedLoadStates ->
            with(binding){
                rvMypageViewingPartyGuest.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                noViewingPartyLayout.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading &&
                        combinedLoadStates.append.endOfPaginationReached &&
                        myViewingPartyParticipateRVA?.itemCount == 0
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myViewingPartyParticipateRVA = null // Prevent memory leak
    }

    companion object {
        private const val CATEGORY = "GUEST"
    }
}
