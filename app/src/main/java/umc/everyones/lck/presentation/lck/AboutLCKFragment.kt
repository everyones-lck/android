package umc.everyones.lck.presentation.lck

import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentAboutLckBinding
import umc.everyones.lck.domain.model.about_lck.AboutLckMatchDetailsModel
import umc.everyones.lck.domain.model.about_lck.AboutLckRankingDetailsModel
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.lck.adapter.MatchVPAdapter
import umc.everyones.lck.presentation.lck.adapter.RankingAdapter
import umc.everyones.lck.presentation.lck.data.RankingData
import umc.everyones.lck.presentation.lck.util.CustomDatePickerDialog
import umc.everyones.lck.presentation.lck.util.OnTeamClickListener
import umc.everyones.lck.presentation.lck.util.VerticalSpaceItemDecoration
import umc.everyones.lck.presentation.mypage.MyPageActivity
import umc.everyones.lck.util.extension.getFormattedDate
import umc.everyones.lck.util.extension.getFormattedNextDay
import umc.everyones.lck.util.extension.getFormattedPreviousDay
import umc.everyones.lck.util.extension.repeatOnStarted
import umc.everyones.lck.util.extension.setOnSingleClickListener
import umc.everyones.lck.util.extension.toCalendar
import java.util.Calendar

