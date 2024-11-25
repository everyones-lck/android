package umc.everyones.lck.presentation.mypage.viewingparty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import umc.everyones.lck.R
import umc.everyones.lck.databinding.ItemMypageViewingPartyHostBinding
import umc.everyones.lck.databinding.ItemViewingPartyBinding
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyListModel
import umc.everyones.lck.util.extension.setOnSingleClickListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyViewingPartyHostRVA(
    val readViewingParty: (Long) -> Unit,
    val deleteViewingParty: (Long) -> Unit,
    val onEditViewingParty: (Long) -> Unit // 수정하기 콜백 추가
) : PagingDataAdapter<HostViewingPartyMypageModel.HostViewingPartyMypageElementModel, MyViewingPartyHostRVA.ViewingPartyViewHolder>(DiffCallback()) {

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
        if (viewingParty != null) {
            holder.bind(viewingParty)
        }
    }

    inner class ViewingPartyViewHolder(private val binding: ItemMypageViewingPartyHostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewingPartyItem: HostViewingPartyMypageModel.HostViewingPartyMypageElementModel) {
            with(binding) {
                tvMypageViewingPartyTitle.text = viewingPartyItem.name
                tvMypageViewingPartyDate.text = viewingPartyItem.date

                tvMypageViewingPartyShortcuts.setOnSingleClickListener {
                    readViewingParty(viewingPartyItem.id)
                }

                // 수정하기 버튼 클릭 리스너 추가
                tvMypageViewingPartyEdit.setOnSingleClickListener {
                    onEditViewingParty(viewingPartyItem.id) // 수정하기 콜백 호출
                }

                // 날짜 비교
                val currentDate = LocalDate.now()
                val eventDate = LocalDate.parse(viewingPartyItem.date, DateTimeFormatter.ofPattern("yyyy.MM.dd"))

                if (eventDate.isBefore(currentDate)) {
                    binding.root.setBackgroundResource(R.drawable.bg_mypage_community)
                    linearLayout.visibility = View.GONE
                } else {
                    binding.root.setBackgroundResource(R.drawable.bg_mypage_viewing_party)
                    linearLayout.visibility = View.VISIBLE
                    tvCancelButton.setOnSingleClickListener {
                        deleteViewingParty(viewingPartyItem.id)
                    }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HostViewingPartyMypageModel.HostViewingPartyMypageElementModel>() {
        override fun areItemsTheSame(oldItem: HostViewingPartyMypageModel.HostViewingPartyMypageElementModel, newItem: HostViewingPartyMypageModel.HostViewingPartyMypageElementModel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: HostViewingPartyMypageModel.HostViewingPartyMypageElementModel, newItem: HostViewingPartyMypageModel.HostViewingPartyMypageElementModel) =
            oldItem == newItem
    }
}
