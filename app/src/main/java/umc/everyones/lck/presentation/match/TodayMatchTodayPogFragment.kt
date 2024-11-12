package umc.everyones.lck.presentation.match

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentTodayMatchTodayPogBinding
import umc.everyones.lck.domain.model.response.match.PogPlayerTodayMatchModel
import umc.everyones.lck.domain.model.todayMatch.TodayPog
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.match.adapter.TodayPogPlayerRVA
import umc.everyones.lck.util.extension.setOnSingleClickListener
import umc.everyones.lck.util.extension.showCustomSnackBar
import umc.everyones.lck.util.extension.toOrdinal

@AndroidEntryPoint
class TodayMatchTodayPogFragment : BaseFragment<FragmentTodayMatchTodayPogBinding>(R.layout.fragment_today_match_today_pog) {
    private val viewModel: TodayMatchTodayPogViewModel by viewModels()
    private val todayViewModel: TodayMatchPredictionViewModel by activityViewModels()
    private lateinit var todayPogPlayerRVA1: TodayPogPlayerRVA
    private lateinit var todayPogPlayerRVA2: TodayPogPlayerRVA
    private lateinit var todayPogPlayerRVA3: TodayPogPlayerRVA
    private lateinit var todayPogPlayerRVA4: TodayPogPlayerRVA
    private lateinit var todayPogPlayerRVA5: TodayPogPlayerRVA
    private lateinit var todayPogPlayerRVAMatch: TodayPogPlayerRVA
    override fun initObserver() {
        // 세트 수에 따라 레이아웃의 visibility를 조정
        viewModel.setCount.observe(viewLifecycleOwner) { setCountModel ->
            adjustLayoutVisibility(setCountModel.setCount)
        }
        // POG 데이터가 변경될 때마다 각 setIndex에 맞게 업데이트
        viewModel.matchPogData.observe(viewLifecycleOwner) { pogPlayerData ->
            // Match POG 데이터 업데이트
            val matchPogData = pogPlayerData.matchPogVoteCandidate.information
            updateMatchRecyclerView(matchPogData)

            // Set POG 데이터 업데이트
            pogPlayerData.setPogVoteCandidates.forEach { setPogVoteCandidate ->
                val setIndex = setPogVoteCandidate.setIndex
                val setPogData = setPogVoteCandidate.information
                updateRecyclerView(setIndex, setPogData)
            }
        }

//        // 모든 항목이 선택되었을 때 투표 버튼 활성화
//        viewModel.allItemsSelected.observe(viewLifecycleOwner) { allSelected ->
//            binding.tvTodayMatchTodayPogVote.isEnabled = allSelected
//        }
        // 투표 결과 메시지 스낵바로 표시
        viewModel.voteResponse.observe(viewLifecycleOwner) { message ->
            showCustomSnackBar(binding.root, message)
        }
        // todayViewModel의 matchData를 관찰하여 seasonInfo로 텍스트 업데이트
        todayViewModel.matchData.observe(viewLifecycleOwner, Observer { matchData ->
            matchData?.let {
                // seasonName과 서수를 포함한 matchNumber 설정
                binding.tvTodayMatchTodayPogDate.text = "${it.seasonName} ${it.matchNumber.toOrdinal()} Match"
                Timber.d("Season %s", it.seasonName)
            }
        })
    }

    override fun initView() {
        goBackButton()
        pogVoteButton()
        setupRecyclerView()
        setupVoteImageViewClick()

        val matchId = arguments?.getLong("matchId") ?: return
        viewModel.fetchTodayMatchSetCount(matchId)
        Timber.d("TodayMatchTodayPogFragment matchId: $matchId") // matchId 로그 출력
        todayViewModel.fetchTodayMatchVoteMatch(matchId)
        viewModel.fetchTodayMatchPogPlayer(matchId)
    }

