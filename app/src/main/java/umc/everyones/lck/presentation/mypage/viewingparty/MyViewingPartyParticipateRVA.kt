package umc.everyones.lck.presentation.mypage.viewingparty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.everyones.lck.R
import umc.everyones.lck.databinding.ItemMypageViewingPartyGuestBinding
import umc.everyones.lck.databinding.ItemViewingPartyBinding
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.ParticipateViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyListModel
import umc.everyones.lck.util.extension.setOnSingleClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyViewingPartyParticipateRVA(
    val readViewingParty: (Long) -> Unit,
    val deleteViewingParty: (Long) -> Unit // 삭제 메소드를 위한 콜백 추가
) : PagingDataAdapter<ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel, MyViewingPartyParticipateRVA.ViewingPartyViewHolder>(DiffCallback()) {

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
        if (viewingParty != null) {
            holder.bind(viewingParty)
        }
    }

    inner class ViewingPartyViewHolder(private val binding: ItemMypageViewingPartyGuestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewingPartyItem: ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel) {
            with(binding) {
                tvMypageViewingPartyTitle.text = viewingPartyItem.name
                tvMypageViewingPartyDate.text = viewingPartyItem.date

                tvMypageViewingPartyShortcuts.setOnSingleClickListener {
                    readViewingParty(viewingPartyItem.id)
                }

                val currentDate = LocalDate.now()
                val eventDate = LocalDate.parse(viewingPartyItem.date, DateTimeFormatter.ofPattern("yyyy.MM.dd")) // 날짜 형식에 맞게 변경

                if (eventDate.isBefore(currentDate)) {
                    binding.root.setBackgroundResource(R.drawable.bg_mypage_community) // 날짜가 지난 경우 사용할 배경
                    linearLayout.visibility = View.GONE // LinearLayout 숨기기
                } else {
                    binding.root.setBackgroundResource(R.drawable.bg_mypage_viewing_party)
                    linearLayout.visibility = View.VISIBLE // LinearLayout 보이기
                    tvCancelButton.setOnSingleClickListener {
                        deleteViewingParty(viewingPartyItem.id) // 삭제 메소드 호출
                    }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel>() {
        override fun areItemsTheSame(oldItem: ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel, newItem: ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel, newItem: ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel) =
            oldItem == newItem
    }
}
