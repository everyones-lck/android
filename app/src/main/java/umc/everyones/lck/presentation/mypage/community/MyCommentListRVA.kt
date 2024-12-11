package umc.everyones.lck.presentation.mypage.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import umc.everyones.lck.databinding.ItemCommunityPostBinding
import umc.everyones.lck.databinding.ItemMypageCommunityBinding
import umc.everyones.lck.domain.model.community.CommunityListModel
import umc.everyones.lck.domain.model.response.mypage.CommentsMypageModel
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.domain.model.response.mypage.PostsMypageModel
import umc.everyones.lck.util.extension.setOnSingleClickListener

class MyCommentListRVA(val readComment: (Long) -> Unit) : PagingDataAdapter<CommentsMypageModel.CommentsMypageElementModel, MyCommentListRVA.CommentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemMypageCommunityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        if(comment != null) {
            holder.bind(comment)
        }
    }

    inner class CommentViewHolder(private val binding: ItemMypageCommunityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(commentListItem: CommentsMypageModel.CommentsMypageElementModel){
            with(binding){
                tvMypageCommunityTitle.text = commentListItem.content
                tvMypageCommunityCategory.text = "#${commentListItem.postType}"
                // 게시글 postId 전달
                root.setOnSingleClickListener {
                    readComment(commentListItem.postId)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CommentsMypageModel.CommentsMypageElementModel>() {
        override fun areItemsTheSame(oldItem:CommentsMypageModel.CommentsMypageElementModel, newItem: CommentsMypageModel.CommentsMypageElementModel) =
            oldItem.postId == newItem.postId

        override fun areContentsTheSame(oldItem:CommentsMypageModel.CommentsMypageElementModel, newItem: CommentsMypageModel.CommentsMypageElementModel) =
            oldItem == newItem
    }
}
