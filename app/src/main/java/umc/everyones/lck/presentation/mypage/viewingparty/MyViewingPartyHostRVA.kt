package umc.everyones.lck.presentation.mypage.viewingparty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.everyones.lck.databinding.ItemMypageViewingPartyHostBinding
import umc.everyones.lck.databinding.ItemViewingPartyBinding
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyListModel
import umc.everyones.lck.util.extension.setOnSingleClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyViewingPartyHostRVA(val readViewingParty: (Long) -> Unit) :
    PagingDataAdapter<HostViewingPartyMypageModel.HostViewingPartyMypageElementModel, MyViewingPartyHostRVA.ViewingPartyViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewingPartyViewHolder {
        return ViewingPartyViewHolder(
            ItemMypageViewingPartyHostBinding.inflate(
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

    inner class ViewingPartyViewHolder(private val binding: ItemMypageViewingPartyHostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewingPartyItem: HostViewingPartyMypageModel.HostViewingPartyMypageElementModel) {
            with(binding){
                tvMypageViewingPartyTitle.text = viewingPartyItem.name
                tvMypageViewingPartyDate.text = viewingPartyItem.date
                root.setOnSingleClickListener {
                    readViewingParty(viewingPartyItem.id)
                }
                val currentDate = LocalDate.now()
                val eventDate = LocalDate.parse(viewingPartyItem.date, DateTimeFormatter.ofPattern("yyyy.MM.dd")) // 날짜 형식에 맞게 변경

                if (eventDate.isBefore(currentDate)) {
                    // 날짜가 지나면 수정하기와 개최 취소하기 버튼 숨기기
                    binding.tvMypageViewingPartyEdit.visibility = View.GONE
                    binding.tvCancelButton.visibility = View.GONE
                } else {
                    // 날짜가 지나지 않았으면 버튼 보이기
                    binding.tvMypageViewingPartyEdit.visibility = View.VISIBLE
                    binding.tvCancelButton.visibility = View.VISIBLE
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HostViewingPartyMypageModel.HostViewingPartyMypageElementModel>() {
        override fun areItemsTheSame(oldItem: HostViewingPartyMypageModel.HostViewingPartyMypageElementModel, newItem:HostViewingPartyMypageModel.HostViewingPartyMypageElementModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem:HostViewingPartyMypageModel.HostViewingPartyMypageElementModel, newItem: HostViewingPartyMypageModel.HostViewingPartyMypageElementModel) =
            oldItem == newItem
    }
}