package umc.everyones.lck.presentation.lck

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentAboutLckTeamPlayerBinding
import umc.everyones.lck.domain.model.about_lck.AboutLckPlayerModel
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.lck.adapter.PlayerCareerAdapter
import umc.everyones.lck.presentation.lck.data.PlayerCareerData
import umc.everyones.lck.presentation.mypage.MyPageActivity
import umc.everyones.lck.util.extension.repeatOnStarted
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class AboutLckTeamPlayerFragment : BaseFragment<FragmentAboutLckTeamPlayerBinding>(R.layout.fragment_about_lck_team_player) {
    private val viewModel: AboutLckPlayerCareerViewModel by viewModels()
    private lateinit var adapter: PlayerCareerAdapter
    override fun initObserver() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.winningCareer.collect { seasonNames ->
                updateWinningCareer(seasonNames)
            }
        }
        viewLifecycleOwner.repeatOnStarted {
            viewModel.history.collect { seasonTeamDetails ->
                updateHistory(seasonTeamDetails)
            }
        }
        viewLifecycleOwner.repeatOnStarted {
            viewModel.player.collect { player ->
                if (player != null) {
                    updatePlayerUI(player)
                } else {
                    Timber.e("Player data is null")
                }
            }
        }
    }

    override fun initView() {
        initRecyclerView()
        initBackButton()
        goMyPage()
        val playerId = arguments?.let { AboutLckTeamPlayerFragmentArgs.fromBundle(it).playerId }

        val page = 0
        val size = 10

        playerId?.let {
            viewModel.fetchLckWinningCareer(it, page, size)
            viewModel.fetchLckHistory(it, page, size)
            viewModel.fetchLckPlayer(it)
        } ?: run {
            Timber.e("Error: teamId is null")
        }
    }
    private fun initRecyclerView() {
        val recyclerView: RecyclerView = binding.rvAboutLckTeamPlayer
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false

        val items = mutableListOf(
            PlayerCareerData("Winning Career", emptyList()),
            PlayerCareerData("History", emptyList())
        )

        adapter = PlayerCareerAdapter(items)
        recyclerView.adapter = adapter
    }

    private fun updateWinningCareer(seasonNames: List<String>) {
        val items = adapter.getItems().toMutableList()

        val winningCareerIndex = items.indexOfFirst { it.title == "Winning Career" }
        if ( winningCareerIndex!= -1) {
            items[ winningCareerIndex] = items [winningCareerIndex].copy(details = seasonNames)
            adapter.updateItems(items)
        }
    }

    private fun updateHistory(seasonTeamDetails: List<String>) {
        val items = adapter.getItems().toMutableList()

        val historyIndex = items.indexOfFirst { it.title == "History" }
        if ( historyIndex != -1) {
            items[historyIndex] = items [historyIndex].copy(details = seasonTeamDetails)
            adapter.updateItems(items)
        }
    }

    private fun updatePlayerUI(player: AboutLckPlayerModel) {

        val teamLogoUrl = arguments?.let { AboutLckTeamPlayerFragmentArgs.fromBundle(it).teamLogoUrl }
        teamLogoUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.ivAboutLckTeamPlayerLogo)
        }

        Glide.with(this)
            .load(player.playerProfileImageUrl)
            .into(binding.ivAboutLckTeamPlayerImg)

        binding.tvAboutLckTeamPlayerNickName.text = player.nickName
        binding.tvAboutLckTeamPlayerBirth.text = player.birthDate.substring(0, 10).replace("-", ".")
        val positionText = when (player.position.toString()) {
            "JUNGLE" -> "JGL"
            "SUPPORT" -> "SPT"
            else -> player.position.toString()
        }
        binding.tvAboutLckTeamPlayerPosition.text = positionText
        binding.tvAboutLckTeamPlayerRealName.text = player.realName

        val positionIcon = when (player.position.name) {
            "TOP" -> R.drawable.ic_top
            "JUNGLE" -> R.drawable.ic_jgl
            "MID" -> R.drawable.ic_mid
            "BOT" -> R.drawable.ic_bot
            "SUPPORT" -> R.drawable.ic_support
            "COACH" -> R.drawable.ic_coach
            else -> R.drawable.ic_top
        }
        binding.ivAboutLckTeamPlayerPosition.setImageResource(positionIcon)
    }

    private fun initBackButton() {
        val backButton = binding.ivAboutLckTeamPlayerPre
        backButton.setOnSingleClickListener {
            findNavController().popBackStack()
        }
    }

    private fun goMyPage(){
        binding.ivMyPage.setOnSingleClickListener {
            startActivity(MyPageActivity.newIntent(requireContext()))
        }
    }
}
