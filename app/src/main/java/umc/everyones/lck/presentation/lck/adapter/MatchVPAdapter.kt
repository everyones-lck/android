package umc.everyones.lck.presentation.lck.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import umc.everyones.lck.R
import umc.everyones.lck.databinding.ItemAboutLckMatchesBinding
import umc.everyones.lck.databinding.ItemAboutLckMatchesDetailBinding
import umc.everyones.lck.databinding.ItemLckMatchContentBinding
import umc.everyones.lck.domain.model.about_lck.AboutLckMatchDetailsModel
import umc.everyones.lck.domain.model.party.ViewingPartyItem
import umc.everyones.lck.presentation.lck.AboutLCKFragment
import umc.everyones.lck.presentation.lck.data.MatchData


class MatchVPAdapter :
    ListAdapter<AboutLckMatchDetailsModel.AboutLckMatchByDateModel, MatchVPAdapter.MatchViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        return MatchViewHolder(
            ItemAboutLckMatchesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class MatchViewHolder(private val binding: ItemAboutLckMatchesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(matchDetails: AboutLckMatchDetailsModel.AboutLckMatchByDateModel) {
            binding.rvAboutLckMatchDetails.layoutManager = LinearLayoutManager(itemView.context)

            val adapter = MatchDetailsAdapter()
            binding.rvAboutLckMatchDetails.adapter = adapter

            if (matchDetails.matchDetailList.isEmpty()) {
                adapter.submitList(listOf(null))
            } else {
                adapter.submitList(matchDetails.matchDetailList)
            }

            binding.rvAboutLckMatchDetails.isNestedScrollingEnabled = false // 스크롤 비활성화
        }
    }

    class DiffCallback :
        DiffUtil.ItemCallback<AboutLckMatchDetailsModel.AboutLckMatchByDateModel>() {
        override fun areItemsTheSame(
            oldItem: AboutLckMatchDetailsModel.AboutLckMatchByDateModel,
            newItem: AboutLckMatchDetailsModel.AboutLckMatchByDateModel
        ) =
            oldItem.matchDate == newItem.matchDate

        override fun areContentsTheSame(
            oldItem: AboutLckMatchDetailsModel.AboutLckMatchByDateModel,
            newItem: AboutLckMatchDetailsModel.AboutLckMatchByDateModel
        ) =
            oldItem == newItem
    }
}
