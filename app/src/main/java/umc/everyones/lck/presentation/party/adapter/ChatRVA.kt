package umc.everyones.lck.presentation.party.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import timber.log.Timber
import umc.everyones.lck.databinding.ItemChatTimeStampBinding
import umc.everyones.lck.databinding.ItemReceiverChatBinding
import umc.everyones.lck.databinding.ItemSenderChatBinding
import umc.everyones.lck.domain.model.response.party.ViewingPartyChatLogModel
import umc.everyones.lck.util.extension.toListViewingPartyDateFormat
import umc.everyones.lck.util.extension.toTimeFormat
import java.security.Timestamp

class ChatRVA :
    ListAdapter<ViewingPartyChatLogModel.ChatLogModel, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            SENDER -> SenderChatViewHolder(ItemSenderChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            RECEIVER -> ReceiverChatViewHolder(ItemReceiverChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TIME_STAMP -> TimeStampViewHolder(ItemChatTimeStampBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalStateException("Invalid position")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is SenderChatViewHolder -> {
                holder.bind(currentList[position])
            }
            is ReceiverChatViewHolder -> {
                holder.bind(currentList[position])
            }
            is TimeStampViewHolder -> {
                holder.bind(currentList[position])
            }
        }
    }

    inner class SenderChatViewHolder(private val binding: ItemSenderChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatItem: ViewingPartyChatLogModel.ChatLogModel) {
            with(binding){
                tvSendererChat.text = chatItem.message
                tvTimeStamp.text = chatItem.createdAt.toTimeFormat()

                if(chatItem.isLastIndex){
                    tvDate.visibility = View.VISIBLE
                    tvDate.text = chatItem.createdAt.toListViewingPartyDateFormat()
                }

                runCatching {
                    currentList[bindingAdapterPosition + 1]
                }.onSuccess {
                    if (chatItem.createdAt.toListViewingPartyDateFormat() != currentList[bindingAdapterPosition + 1].createdAt.toListViewingPartyDateFormat()) {
                        tvDate.visibility = View.VISIBLE
                        tvDate.text = chatItem.createdAt.toListViewingPartyDateFormat()
                        Timber.d("seceer $bindingAdapterPosition ${chatItem.createdAt}")
                    }
                }.onFailure {
                    Timber.d("seceer $it")
                }
            }
        }
    }

    inner class ReceiverChatViewHolder(private val binding: ItemReceiverChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatItem: ViewingPartyChatLogModel.ChatLogModel) {
            with(binding){
                tvReceiverChat.text = chatItem.message
                Glide.with(ivReceiverProfile.context)
                    .load(chatItem.receiverProfileImage)
                    .into(ivReceiverProfile)

                tvReceiverNickname.text = chatItem.senderName
                tvTimeStamp.text = chatItem.createdAt.toTimeFormat()

                if(chatItem.isLastIndex){
                    tvDate.visibility = View.VISIBLE
                    tvDate.text = chatItem.createdAt.toListViewingPartyDateFormat()
                }

                runCatching {
                    currentList[bindingAdapterPosition + 1]
                }.onSuccess {
                    if (chatItem.createdAt.toListViewingPartyDateFormat() != currentList[bindingAdapterPosition + 1].createdAt.toListViewingPartyDateFormat()) {
                        tvDate.visibility = View.VISIBLE
                        tvDate.text = chatItem.createdAt.toListViewingPartyDateFormat()
                        Timber.d("receer $bindingAdapterPosition ${chatItem.createdAt}")
                    }
                }.onFailure {
                    Timber.d("receer $it")
                }
            }
        }
    }

    inner class TimeStampViewHolder(private val binding: ItemChatTimeStampBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatItem: ViewingPartyChatLogModel.ChatLogModel) {
            with(binding){
                tv.text = chatItem.message
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
    }

    class DiffCallback : DiffUtil.ItemCallback<ViewingPartyChatLogModel.ChatLogModel>() {
        override fun areItemsTheSame(oldItem: ViewingPartyChatLogModel.ChatLogModel, newItem: ViewingPartyChatLogModel.ChatLogModel) =
            oldItem.createdAt == newItem.createdAt
        override fun areContentsTheSame(oldItem: ViewingPartyChatLogModel.ChatLogModel, newItem: ViewingPartyChatLogModel.ChatLogModel) =
            oldItem == newItem
    }

    companion object {
        const val SENDER = 0
        const val RECEIVER = 1
        const val TIME_STAMP = 2
    }
}