package umc.everyones.lck.presentation.mypage.viewingparty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import umc.everyones.lck.databinding.ItemMypageViewingPartyGuestBinding
import umc.everyones.lck.databinding.ItemMypageViewingPartyHostBinding
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.ParticipateViewingPartyMypageModel
import java.util.Date

sealed class ViewingPartyItem {
    data class HostItem(val host : HostViewingPartyMypageModel.HostViewingPartyMypageElementModel): ViewingPartyItem()
    data class GuestItem(val guest : ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel): ViewingPartyItem()
}

class MyPageViewingPartyRVA : ListAdapter<ViewingPartyItem, RecyclerView.ViewHolder>(
    ViewingPartyItemDiffCallback()
){
    companion object {
        private const val TYPE_HOST = 0
        private const val TYPE_GUEST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ViewingPartyItem.HostItem -> TYPE_HOST
            is ViewingPartyItem.GuestItem -> TYPE_GUEST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            TYPE_HOST -> HostViewHolder(ItemMypageViewingPartyHostBinding.inflate(inflater, parent, false))
            TYPE_GUEST -> GuestViewHolder(ItemMypageViewingPartyGuestBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HostViewHolder -> holder.bind(getItem(position) as ViewingPartyItem.HostItem)
            is GuestViewHolder -> holder.bind(getItem(position) as ViewingPartyItem.GuestItem)
        }
    }

    class HostViewHolder(private val binding: ItemMypageViewingPartyHostBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item:ViewingPartyItem.HostItem) {
            binding.tvMypageViewingPartyTitle.text = item.host.name
            binding.tvMypageViewingPartyDate.text = item.host.date
            binding.executePendingBindings()
        }
    }

    class GuestViewHolder(private val binding: ItemMypageViewingPartyGuestBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ViewingPartyItem.GuestItem) {
            binding.tvMypageViewingPartyTitle.text = item.guest.name
            binding.tvMypageViewingPartyDate.text = item.guest.date
            binding.executePendingBindings()
        }
    }
}
class ViewingPartyItemDiffCallback : DiffUtil.ItemCallback<ViewingPartyItem>() {
    override fun areContentsTheSame(oldItem: ViewingPartyItem, newItem: ViewingPartyItem): Boolean {
        return when {
            oldItem is ViewingPartyItem.HostItem && newItem is ViewingPartyItem.HostItem -> oldItem.host.id == newItem.host.id
            oldItem is ViewingPartyItem.GuestItem && newItem is ViewingPartyItem.GuestItem -> oldItem.guest.id == newItem.guest.id
            else -> false
        }
    }

    override fun areItemsTheSame(oldItem: ViewingPartyItem, newItem: ViewingPartyItem): Boolean {
        return oldItem == newItem
    }
}
