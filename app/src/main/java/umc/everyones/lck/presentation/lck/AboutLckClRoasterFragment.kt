package umc.everyones.lck.presentation.lck

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentAboutLckClRoasterBinding
import umc.everyones.lck.domain.model.about_lck.AboutLckPlayerDetailsModel
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.lck.adapter.PlayerAdapter
import umc.everyones.lck.presentation.lck.data.PlayerData
import umc.everyones.lck.util.extension.repeatOnStarted

@AndroidEntryPoint
class AboutLckClRoasterFragment : BaseFragment<FragmentAboutLckClRoasterBinding>(R.layout.fragment_about_lck_cl_roaster) {

    private val viewModel: AboutLckTeamViewModel by viewModels({requireParentFragment()})

    override fun initObserver() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.playerDetails.collect { playerDetails ->
                if (playerDetails != null) {
                    setPlayerData(playerDetails)
                } else {
                    Timber.e("Failed to fetch player details")
                }
            }
        }
    }

    override fun initView() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val recyclerView: RecyclerView = binding.rvAboutLckClRoaster
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.isNestedScrollingEnabled = false
    }

    private fun setPlayerData(playerDetails: AboutLckPlayerDetailsModel) {
        val playerDataList = playerDetails.playerDetails.map { player ->
            PlayerData(
                playerId = player.playerId,
                playerImg = player.profileImageUrl,
                teamColor = getTeamColorResource(viewModel.teamId.value ?: 0),
                name = player.playerName,
                teamLogo = getTeamLogoResource(viewModel.teamId.value ?: 0),
                isCaptain = player.isCaptain,
                position = player.position
            )
        }
        binding.rvAboutLckClRoaster.adapter = PlayerAdapter(playerDataList, null)
    }


    private fun getTeamColorResource(teamId: Int): Int {
        val colorRes = when (teamId) {
            2 -> R.drawable.img_about_lck_player_team_color_geng
            3 -> R.drawable.img_about_lck_player_team_color_hanwha
            4 -> R.drawable.img_about_lck_player_team_color_kia
            5 -> R.drawable.img_about_lck_player_team_color_t1_kt_kdf
            6 -> R.drawable.img_about_lck_player_team_color_t1_kt_kdf
            7 -> R.drawable.img_about_lck_player_team_color_t1_kt_kdf
            8 -> R.drawable.img_about_lck_player_team_color_bnk
            9 -> R.drawable.img_about_lck_player_team_color_t1_kt_kdf
            10 -> R.drawable.img_about_lck_player_team_color_drx
            11 -> R.drawable.img_about_lck_player_team_color_ok
            else -> R.drawable.img_about_lck_player_team_color_geng
        }
        return colorRes
    }

    private fun getTeamLogoResource(teamId: Int): Int {
        val logoRes = when (teamId) {
            2 -> R.drawable.img_about_lck_player_team_logo_geng
            3 -> R.drawable.img_about_lck_player_team_logo_hanhwa
            4 -> R.drawable.img_about_lck_player_team_logo_dk
            5 -> R.drawable.img_about_lck_player_team_logo_t1
            6 -> R.drawable.img_about_lck_player_team_logo_kt
            7 -> R.drawable.img_about_lck_player_team_logo_kwangdong
            8 -> R.drawable.img_about_lck_player_team_logo_bnk
            9 -> R.drawable.img_about_lck_player_team_logo_ns
            10 -> R.drawable.img_about_lck_player_team_logo_drx
            11 -> R.drawable.img_about_lck_player_team_logo_ok
            else ->R.drawable.img_about_lck_player_team_logo_t1
        }
        return logoRes
    }
}

