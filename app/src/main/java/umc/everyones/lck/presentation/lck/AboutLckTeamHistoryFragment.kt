package umc.everyones.lck.presentation.lck

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentAboutLckTeamHistoryBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.lck.adapter.HistoryAdapter
import umc.everyones.lck.presentation.lck.data.HistoryData
import umc.everyones.lck.presentation.mypage.MyPageActivity
import umc.everyones.lck.util.extension.repeatOnStarted
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class AboutLckTeamHistoryFragment : BaseFragment<FragmentAboutLckTeamHistoryBinding>(R.layout.fragment_about_lck_team_history) {

    private val viewModel: AboutLckTeamHistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter
    override fun initObserver() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.winningHistory.collect { seasonNameList ->
                updateWinningHistory(seasonNameList)
            }
        }
        viewLifecycleOwner.repeatOnStarted {
            viewModel.recentPerformances.collect { recentPerformances ->
                updateRecentPerformances(recentPerformances)
            }
        }
        viewLifecycleOwner.repeatOnStarted {
            viewModel.historyOfRoaster.collect { historyOfRoaster ->
                updateHistoryOfRoaster(historyOfRoaster)
            }
        }

    }

    override fun initView() {
        initRecyclerView()
        initBackButton()
        setupTeamInfo()
        fetchTeamData()
        goMyPage()
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = binding.rvAboutLckHistory
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false

        val items = mutableListOf(
            HistoryData("Winning History", emptyList()),
            HistoryData("Recent Performance", emptyList()),
            HistoryData("History Of Roaster", emptyList())
        )

        adapter = HistoryAdapter(items)
        recyclerView.adapter = adapter
    }

    private fun setupTeamInfo() {
        val teamName = arguments?.let { AboutLckTeamHistoryFragmentArgs.fromBundle(it).teamName }
        val teamLogoUrl = arguments?.let { AboutLckTeamHistoryFragmentArgs.fromBundle(it).teamLogoUrl }

        teamName?.let { binding.ivAboutLckTeamHistoryTeamName.text = it }
        teamLogoUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.ivAboutLckTeamHistoryLogo)
        }
    }

    private fun fetchTeamData() {
        val teamId = arguments?.let { AboutLckTeamHistoryFragmentArgs.fromBundle(it).teamId }
        val page = 0
        val size = 10

        teamId?.let {
            viewModel.fetchLckWinningHistory(it, page, size)
            viewModel.fetchLckRecentPerformances(it, page, size)
            viewModel.fetchLckHistoryOfRoaster(it, page, size)
        } ?: run {
            Timber.e("Error: teamId is null")
        }
    }

    private fun updateWinningHistory(seasonNameList: List<String>) {
        val items = adapter.getItems().toMutableList()

        val winningHistoryIndex = items.indexOfFirst { it.title == "Winning History" }
        if (winningHistoryIndex != -1) {
            items[winningHistoryIndex] = items[winningHistoryIndex].copy(details = seasonNameList)
            adapter.updateItems(items)
        }
    }

    private fun updateRecentPerformances(recentPerformances: List<String>) {
        val items = adapter.getItems().toMutableList()
        val recentPerformanceIndex = items.indexOfFirst { it.title == "Recent Performance" }
        if (recentPerformanceIndex != -1) {
            items[recentPerformanceIndex] = items[recentPerformanceIndex].copy(details = recentPerformances)
            adapter.updateItems(items)
        }
    }

    private fun updateHistoryOfRoaster(historyOfRoaster: List<String>) {
        val items = adapter.getItems().toMutableList()
        val historyOfRoasterIndex = items.indexOfFirst { it.title == "History Of Roaster" }
        if (historyOfRoasterIndex != -1) {
            items[historyOfRoasterIndex] = items[historyOfRoasterIndex].copy(details = historyOfRoaster)
            adapter.updateItems(items)
        }
    }

    private fun initBackButton() {
        val backButton = binding.ivAboutLckTeamHistoryPre
        backButton.setOnSingleClickListener{
            findNavController().popBackStack()
        }
    }
    private fun goMyPage(){
        binding.ivMyPage.setOnSingleClickListener {
            startActivity(MyPageActivity.newIntent(requireContext()))
        }
    }
}
