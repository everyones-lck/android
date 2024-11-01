package umc.everyones.lck.presentation.mypage

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageMyteamBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.util.TeamData
import umc.everyones.lck.util.extension.setOnSingleClickListener

@AndroidEntryPoint
class MyPageMyteamFragment : BaseFragment<FragmentMypageMyteamBinding>(R.layout.fragment_mypage_myteam) {

    private var selectedTeamId: Int = 1 // 기본 팀 ID로 초기화
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private val navigator by lazy { findNavController() }

    override fun initObserver() {
        myPageViewModel.inquiryProfile()
        myPageViewModel.teamId.observe(viewLifecycleOwner) { teamId ->
            Timber.d("Observed teamId: $teamId") // teamId 로그 추가

            // 기존 팀 로고와 이름을 반영 (처음 로딩 시)
            val teamLogoResId = TeamData.mypageMyteam[teamId] ?: R.drawable.ic_mypage_myteam_empty // 기본 로고 설정
            val teamName = TeamData.teamNames[teamId] // 팀 ID로 팀 이름 가져오기

            // 팀 로고와 이름 업데이트
            binding.ivMypageMyteamTeamLogo.setImageResource(teamLogoResId)
            binding.tvMypageMyteamTeamName.text = teamName

            // 선택된 팀 ID 초기화 및 UI 업데이트
            selectedTeamId = teamId // 현재 팀 ID로 선택된 팀 ID 설정
            updateTeamSelectionUI() // UI 업데이트
        }
    }


    override fun initView() {
        setupTeamSelection { teamName, teamId ->
            // 팀 로고 업데이트
            val teamLogoResId = TeamData.myteamLogos[teamId] // TeamData에서 로고 리소스 가져오기
            if (teamLogoResId != null) {
                binding.ivMypageMyteamTeamLogo.setImageResource(teamLogoResId) // 로고 설정
            }
        }

        binding.tvMypageMyteamTopbarEdit.setOnSingleClickListener {
            // 선택된 팀이 없을 경우 기본 팀 ID(1)로 설정
            val teamIdToUpdate = selectedTeamId ?: 1

            lifecycleScope.launch {
                try {
                    // 팀 업데이트 호출
                    myPageViewModel.updateTeam(teamIdToUpdate)

                    // 팀 ID 관찰 (한 번만 등록)
                    myPageViewModel.teamId.observe(viewLifecycleOwner) { teamId ->
                        Timber.d("Observed teamId: $teamId") // teamId 로그 추가

                        // 기존 팀 로고를 반영 (처음 로딩 시)
                        val teamLogoResId = TeamData.mypageMyteam[teamId]
                        val teamName = TeamData.teamNames[teamId] // 팀 ID로 팀 이름 가져오기

                        if (teamLogoResId != null) {
                            binding.ivMypageMyteamTeamLogo.setImageResource(teamLogoResId)
                        } else {
                            // 기본 로고 설정 (예: 선택된 팀이 없을 경우)
                            binding.ivMypageMyteamTeamLogo.setImageResource(R.drawable.ic_mypage_myteam_empty)
                        }

                        // 팀 이름 업데이트
                        binding.tvMypageMyteamTeamName.text = teamName ?: "선택된 My Team이 없습니다"
                    }

                    // 프로필 조회 (팀 업데이트 후)
                    myPageViewModel.inquiryProfile()

                    Timber.d( "Navigated to next fragment with team ID: $teamIdToUpdate")

                } catch (e: Exception) {
                    Timber.e("Error navigating", e)
                }
            }
        }


        // ScrollView 및 버튼 초기화
        binding.layoutMypageMyteamList.visibility = View.GONE
        binding.btnMypageMyteamArrow.setImageResource(R.drawable.ic_arrow_down)

        binding.btnMypageMyteamArrow.setOnSingleClickListener {
            toggleScrollView()
        }

        binding.ivMypageMyteamBack.setOnSingleClickListener {
            findNavController().navigateUp()
        }
    }


    private fun setupTeamSelection(onTeamSelected: (String?, Int) -> Unit) {
        TeamData.myteamLogos.forEach { (imageViewId, teamId) ->
            val imageView = binding.root.findViewById<ImageView>(imageViewId)

            if (imageView != null) { // null 체크
                imageView.setOnSingleClickListener {
                    // 선택한 팀 ID가 이미 선택된 ID와 같으면 기본 팀 ID로 설정
                    selectedTeamId = if (selectedTeamId == teamId) {
                        1 // 기본 팀 ID
                    } else {
                        teamId // 선택한 팀 ID
                    }

                    // 선택된 팀의 UI 업데이트
                    updateTeamSelectionUI()

                    // 로그 찍기: 현재 선택된 팀 ID와 보낼 팀 ID 확인
                    Timber.d("보내고자 하는 팀 ID: $teamId, 현재 선택된 팀 ID: $selectedTeamId")

                    // 팀 이름과 ID를 전달
                    val teamName = TeamData.teamNames[selectedTeamId]
                    onTeamSelected(teamName, selectedTeamId!!)
                }
            } else {
                Timber.e("ImageView with ID $imageViewId not found.")
            }
        }
    }

    //팀 선택 시 색상 변경
    private fun updateTeamSelectionUI() {
        TeamData.myteamLogos.forEach { (imageViewId, teamId) ->
            val imageView = binding.root.findViewById<ImageView>(imageViewId)
            val drawableRes = if (teamId == selectedTeamId) { // selectedTeamId로 변경
                R.drawable.shape_team_background_selected
            } else {
                R.drawable.shape_team_background
            }
            imageView.background = ContextCompat.getDrawable(requireContext(), drawableRes)
        }
    }

    // 화살표 클릭시 팀 열림
    private fun toggleScrollView() {
        // ScrollView의 상태를 전환하는 메서드
        if (binding.layoutMypageMyteamList.visibility == View.GONE) {
            binding.layoutMypageMyteamList.visibility = View.VISIBLE
            binding.btnMypageMyteamArrow.setImageResource(R.drawable.ic_arrow_up)
        } else {
            binding.layoutMypageMyteamList.visibility = View.GONE
            binding.btnMypageMyteamArrow.setImageResource(R.drawable.ic_arrow_down)
        }
    }
}
