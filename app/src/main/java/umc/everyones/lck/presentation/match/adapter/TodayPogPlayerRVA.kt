package umc.everyones.lck.presentation.match.adapter

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.everyones.lck.databinding.ItemTodayPogPlayerBinding
import umc.everyones.lck.domain.model.response.match.PogPlayerTodayMatchModel
import umc.everyones.lck.domain.model.todayMatch.TodayPog
import umc.everyones.lck.util.extension.setOnSingleClickListener

class TodayPogPlayerRVA(
    private val onPlayerSelected: (Int) -> Unit // playerId를 전달하는 람다 함수
) : ListAdapter<PogPlayerTodayMatchModel.InformationModel, TodayPogPlayerRVA.PogPlayerViewHolder>(PogPlayerDiffCallback()) {

    private var selectedPosition: Int? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PogPlayerViewHolder {
        val binding = ItemTodayPogPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PogPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PogPlayerViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition) {
            // 이전에 선택된 아이템의 포지션을 저장
            val previousSelectedPosition = selectedPosition
            // 현재 선택된 포지션 업데이트
            selectedPosition = if (selectedPosition == position) null else position
            previousSelectedPosition?.let { notifyItemChanged(it) }
            notifyItemChanged(position)

            // 선택된 playerId를 전달
            onPlayerSelected(getItem(position).playerId)
        }
    }

    inner class PogPlayerViewHolder(private val binding: ItemTodayPogPlayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(player: PogPlayerTodayMatchModel.InformationModel, isSelected: Boolean, onItemClick: () -> Unit) {
            Glide.with(binding.root.context)
                .load(player.playerProfileImageUrl)
                .into(binding.ivTodayPogPlayer)

            // 아이템이 선택되었는지 여부에 따라 색상 필터 적용
            if (isSelected) {
                binding.ivTodayPogPlayer.colorFilter = null
            } else {
                // 선택되지 않은 경우 흑백 처리
                val matrix = ColorMatrix().apply { setSaturation(0f) }
                binding.ivTodayPogPlayer.colorFilter = ColorMatrixColorFilter(matrix)
            }
            // 아이템 클릭 시 실행할 리스너 설정
            binding.root.setOnSingleClickListener {
                onItemClick()
            }
        }
    }

    class PogPlayerDiffCallback : DiffUtil.ItemCallback<PogPlayerTodayMatchModel.InformationModel>() {
        override fun areItemsTheSame(oldItem: PogPlayerTodayMatchModel.InformationModel, newItem: PogPlayerTodayMatchModel.InformationModel): Boolean {
            return oldItem.playerId == newItem.playerId
        }

        override fun areContentsTheSame(oldItem: PogPlayerTodayMatchModel.InformationModel, newItem: PogPlayerTodayMatchModel.InformationModel): Boolean {
            return oldItem == newItem
        }
    }
}