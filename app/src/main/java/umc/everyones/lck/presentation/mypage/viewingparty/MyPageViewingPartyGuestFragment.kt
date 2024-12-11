// MyPageViewingPartyGuestFragment.kt
package umc.everyones.lck.presentation.mypage.viewingparty

import android.app.Activity
import android.widget.Toast
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
        _myViewingPartyParticipateRVA = MyViewingPartyParticipateRVA(
            readViewingParty = { id ->
                val action = MyPageViewingPartyFragmentDirections.actionMyPageViewingPartyFragmentToReadViewingPartyFragment(id, "shortLocationValue")
                findNavController().navigate(action) // Viewing Party 읽기 화면으로 이동
            },
            deleteViewingParty = { id ->
                deleteViewingParty(id) // 삭제 메소드 호출
                Toast.makeText(requireContext(), "참여가 취소되었습니다.", Toast.LENGTH_SHORT).show() // Toast 메시지
            }
        )
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

    private fun deleteViewingParty(id: Long) {
        viewModel.cancleGuestViewingPartyMypage(id) // ID를 사용하여 삭제 요청
        Toast.makeText(requireContext(), "참여가 취소되었습니다.", Toast.LENGTH_SHORT).show() // Toast 메시지
        refreshParticipateList() // 리스트 새로 고침 메서드 호출
    }
    companion object {
        private const val CATEGORY = "GUEST"
    }

    private fun refreshParticipateList() {
        // 새로운 데이터를 로드하는 메서드
        viewModel.fetchMypageViewingPartyParticipateList(0, 10) // 페이지 번호와 사이즈를 설정하여 호출
    }
}
