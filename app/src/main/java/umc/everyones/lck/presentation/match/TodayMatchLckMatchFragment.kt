package umc.everyones.lck.presentation.match

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentTodayMatchLckMatchBinding
import umc.everyones.lck.domain.model.response.match.TodayMatchInformationModel
import umc.everyones.lck.domain.model.todayMatch.LckMatch
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.match.adapter.LckMatchContentRVA

@AndroidEntryPoint
class TodayMatchLckMatchFragment : BaseFragment<FragmentTodayMatchLckMatchBinding>(R.layout.fragment_today_match_lck_match) {
    private val viewModel: TodayMatchLckMatchViewModel by activityViewModels()
    private lateinit var lckMatchContentRVA: LckMatchContentRVA
    override fun initObserver() {
        // ViewModel의 matchData를 관찰하여 데이터 변경 시 UI를 업데이트
        viewModel.matchData.observe(viewLifecycleOwner, Observer { matchData ->
            if (matchData == null || matchData.matchResponses.isEmpty()) {
                // 경기가 없을 때
                binding.layoutTodayMatchLckMatchNoMatch.visibility = View.VISIBLE
                binding.rvTodayMatchLckMatchContainer.visibility = View.GONE
            } else {
                // 경기가 있을 때
                binding.layoutTodayMatchLckMatchNoMatch.visibility = View.GONE
                binding.rvTodayMatchLckMatchContainer.visibility = View.VISIBLE
                updateMatchContent(matchData.matchResponses)
            }
        })
    }

    override fun initView() {
        Timber.d("TodayMatchLckMatchFragment initView called")
        viewModel.fetchTodayMatchInformation()
    }
    private fun updateMatchContent(matchResponses: List<TodayMatchInformationModel.MatchResponsesModel>) {
        // match-id를 전달하도록 수정
        lckMatchContentRVA = LckMatchContentRVA(
            matchResponses,
            onPredictClick = { matchId ->
                val bundle = Bundle().apply {
                    putLong("matchId", matchId)
                }
                findNavController().navigate(R.id.todayMatchPredictionFragment, bundle)
            },
            onPogClick = { matchId ->
                val bundle = Bundle().apply {
                    putLong("matchId", matchId)
                }
                findNavController().navigate(R.id.todayMatchTodayPogFragment, bundle)
            }
        )
        binding.rvTodayMatchLckMatchContainer.layoutManager = LinearLayoutManager(context)
        binding.rvTodayMatchLckMatchContainer.adapter = lckMatchContentRVA
    }

}