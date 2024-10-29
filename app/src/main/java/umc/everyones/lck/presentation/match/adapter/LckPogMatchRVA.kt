package umc.everyones.lck.presentation.match.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import umc.everyones.lck.databinding.ItemLckPogMatchBinding
import umc.everyones.lck.domain.model.response.match.CommonTodayMatchPogModel
import umc.everyones.lck.domain.model.todayMatch.LckPog
import umc.everyones.lck.util.extension.toOrdinal

class LckPogMatchRVA(
    private var setCount: Int,  // 세트 수를 받아서 탭을 동적으로 추가
    private val onTabSelected: Int // 탭 선택 시 호출할 함수
) : ListAdapter<CommonTodayMatchPogModel, LckPogMatchRVA.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLckPogMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemLckPogMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val playerAdapter = LckPogPlayerRVA()
        private lateinit var currentItem: CommonTodayMatchPogModel // 현재 item을 저장하는 변수

        init {
            binding.rvTodayMatchLckPogPlayer.adapter = playerAdapter

            // 탭 레이아웃 초기화 및 세트 수에 맞춰 탭 추가
            binding.tabTodayMatchLckPog.removeAllTabs()
            for (i in 1..setCount) {
                binding.tabTodayMatchLckPog.addTab(binding.tabTodayMatchLckPog.newTab().setText("${i.toOrdinal()} POG"))
            }
            binding.tabTodayMatchLckPog.addTab(binding.tabTodayMatchLckPog.newTab().setText("by Match"))

            // 탭의 마진 설정
            for (i in 0 until binding.tabTodayMatchLckPog.tabCount) {
                val tab = (binding.tabTodayMatchLckPog.getChildAt(0) as? ViewGroup)?.getChildAt(i)
                tab?.let {
                    val layoutParams = it.layoutParams as LinearLayout.LayoutParams
                    layoutParams.marginStart = 20
                    layoutParams.marginEnd = 20 // 20dp margin between tabs
                    it.layoutParams = layoutParams
                }
            }

            // 탭 선택 리스너
            binding.tabTodayMatchLckPog.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let { position ->
                        val playerList: List<CommonTodayMatchPogModel.PogPlayerModel.SetPogResponsesModel> = when {
                            position < currentItem.setPogResponses.size -> {
                                // 현재 탭 위치에 따라 setIndex를 결정
                                currentItem.setPogResponses.filter { it.setIndex == (position + 1) }.take(1) // index 조정
                            }
                            // byMatch 탭의 선수
                            position == currentItem.setPogResponses.size -> currentItem.matchPogResponse?.let { matchPogResponse ->
                                listOf(
                                    CommonTodayMatchPogModel.PogPlayerModel.SetPogResponsesModel(
                                        matchPogResponse.name,
                                        matchPogResponse.profileImageUrl,
                                        matchPogResponse.playerId,
                                        0 // setIndex는 필요 없으므로 0으로 설정
                                    )
                                )
                            } ?: emptyList() // matchPogResponse가 null일 경우 빈 리스트 반환
                            else -> emptyList()
                        }
                        playerAdapter.submitList(playerList)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }


        fun bind(item: CommonTodayMatchPogModel) {
            currentItem = item
            binding.tvTodayMatchLckPogMatchTitle.text = "${item.seasonInfo} ${item.matchNumber.toOrdinal()} Match"
            binding.tvTodayMatchLckPogMatchDate.text = item.matchDate
            // 첫 번째 탭을 기본으로 설정 (1st POG tab에 해당하는 플레이어만 표시)
            val firstTabPlayerList = item.setPogResponses.filter { it.setIndex == 1 }.take(1)

            // setPogResponses와 matchPogResponse를 체크하여 visibility 설정
            if (firstTabPlayerList.isEmpty() && item.matchPogResponse == null) {
                binding.tvTodayMatchLckPogPlaying.visibility = View.VISIBLE
                binding.rvTodayMatchLckPogPlayer.visibility = View.GONE
            } else {
                binding.tvTodayMatchLckPogPlaying.visibility = View.GONE
                binding.rvTodayMatchLckPogPlayer.visibility = View.VISIBLE
                playerAdapter.submitList(firstTabPlayerList)
            }
        }
    }
    fun updatePlayers(players: List<CommonTodayMatchPogModel>) {
        submitList(players)
    }

    class DiffCallback : DiffUtil.ItemCallback<CommonTodayMatchPogModel>() {
        override fun areItemsTheSame(oldItem: CommonTodayMatchPogModel, newItem: CommonTodayMatchPogModel): Boolean {
            return oldItem.matchNumber == newItem.matchNumber // 각 항목의 고유 ID로 비교
        }

        override fun areContentsTheSame(oldItem: CommonTodayMatchPogModel, newItem: CommonTodayMatchPogModel): Boolean {
            return oldItem == newItem // 전체 항목이 동일한지 비교
        }
    }
}