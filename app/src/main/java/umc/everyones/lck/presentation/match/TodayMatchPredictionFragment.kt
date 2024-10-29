package umc.everyones.lck.presentation.match

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentTodayMatchPredictionBinding
import umc.everyones.lck.domain.model.response.match.MatchTodayMatchModel
import umc.everyones.lck.domain.model.todayMatch.LckMatch
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.match.adapter.MatchPredictionRVA
import umc.everyones.lck.util.extension.setOnSingleClickListener
import umc.everyones.lck.util.extension.showCustomSnackBar
import umc.everyones.lck.util.extension.toOrdinal

@AndroidEntryPoint
class TodayMatchPredictionFragment : BaseFragment<FragmentTodayMatchPredictionBinding>(R.layout.fragment_today_match_prediction) {
    private val viewModel: TodayMatchPredictionViewModel by viewModels()
    private lateinit var matchPredictionRVA: MatchPredictionRVA
    private var selectedTeam: Int? = null // 사용자가 선택한 팀 ID를 저장할 변수

    override fun initObserver() {
        viewModel.matchData.observe(viewLifecycleOwner, Observer { matchData ->
            matchData?.let {
                // seasonName과 서수를 포함한 matchNumber 설정
                binding.tvTodayMatchPredictionDate.text = "${it.seasonName} ${it.matchNumber.toOrdinal()} Match"

                // RecyclerView 설정
                setupRecyclerView(it)
            }
        })
    }

    override fun initView() {
        goBackButton()
        matchVoteButton()

        val matchId = arguments?.getLong("matchId") ?: return
        viewModel.fetchTodayMatchVoteMatch(matchId)
    }

    private fun goBackButton() {
        binding.ivTodayMatchPredictionBack.setOnSingleClickListener {
            findNavController().navigateUp()
        }
    }
    private fun matchVoteButton() {
        binding.tvTodayMatchPredictionVote.setOnSingleClickListener {
            val matchId = arguments?.getLong("matchId") ?: return@setOnSingleClickListener
            selectedTeam?.let { teamId ->
                viewModel.voteMatch(matchId, teamId)
            } ?: run {
                showCustomSnackBar(binding.root, "투표할 팀을 선택해주세요.")
            }
        }
        // voteResponse를 관찰하여 메시지를 표시
        viewModel.voteResponse.observe(viewLifecycleOwner) { message ->
            showCustomSnackBar(binding.root, message)
        }
    }

    private fun setupRecyclerView(matchData: MatchTodayMatchModel) {
        matchPredictionRVA = MatchPredictionRVA { selectedTeam ->
            // 사용자가 선택한 팀을 처리할 수 있습니다.
            this.selectedTeam = selectedTeam // 선택한 팀 ID를 저장
        }
        matchPredictionRVA.submitList(listOf(matchData))
        binding.rvTodayMatchPrediction.adapter = matchPredictionRVA

    }
}