    private fun goBackButton() {
        binding.ivTodayMatchTodayPogBack.setOnSingleClickListener {
            findNavController().navigateUp()
        }
    }
    private fun pogVoteButton() {
        binding.tvTodayMatchTodayPogVote.setOnSingleClickListener {
            val matchId = arguments?.getLong("matchId") ?: return@setOnSingleClickListener
            // 세트 POG 투표
            viewModel.setCount.value?.setCount?.let { setCount ->
                val selectedSetPlayers = viewModel.selectedSetPlayers.value ?: return@setOnSingleClickListener
                for (setIndex in 1..setCount) {
                    val playerId = selectedSetPlayers[setIndex] ?: return@setOnSingleClickListener
                    viewModel.voteSetPog(matchId, setIndex, playerId)
                }
            }
            // 매치 POG 투표
            val selectedMatchPlayer = viewModel.selectedMatchPlayer.value ?: return@setOnSingleClickListener
            viewModel.voteMatchPog(matchId, selectedMatchPlayer)

        }
    }
    // RecyclerView의 아이템을 업데이트하는 함수
    private fun updateRecyclerView(setIndex: Int, information: List<PogPlayerTodayMatchModel.InformationModel>) {
        when (setIndex) {
            1 -> todayPogPlayerRVA1.submitList(information)
            2 -> todayPogPlayerRVA2.submitList(information)
            3 -> todayPogPlayerRVA3.submitList(information)
            4 -> todayPogPlayerRVA4.submitList(information)
            5 -> todayPogPlayerRVA5.submitList(information)
        }
    }
    // 매치 POG RecyclerView의 아이템을 업데이트하는 함수
    private fun updateMatchRecyclerView(information: List<PogPlayerTodayMatchModel.InformationModel>) {
        todayPogPlayerRVAMatch.submitList(information)
    }
    private fun setupRecyclerView() {
        todayPogPlayerRVA1 = TodayPogPlayerRVA { playerId -> viewModel.selectSetPlayer(1, playerId) }
        binding.rvTodayMatchTodayPog1stVote.adapter = todayPogPlayerRVA1

        todayPogPlayerRVA2 = TodayPogPlayerRVA { playerId -> viewModel.selectSetPlayer(2, playerId) }
        binding.rvTodayMatchTodayPog2ndVote.adapter = todayPogPlayerRVA2

        todayPogPlayerRVA3 = TodayPogPlayerRVA { playerId -> viewModel.selectSetPlayer(3, playerId) }
        binding.rvTodayMatchTodayPog3rdVote.adapter = todayPogPlayerRVA3

        todayPogPlayerRVA4 = TodayPogPlayerRVA { playerId -> viewModel.selectSetPlayer(4, playerId) }
        binding.rvTodayMatchTodayPog4thVote.adapter = todayPogPlayerRVA4

        todayPogPlayerRVA5 = TodayPogPlayerRVA { playerId -> viewModel.selectSetPlayer(5, playerId) }
        binding.rvTodayMatchTodayPog5thVote.adapter = todayPogPlayerRVA5

        todayPogPlayerRVAMatch = TodayPogPlayerRVA { playerId -> viewModel.selectMatchPlayer(playerId) }
        binding.rvTodayMatchTodayPogMatchVote.adapter = todayPogPlayerRVAMatch
    }

    // setCount 수 만큼 리사이클러뷰 개수 설정
    private fun adjustLayoutVisibility(setCount: Int) {
        binding.layoutTodayMatch1stVote.visibility = if (setCount >= 1) View.VISIBLE else View.GONE
        binding.layoutTodayMatch2ndVote.visibility = if (setCount >= 2) View.VISIBLE else View.GONE
        binding.layoutTodayMatch3rdVote.visibility = if (setCount >= 3) View.VISIBLE else View.GONE
        binding.layoutTodayMatch4thVote.visibility = if (setCount >= 4) View.VISIBLE else View.GONE
        binding.layoutTodayMatch5thVote.visibility = if (setCount >= 5) View.VISIBLE else View.GONE
        binding.layoutTodayMatchMatchVote.visibility = View.VISIBLE // 매치 POG는 항상 visible
    }

    private fun setupVoteImageViewClick() {
        binding.ivTodayMatchTodayPog1stVote.setOnSingleClickListener {
            binding.ivTodayMatchTodayPog1stVote.visibility = View.GONE
            binding.rvTodayMatchTodayPog1stVote.visibility = View.VISIBLE
        }
        binding.ivTodayMatchTodayPog2ndVote.setOnSingleClickListener {
            binding.ivTodayMatchTodayPog2ndVote.visibility = View.GONE
            binding.rvTodayMatchTodayPog2ndVote.visibility = View.VISIBLE
        }
        binding.ivTodayMatchTodayPog3rdVote.setOnSingleClickListener {
            binding.ivTodayMatchTodayPog3rdVote.visibility = View.GONE
            binding.rvTodayMatchTodayPog3rdVote.visibility = View.VISIBLE
        }
        binding.ivTodayMatchTodayPog4thVote.setOnSingleClickListener {
            binding.ivTodayMatchTodayPog4thVote.visibility = View.GONE
            binding.rvTodayMatchTodayPog4thVote.visibility = View.VISIBLE
        }
        binding.ivTodayMatchTodayPog5thVote.setOnSingleClickListener {
            binding.ivTodayMatchTodayPog5thVote.visibility = View.GONE
            binding.rvTodayMatchTodayPog5thVote.visibility = View.VISIBLE
        }
        binding.ivTodayMatchTodayPogMatchVote.setOnSingleClickListener {
            binding.ivTodayMatchTodayPogMatchVote.visibility = View.GONE
            binding.rvTodayMatchTodayPogMatchVote.visibility = View.VISIBLE
        }
    }
}