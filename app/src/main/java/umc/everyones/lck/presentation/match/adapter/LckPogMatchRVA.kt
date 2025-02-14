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

    fun updateSetCount(newSetCount: Int) {
        this.setCount = newSetCount
        notifyDataSetChanged() // 내부적으로 세트 수가 변하면 업데이트
    }


    inner class ViewHolder(private val binding: ItemLckPogMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val playerAdapter = LckPogPlayerRVA()
        private lateinit var currentItem: CommonTodayMatchPogModel // 현재 item을 저장하는 변수

        init {
            binding.rvTodayMatchLckPogPlayer.adapter = playerAdapter

            // 탭 선택 리스너는 ViewHolder 생성 시 한 번만 추가합니다.
            binding.tabTodayMatchLckPog.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let { position ->
                        // by Match 탭이 추가되었는지 여부는 탭 개수로 판단합니다.
                        val hasByMatchTab = binding.tabTodayMatchLckPog.tabCount > setCount
                        val playerList: List<CommonTodayMatchPogModel.PogPlayerModel.SetPogResponsesModel> = when {
                            // 0 ~ (setCount-1): 각 세트 탭
                            position < setCount -> {
                                // setIndex는 1부터 시작하므로 position + 1
                                currentItem.setPogResponses.filter { it.setIndex == (position + 1) }.take(1)
                            }
                            // 마지막 탭이 by Match 탭인 경우
                            position == setCount && hasByMatchTab -> {
                                currentItem.matchPogResponse?.let { matchPogResponse ->
                                    listOf(
                                        CommonTodayMatchPogModel.PogPlayerModel.SetPogResponsesModel(
                                            matchPogResponse.name,
                                            matchPogResponse.profileImageUrl,
                                            matchPogResponse.playerId,
                                            0 // setIndex는 필요 없으므로 0으로 설정
                                        )
                                    )
                                } ?: emptyList()
                            }
                            else -> emptyList()
                        }
                        if (playerList.isEmpty()) {
                            binding.tvTodayMatchLckPogPlaying.visibility = View.VISIBLE
                            binding.rvTodayMatchLckPogPlayer.visibility = View.GONE
                        } else {
                            binding.tvTodayMatchLckPogPlaying.visibility = View.GONE
                            binding.rvTodayMatchLckPogPlayer.visibility = View.VISIBLE
                            playerAdapter.submitList(playerList)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            // (선택 사항) 탭 간 margin 설정
            for (i in 0 until binding.tabTodayMatchLckPog.tabCount) {
                val tabView = (binding.tabTodayMatchLckPog.getChildAt(0) as? ViewGroup)?.getChildAt(i)
                tabView?.let {
                    val layoutParams = it.layoutParams as LinearLayout.LayoutParams
                    layoutParams.marginStart = 20
                    layoutParams.marginEnd = 20 // 20dp margin between tabs
                    it.layoutParams = layoutParams
                }
            }
        }

        // 탭 생성 로직을 별도 함수로 분리 (item의 matchPogResponse 값을 확인)
        private fun setupTabs(item: CommonTodayMatchPogModel) {
            binding.tabTodayMatchLckPog.removeAllTabs()
            // 세트 탭 추가
            for (i in 1..setCount) {
                binding.tabTodayMatchLckPog.addTab(
                    binding.tabTodayMatchLckPog.newTab().setText("${i.toOrdinal()} POG")
                )
            }
            // matchPogResponse가 기본값인지 확인
            val shouldShowByMatch = item.matchPogResponse?.let { response ->
                // 이름이 "기본" 이거나, 프로필 이미지 URL이 "defaultimage" (대소문자 무시),
                // 또는 playerId가 1이면 기본값으로 판단하여 by Match 탭을 표시하지 않음.
                !(response.name == "기본" ||
                        response.profileImageUrl.equals("defaultimage", ignoreCase = true) ||
                        response.playerId == 1)
            } ?: false

            if (shouldShowByMatch) {
                binding.tabTodayMatchLckPog.addTab(
                    binding.tabTodayMatchLckPog.newTab().setText("by Match")
                )
            }
        }

        fun bind(item: CommonTodayMatchPogModel) {
            currentItem = item
            binding.tvTodayMatchLckPogMatchTitle.text = "${item.seasonInfo} ${item.matchNumber.toOrdinal()} Match"
            binding.tvTodayMatchLckPogMatchDate.text = item.matchDate

            // item 데이터에 따라 탭을 다시 생성합니다.
            setupTabs(item)

            // 첫 번째 탭(세트 1 탭)을 기본 선택하여 데이터를 표시합니다.
            val firstTabPlayerList = item.setPogResponses.filter { it.setIndex == 1 }.take(1)
            if (firstTabPlayerList.isEmpty()) {
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
            return false
        }
    }
}