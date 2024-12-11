// MyPageViewingPartyHostFragment.kt
package umc.everyones.lck.presentation.mypage.viewingparty

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageViewingPartyHostBinding
import umc.everyones.lck.domain.model.request.party.WriteViewingPartyModel
import umc.everyones.lck.domain.model.response.party.ReadViewingPartyModel
import umc.everyones.lck.presentation.MainActivity
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.party.read.ReadViewingPartyFragment
import umc.everyones.lck.presentation.party.read.ReadViewingPartyFragmentArgs
import umc.everyones.lck.presentation.party.read.ReadViewingPartyViewModel
import umc.everyones.lck.presentation.party.write.WriteViewingPartyActivity
import umc.everyones.lck.util.extension.repeatOnStarted
import umc.everyones.lck.util.extension.toWriteViewingPartyDateFormat
import umc.everyones.lck.util.network.UiState

@AndroidEntryPoint
class MyPageViewingPartyHostFragment : BaseFragment<FragmentMypageViewingPartyHostBinding>(R.layout.fragment_mypage_viewing_party_host) {

    private val viewModel: MyPageViewingPartyViewModel by activityViewModels()
    private val readViewModel : ReadViewingPartyViewModel by activityViewModels()
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
        _myViewingPartyHostRVA = MyViewingPartyHostRVA(
            readViewingParty = { id ->
                val action = MyPageViewingPartyFragmentDirections.actionMyPageViewingPartyFragmentToReadViewingPartyFragment(id, "shortLocationValue")
                findNavController().navigate(action) // Viewing Party 읽기 화면으로 이동
            },
            deleteViewingParty = { id ->
                deleteViewingParty(id)
            },
            onEditViewingParty = { id ->
                readViewModel.fetchViewingParty(id) // 뷰잉 파티 데이터를 가져옵니다.

                // 데이터를 가져온 후 UI 업데이트를 위해 StateFlow 수집
                viewLifecycleOwner.lifecycleScope.launch {
                    // fetchViewingParty의 결과를 기다림
                    readViewModel.readViewingPartyEvent.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                val viewingParty = readViewModel.viewingParty // 직접 응답을 가져옴
                                if (viewingParty != null) {
                                    readResultLauncher.launch(WriteViewingPartyActivity.editIntent(requireContext(), id,
                                        WriteViewingPartyModel(
                                            name = viewingParty.name,
                                            date = viewingParty.partyDate.toWriteViewingPartyDateFormat(),
                                            latitude = viewingParty.latitude,
                                            longitude = viewingParty.longitude,
                                            price = viewingParty.price.replace("₩", "").trim(),
                                            lowParticipate = viewingParty.participants.split("-")[0].trim(),
                                            highParticipate = viewingParty.participants.split("-")[1].replace(("[^\\d]").toRegex(), ""),
                                            qualify = viewingParty.qualify.replace("To.", ""),
                                            etc = viewingParty.etc,
                                            location = viewingParty.place,
                                            shortLocation = "" // shortLocation이 어떤 값인지 확인 필요
                                        )
                                    ))
                                } else {
                                    Toast.makeText(requireContext(), "데이터를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            is UiState.Failure -> {
                                Toast.makeText(requireContext(), "뷰잉파티를 조회하지 못했습니다.", Toast.LENGTH_SHORT).show()
                            }
                            is UiState.Loading -> {
                                // 로딩 상태 처리
                            }
                            is UiState.Empty -> {
                                Toast.makeText(requireContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        )
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

    private fun deleteViewingParty(id: Long) {
        viewModel.cancleHostViewingPartyMypage(id) // ID를 사용하여 삭제 요청
        Toast.makeText(requireContext(), "개최가 취소되었습니다.", Toast.LENGTH_SHORT).show() // Toast 메시지
        refreshParticipateList() // 리스트 새로 고침 메서드 호출
    }

    companion object {
        private const val CATEGORY = "HOST"
    }

    private fun refreshParticipateList() {
        // 새로운 데이터를 로드하는 메서드
        viewModel.fetchMypageViewingPartyHostList(0, 10) // 페이지 번호와 사이즈를 설정하여 호출
    }
}
