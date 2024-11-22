package umc.everyones.lck.presentation.mypage.viewingparty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.everyones.lck.databinding.ItemMypageViewingPartyGuestBinding
import umc.everyones.lck.databinding.ItemViewingPartyBinding
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.ParticipateViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyListModel
import umc.everyones.lck.util.extension.setOnSingleClickListener

class MyViewingPartyParticipateRVA(val readViewingParty: (Long) -> Unit) :
    PagingDataAdapter<ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel, MyViewingPartyParticipateRVA.ViewingPartyViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewingPartyViewHolder {
        return ViewingPartyViewHolder(
            ItemMypageViewingPartyGuestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewingPartyViewHolder, position: Int) {
        val viewingParty = getItem(position)
        if(viewingParty != null) {
            holder.bind(viewingParty)
        }
    }

    inner class ViewingPartyViewHolder(private val binding: ItemMypageViewingPartyGuestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewingPartyItem: ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel) {
            with(binding){
                tvMypageViewingPartyTitle.text = viewingPartyItem.name
                tvMypageViewingPartyDate.text = viewingPartyItem.date
                root.setOnSingleClickListener {
                    readViewingParty(viewingPartyItem.id)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel>() {
        override fun areItemsTheSame(oldItem: ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel, newItem:ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem:ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel, newItem: ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel) =
            oldItem == newItem
    }
}