@AndroidEntryPoint
class AboutLCKFragment : BaseFragment<FragmentAboutLckBinding>(R.layout.fragment_about_lck),
    OnTeamClickListener {

    private lateinit var matchVPAdapter: MatchVPAdapter
    private lateinit var rankingAdapter: RankingAdapter
    private val topTeams = mutableListOf<RankingData>()

    private var isDatePickerDialogVisible = false
    private var datePickerDialog: CustomDatePickerDialog? = null

    private val navigator by lazy { findNavController() }
    private val viewModel: AboutLckViewModel by viewModels()

    private var selectedDate: Calendar = Calendar.getInstance()
    private var isSelectedByCalendar = false

    override fun initObserver() {

        viewLifecycleOwner.repeatOnStarted {
            viewModel.matchDetails.collect { matchDetails ->
                if (matchDetails != null) {
                    updateMatchDetails(matchDetails)
                } else {
                    Timber.tag("initObserver").e("Failed to fetch match details")
                }
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            viewModel.rankingDetails.collect { rankingDetails ->
                if (rankingDetails != null) {
                    updateRankingDetails(rankingDetails)
                } else {
                    Timber.tag("initObserver").e("Failed to fetch ranking details")
                }
            }
        }
    }

    override fun initView() {
        initMatchViewPager()
        initRankingRecyclerView()
        initBackButton()
        initCalendarButton()
        goMyPage()
    }

    private fun updateMatchDetails(matchDetailsModel: AboutLckMatchDetailsModel) {
        val matchDetailList = matchDetailsModel.matchByDateList.sortedBy { it.matchDate }

        // Set을 통해 기존 ViewPager List와 API Response List 간의 중복 제거
        val tempSet = matchVPAdapter.currentList.toHashSet()
        tempSet.addAll(matchDetailList)

        matchVPAdapter.submitList(tempSet.toList().sortedBy { it.matchDate }) {
            scrollToSelectedDate(selectedDate.getFormattedDate())
        }
    }

    private fun scrollToSelectedDate(selectedDate: String) {
        val currentIndex = matchVPAdapter.currentList.indexOfFirst { it.matchDate == selectedDate }

        // 선택된 날짜로 ViewPager 이동
        binding.vpAboutLckMatch.setCurrentItem(currentIndex, false)
        binding.tvAboutLckDate.text = selectedDate.replace("-", ".")
    }

    private fun initMatchViewPager() {
        matchVPAdapter = MatchVPAdapter()
        binding.vpAboutLckMatch.apply {
            adapter = matchVPAdapter

            // ViewPager 양 옆에 여백 추가
            offscreenPageLimit = 2
            val pageMarginPx = 16 * resources.displayMetrics.densityDpi / 160
            val offsetPx = 40 * resources.displayMetrics.densityDpi / 160

            setPageTransformer { page, position ->
                page.translationX = -offsetPx * position
            }

            val recyclerViewMatches = getChildAt(0) as RecyclerView
            recyclerViewMatches.setPadding(pageMarginPx, 0, pageMarginPx, 0)
            recyclerViewMatches.clipToPadding = false

            // ViewPager의 page가 Scroll/Calendar에 의해 선택되었을 떄  API 호출 제어
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val currentVpDate = matchVPAdapter.currentList[position].matchDate

                    if (isSelectedByCalendar) {
                        onPageSelectedByCalendar(currentVpDate)
                    } else {
                       onPageSelectedByScroll(currentVpDate)
                    }
                }
            })
        }
    }

    private fun onPageSelectedByCalendar(currentVpDate: String) {
        if (selectedDate.getFormattedDate() != currentVpDate) {
            val index =
                matchVPAdapter.currentList.indexOfFirst { it.matchDate == selectedDate.getFormattedDate() }
            binding.vpAboutLckMatch.currentItem = index
            binding.tvAboutLckDate.text =
                selectedDate.getFormattedDate().replace("-", ".")
        }
        isSelectedByCalendar = false
    }

    private fun onPageSelectedByScroll(currentVpDate: String) {
        // 동일 날짜에 대한 API 호출 방지
        if (selectedDate.getFormattedDate() != currentVpDate) {
            selectedDate = currentVpDate.toCalendar()
            binding.tvAboutLckDate.text = currentVpDate.replace("-", ".")

            // API 중복 호출 방지
            // ViewPager 현재 page 날짜에 대한 이전날짜 혹은 다음 날짜가 없을 떄만 API 호출
            if (getDuplicateFetchMatchDetailsFlag(selectedDate)) {
                viewModel.fetchLckMatchDetails(currentVpDate)
            }
        }
    }
    private fun getDuplicateFetchMatchDetailsFlag(currentVpDate: Calendar) =
        matchVPAdapter.currentList.none { it.matchDate == getFormattedPreviousDay(currentVpDate) }
                || matchVPAdapter.currentList.none {
            it.matchDate == getFormattedNextDay(currentVpDate)
        }

    private fun updateRankingDetails(rankingDetails: AboutLckRankingDetailsModel) {
        val teamDetails = rankingDetails.teamDetailList

        if (teamDetails.isNotEmpty()) {
            topTeams.clear()
            topTeams.addAll(teamDetails.take(3).map { teamDetail ->
                RankingData(
                    teamId = teamDetail.teamId,
                    ranking = teamDetail.rating,
                    teamLogoUrl = teamDetail.teamLogoUrl,
                    teamName = teamDetail.teamName
                )
            })
            displayTopTeams(topTeams)

            val remainingTeams = teamDetails.drop(3).map { teamDetail ->
                RankingData(
                    teamId = teamDetail.teamId,
                    ranking = teamDetail.rating,
                    teamLogoUrl = teamDetail.teamLogoUrl,
                    teamName = teamDetail.teamName
                )
            }
            rankingAdapter.updateTeams(remainingTeams)
        }
    }

    private fun initRankingRecyclerView() {
        val recyclerView: RecyclerView = binding.rvAboutLckRanking
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        rankingAdapter = RankingAdapter(mutableListOf(), this)
        recyclerView.adapter = rankingAdapter

        val verticalSpaceHeightPx = (10 * resources.displayMetrics.density).toInt()
        recyclerView.addItemDecoration(VerticalSpaceItemDecoration(verticalSpaceHeightPx))

        viewModel.fetchLckRanking("2024 Summer", 0, 10)

    }

    private fun displayTopTeams(topTeams: List<RankingData>) {
        binding.ivAboutLckRanking1st.loadImage(topTeams[0].teamLogoUrl)
        binding.tvAboutLckRanking1st.text = topTeams[0].teamName

        binding.ivAboutLckRanking2nd.loadImage(topTeams[1].teamLogoUrl)
        binding.tvAboutLckRanking2nd.text = topTeams[1].teamName

        binding.ivAboutLckRanking3rd.loadImage(topTeams[2].teamLogoUrl)
        binding.tvAboutLckRanking3rd.text = topTeams[2].teamName
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(this.context)
            .load(url)
            .into(this)
    }


    private fun initBackButton() {
        binding.viewAboutLckRect1.setOnSingleClickListener {
            topTeams.getOrNull(0)?.let { team ->
                val action =
                    AboutLCKFragmentDirections.actionAboutLCKFragmentToAboutLckTeamFragment(
                        team.teamId,
                        team.teamName,
                        team.teamLogoUrl
                    )
                navigator.navigate(action)
            }
        }

        binding.viewAboutLckRect2.setOnSingleClickListener {
            topTeams.getOrNull(1)?.let { team ->
                val action =
                    AboutLCKFragmentDirections.actionAboutLCKFragmentToAboutLckTeamFragment(
                        team.teamId,
                        team.teamName,
                        team.teamLogoUrl
                    )
                navigator.navigate(action)
            }
        }

        binding.viewAboutLckRect3.setOnSingleClickListener {
            topTeams.getOrNull(2)?.let { team ->
                val action =
                    AboutLCKFragmentDirections.actionAboutLCKFragmentToAboutLckTeamFragment(
                        team.teamId,
                        team.teamName,
                        team.teamLogoUrl
                    )
                navigator.navigate(action)
            }
        }
    }

    private fun initCalendarButton() {
        binding.ivAboutLckCalendar.setOnSingleClickListener {
            toggleDatePickerDialog()
        }

        updateSelectedDate(
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun toggleDatePickerDialog() {
        if (isDatePickerDialogVisible) {
            datePickerDialog?.dismiss()
            isDatePickerDialogVisible = false
        } else {
            showCustomDatePickerDialog()
        }
    }

    private fun showCustomDatePickerDialog() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = CustomDatePickerDialog(
            requireContext(),
            year,
            month,
            day
        ) { selectedYear, selectedMonth, selectedDay ->
            updateSelectedDate(selectedYear, selectedMonth, selectedDay)
        }

        datePickerDialog?.show()
        isDatePickerDialogVisible = true
    }

    private fun updateSelectedDate(year: Int, month: Int, day: Int) {
        selectedDate.set(year, month, day)
        val formattedDate = selectedDate.getFormattedDate()
        Timber.d(formattedDate)
        isSelectedByCalendar = true
        viewModel.fetchLckMatchDetails(formattedDate)
    }

    override fun onTeamClick(team: RankingData) {
        val action = AboutLCKFragmentDirections.actionAboutLCKFragmentToAboutLckTeamFragment(
            team.teamId,
            team.teamName,
            team.teamLogoUrl
        )
        navigator.navigate(action)
    }

    private fun goMyPage() {
        binding.ivMyPage.setOnSingleClickListener {
            startActivity(MyPageActivity.newIntent(requireContext()))
        }
    }
}
