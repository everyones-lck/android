// MyPageViewingPartyHostFragment.kt
package umc.everyones.lck.presentation.mypage.viewingparty

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageViewingPartyHostBinding
import umc.everyones.lck.presentation.MainActivity
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.party.read.ReadViewingPartyFragment
import umc.everyones.lck.util.extension.repeatOnStarted

@AndroidEntryPoint
class MyPageViewingPartyHostFragment : BaseFragment<FragmentMypageViewingPartyHostBinding>(R.layout.fragment_mypage_viewing_party_host) {

    private val viewModel: MyPageViewingPartyViewModel by activityViewModels()
    private var _myViewingPartyHostRVA : MyViewingPartyHostRVA?=null
    private val myViewingPartyHostRVA get() = _myViewingPartyHostRVA

    private var readResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data?.getBooleanExtra("isReadMenuDone", false) == true) {
                myViewingPartyHostRVA?.refresh() // Refresh the list after reading a post
                binding.rvMypageViewingPartyHost.scrollToPosition(0) // Scroll to the top
            }
        }
    }

    override fun initObserver() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.myPageHostPage.collectLatest { pagingData ->
                myViewingPartyHostRVA?.submitData(pagingData) // PagingData를 어댑터에 제출
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            viewModel.categoryMypageRefresh.collect { categoryMypageRefresh ->
                Timber.d("HOST", categoryMypageRefresh)
                if (categoryMypageRefresh == CATEGORY) {
                    myViewingPartyHostRVA?.refresh()
                    binding.rvMypageViewingPartyHost.scrollToPosition(0)
                }
            }
        }
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvMypageViewingPartyHost.layoutManager = layoutManager // RecyclerView에 레이아웃 매니저 설정
        initPostListRVA()
    }

    private fun initPostListRVA() {
        _myViewingPartyHostRVA = MyViewingPartyHostRVA { id ->
            val action = MyPageViewingPartyFragmentDirections.actionMyPageViewingPartyFragmentToReadViewingPartyFragment(id, "shortLocationValue")
            findNavController().navigate(action) // NavController를 사용하여 Fragment로 이동
        }
        binding.rvMypageViewingPartyHost.adapter = myViewingPartyHostRVA

        _myViewingPartyHostRVA?.addLoadStateListener { combinedLoadStates ->
            with(binding) {
                rvMypageViewingPartyHost.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                noViewingPartyLayout.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading &&
                        combinedLoadStates.append.endOfPaginationReached &&
                        myViewingPartyHostRVA?.itemCount == 0
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myViewingPartyHostRVA = null // Prevent memory leak
    }

    companion object {
        private const val CATEGORY = "HOST"
    }
}
