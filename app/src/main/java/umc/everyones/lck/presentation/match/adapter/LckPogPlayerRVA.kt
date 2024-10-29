package umc.everyones.lck.presentation.match.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.w3c.dom.Comment
import umc.everyones.lck.databinding.ItemLckPogPlayerBinding
import umc.everyones.lck.domain.model.response.match.CommonTodayMatchPogModel
import umc.everyones.lck.domain.model.todayMatch.Player

class LckPogPlayerRVA() :
    ListAdapter<CommonTodayMatchPogModel.PogPlayerModel, LckPogPlayerRVA.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLckPogPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size


    inner class ViewHolder(private val binding: ItemLckPogPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommonTodayMatchPogModel.PogPlayerModel) {
//            binding.tvLckPogPlayerName.text = item.name
//            Glide.with(binding.ivLckPogPlayerProfile.context)
//                .load(item.profileImageUrl)
//                .into(binding.ivLckPogPlayerProfile)
            when (item) {
                is CommonTodayMatchPogModel.PogPlayerModel.SetPogResponsesModel -> {
                    binding.tvLckPogPlayerName.text = item.name
                    Glide.with(binding.ivLckPogPlayerProfile.context)
                        .load(item.profileImageUrl)
                        .into(binding.ivLckPogPlayerProfile)
                }
                is CommonTodayMatchPogModel.PogPlayerModel.MatchPogResponseModel -> {
                    binding.tvLckPogPlayerName.text = item.name
                    Glide.with(binding.ivLckPogPlayerProfile.context)
                        .load(item.profileImageUrl)
                        .into(binding.ivLckPogPlayerProfile)
                }
            }
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<CommonTodayMatchPogModel.PogPlayerModel>() {
        override fun areItemsTheSame(oldItem: CommonTodayMatchPogModel.PogPlayerModel, newItem: CommonTodayMatchPogModel.PogPlayerModel): Boolean {
            return when {
                oldItem is CommonTodayMatchPogModel.PogPlayerModel.SetPogResponsesModel && newItem is CommonTodayMatchPogModel.PogPlayerModel.SetPogResponsesModel ->
                    oldItem.playerId == newItem.playerId
                oldItem is CommonTodayMatchPogModel.PogPlayerModel.MatchPogResponseModel && newItem is CommonTodayMatchPogModel.PogPlayerModel.MatchPogResponseModel ->
                    oldItem.playerId == newItem.playerId
                else -> false
            }
        }


        override fun areContentsTheSame(oldItem: CommonTodayMatchPogModel.PogPlayerModel, newItem: CommonTodayMatchPogModel.PogPlayerModel) =
            oldItem == newItem
    }
}