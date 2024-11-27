package umc.everyones.lck.presentation.match

import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentTodayMatchLckPogBinding
import umc.everyones.lck.domain.model.response.match.CommonTodayMatchPogModel
import umc.everyones.lck.domain.model.todayMatch.LckPog
import umc.everyones.lck.domain.model.todayMatch.Player
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.match.adapter.LckPogMatchRVA
import umc.everyones.lck.presentation.match.adapter.LckPogPlayerRVA

@AndroidEntryPoint
class TodayMatchLckPogFragment : BaseFragment<FragmentTodayMatchLckPogBinding>(R.layout.fragment_today_match_lck_pog) {
    private val viewModel: TodayMatchLckPogViewModel by viewModels()
    private val todayViewModel: TodayMatchLckMatchViewModel by activityViewModels()
    private lateinit var lckPogMatchRVA: LckPogMatchRVA
    private var tabIndex = 0

    override fun initObserver() {
        // 세트 수를 받아와서 탭 레이아웃 설정
        viewModel.setCount.observe(viewLifecycleOwner) { setCountModel ->
            setupRecyclerView(setCountModel.setCount) // setCount 변경 시 RecyclerView 초기화
            Timber.d("Set Count: ${setCountModel.setCount}")
        }

//        viewModel.pogData.observe(viewLifecycleOwner) { response ->
//            response?.let {
//                lckPogMatchRVA.updatePlayers(listOf(CommonTodayMatchPogModel(it.seasonInfo, it.matchNumber, it.matchDate, it.setPogResponses, it.matchPogResponse, tabIndex)))
//                Timber.d("POG Data: $response")
//            }
//        }

        // pogData를 누적 저장할 리스트
        val pogDataList = mutableListOf<CommonTodayMatchPogModel>()

        // ViewModel의 pogData를 관찰하여 데이터를 누적
        viewModel.pogData.distinctUntilChanged().observe(viewLifecycleOwner) { response ->
            response?.let {
                // 새로운 데이터를 리스트에 추가
                val newData = CommonTodayMatchPogModel(
                    seasonInfo = it.seasonInfo,
                    matchNumber = it.matchNumber,
                    matchDate = it.matchDate,
                    setPogResponses = it.setPogResponses,
                    matchPogResponse = it.matchPogResponse,
                    tabIndex = tabIndex
                )
                // 중복 확인 후 추가
                if (pogDataList.none { data -> data.matchNumber == newData.matchNumber }) {
                    pogDataList.add(newData)
                }

                // matchNumber로 정렬
                val sortedList = pogDataList.sortedBy { data -> data.matchNumber }

                // 어댑터에 정렬된 리스트 전달
                lckPogMatchRVA.submitList(sortedList)
                Timber.d("Updated Sorted POG Data List: $sortedList")

            }
        }

        // ViewModel의 matchData를 관찰하여 matchId를 가져와서 사용
        todayViewModel.matchData.observe(viewLifecycleOwner) { matchData ->
            if (matchData == null || matchData.matchResponses.isEmpty()) {
                // 경기가 없을 때
                binding.layoutTodayMatchPogNoMatch.visibility = View.VISIBLE
                binding.rvTodayMatchLckPogContainer.visibility = View.GONE
            } else {
                // 경기가 있을 때
                binding.layoutTodayMatchPogNoMatch.visibility = View.GONE
                binding.rvTodayMatchLckPogContainer.visibility = View.VISIBLE

//                val matchId = matchData.matchResponses.firstOrNull()?.matchId ?: return@observe
//
//                // 세트 수를 가져오기 위해 matchId를 사용
//                viewModel.fetchTodayMatchSetCount(matchId)
//                Timber.d("Match ID: $matchId")
//
//                // 초기 데이터 로드
//                viewModel.fetchTodayMatchPog(matchId)

                // 모든 matchId 가져오기
                val matchIds = matchData.matchResponses.map { it.matchId }

                // 각 matchId에 대해 작업 수행
                matchIds.forEach { matchId ->
                    viewModel.fetchTodayMatchSetCount(matchId)
                    viewModel.fetchTodayMatchPog(matchId)
                    Timber.d("Match ID: $matchId")
                }
            }
        }
    }

    override fun initView() {

    }
    private fun setupRecyclerView(newSetCount: Int) {
        if (!::lckPogMatchRVA.isInitialized) {
            // 어댑터가 초기화되지 않았을 때만 생성
            lckPogMatchRVA = LckPogMatchRVA(
                setCount = newSetCount,
                onTabSelected = tabIndex
            )
            binding.rvTodayMatchLckPogContainer.layoutManager = LinearLayoutManager(context)
            binding.rvTodayMatchLckPogContainer.adapter = lckPogMatchRVA
            binding.rvTodayMatchLckPogContainer.itemAnimator = null
        }
    }
}