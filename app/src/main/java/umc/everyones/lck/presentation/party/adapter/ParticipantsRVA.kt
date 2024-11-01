package umc.everyones.lck.presentation.party.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import timber.log.Timber
import umc.everyones.lck.databinding.ItemParticipantBinding
import umc.everyones.lck.databinding.ItemViewingPartyBinding
import umc.everyones.lck.domain.model.party.ParticipantItem
import umc.everyones.lck.domain.model.party.ViewingPartyItem
import umc.everyones.lck.domain.model.response.party.ViewingPartyParticipantsModel
import umc.everyones.lck.util.extension.setOnSingleClickListener

class ParticipantsRVA(val goToChat: (String) -> Unit) :
    PagingDataAdapter<ViewingPartyParticipantsModel.ParticipantsModel, ParticipantsRVA.ParticipantViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        return ParticipantViewHolder(
            ItemParticipantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = getItem(position)
        if(participant != null) {
            holder.bind(participant)
        }
    }

    inner class ParticipantViewHolder(private val binding: ItemParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(participantItem: ViewingPartyParticipantsModel.ParticipantsModel) {
            with(binding){
                Timber.d("participant $participantItem")
                if(participantItem.isParticipating){
                    ivParticipantsViewingPartyMark.visibility = View.VISIBLE
                } else {
                    ivParticipantsChatMark.visibility = View.VISIBLE
                }

                Glide.with(ivParticipantProfileImage.context)
                    .load(participantItem.image)
                    .into(ivParticipantProfileImage)

                tvParticipantName.text = participantItem.name
                if (participantItem.team == "empty"){
                    tvParticipantDivider.isVisible = false
                    tvParticipantFavoriteTeam.isVisible = false
                } else {
                    tvParticipantFavoriteTeam.text = participantItem.team
                }
                ivParticipantChatBtn.setOnSingleClickListener {
                    goToChat(participantItem.kakaoUserId)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ViewingPartyParticipantsModel.ParticipantsModel>() {
        override fun areItemsTheSame(oldItem: ViewingPartyParticipantsModel.ParticipantsModel, newItem: ViewingPartyParticipantsModel.ParticipantsModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ViewingPartyParticipantsModel.ParticipantsModel, newItem: ViewingPartyParticipantsModel.ParticipantsModel) =
            oldItem == newItem
    }
}