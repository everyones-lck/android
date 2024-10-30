package umc.everyones.lck.presentation.lck

import androidx.fragment.app.viewModels
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentAboutLckTeamBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.lck.adapter.TeamVPAdapter
import umc.everyones.lck.presentation.lck.data.PlayerData
import umc.everyones.lck.presentation.lck.util.OnPlayerItemClickListener
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber
import umc.everyones.lck.data.dto.response.about_lck.LckPlayerDetailsResponseDto
import umc.everyones.lck.presentation.mypage.MyPageActivity
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class AboutLckTeamFragment : BaseFragment<FragmentAboutLckTeamBinding>(R.layout.fragment_about_lck_team),
    OnPlayerItemClickListener {

    private val viewModel: AboutLckTeamViewModel by viewModels()
    private val navigator by lazy { findNavController() }
    private lateinit var pagerAdapter: TeamVPAdapter

    private var teamLogoUrl: String? = null
    private var teamId: Int ?= null

    override fun initObserver() {

    }

    override fun initView() {
        receiveSafeArgs()
        goMyPage()

        if (teamId != null) {
            setupViewPagerAndTabs()
            setupButtons()
            setupTeamInfo()
        } else {
            Timber.e("teamId is null during initView, cannot setup views properly")
        }
    }

    private fun receiveSafeArgs() {
        val receivedTeamId = arguments?.let { AboutLckTeamFragmentArgs.fromBundle(it).teamId }
        teamLogoUrl = arguments?.let { AboutLckTeamFragmentArgs.fromBundle(it).teamLogoUrl }

        receivedTeamId?.let {
            teamId = it
            viewModel.setTeamId(it)
        } ?: Timber.e("Failed to receive teamId from arguments")
    }

    private fun setupViewPagerAndTabs() {
        val tabLayout: TabLayout = binding.tbAboutLckTeam
        val viewPager: ViewPager2 = binding.vpAboutLckTeam

        pagerAdapter = TeamVPAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "LCK Roaster"
                1 -> "Coaches"
                2 -> "LCK CL Roaster"
                else -> throw IllegalArgumentException("Invalid tab position")
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                teamId?.let {
                    val playerRole = when (position) {
                        0 -> LckPlayerDetailsResponseDto.PlayerRole.LCK_ROSTER
                        1 -> LckPlayerDetailsResponseDto.PlayerRole.COACH
                        2 -> LckPlayerDetailsResponseDto.PlayerRole.LCK_CL_ROSTER
                        else -> throw IllegalArgumentException("Invalid tab position")
                    }
                    viewModel.fetchLckPlayerDetails(it, "2024 Summer", playerRole)
                } ?: Timber.e("teamId is null during onPageSelected")
            }
        })
    }
    private fun setupButtons() {
        val backButton: ImageView = binding.ivAboutLckTeamPre
        val nextButton: ImageView = binding.ivAboutLckTeamNext

        backButton.setOnSingleClickListener { navigator.popBackStack() }
        nextButton.setOnSingleClickListener {
            val teamId = arguments?.let { AboutLckTeamFragmentArgs.fromBundle(it).teamId }
            teamId?.let {
                val action = AboutLckTeamFragmentDirections
                    .actionAboutLCKTeamFragmentToAboutLckTeamHistoryFragment(
                        teamId = it,
                        teamName = arguments?.let { AboutLckTeamFragmentArgs.fromBundle(it).teamName } ?: "",
                        teamLogoUrl = teamLogoUrl ?: ""
                    )
                navigator.navigate(action)
            } ?: Timber.e("teamId is null, cannot navigate")
        }
    }

    private fun setupTeamInfo() {
        val teamTitleTextView: TextView = binding.tvAboutLckTeamTitle
        val teamLogoImageView: ImageView = binding.ivAboutLckTeamLogo
        val teamName = arguments?.let { AboutLckTeamFragmentArgs.fromBundle(it).teamName }

        teamName?.let {
            teamTitleTextView.text = it
        }

        teamLogoUrl?.let {
            Glide.with(this)
                .load(it)
                .into(teamLogoImageView)
        }
    }
    override fun onPlayerItemClick(player: PlayerData) {
        val action = AboutLckTeamFragmentDirections.actionAboutLCKTeamFragmentToAboutLckTeamPlayerFragment(player.playerId,teamLogoUrl?:" ")
        navigator.navigate(action)
    }

    private fun goMyPage(){
        binding.ivMyPage.setOnSingleClickListener {
            startActivity(MyPageActivity.newIntent(requireContext()))
        }
    }
}
