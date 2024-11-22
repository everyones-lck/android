package umc.everyones.lck.presentation.mypage.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import umc.everyones.lck.databinding.ItemMypageCommunityBinding
import umc.everyones.lck.domain.model.response.mypage.MyComment
import umc.everyones.lck.domain.model.response.mypage.MyPost

sealed class CommunityItem {
    data class PostItem(val post: MyPost) : CommunityItem()
    data class CommentItem(val comment: MyComment) : CommunityItem()
}

class MyPageCommunityRVA : ListAdapter<CommunityItem, RecyclerView.ViewHolder>(
    CommunityItemDiffCallback()
) {
    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_COMMENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CommunityItem.PostItem -> TYPE_POST
            is CommunityItem.CommentItem -> TYPE_COMMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_POST -> PostViewHolder(ItemMypageCommunityBinding.inflate(inflater, parent, false))
            TYPE_COMMENT -> CommentViewHolder(ItemMypageCommunityBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> holder.bind(getItem(position) as CommunityItem.PostItem)
            is CommentViewHolder -> holder.bind(getItem(position) as CommunityItem.CommentItem)
        }
    }

    class PostViewHolder(private val binding: ItemMypageCommunityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommunityItem.PostItem) {
            binding.tvMypageCommunityTitle.text = item.post.title
            binding.tvMypageCommunityCategory.text = item.post.postType
            binding.executePendingBindings()
        }
    }

    class CommentViewHolder(private val binding: ItemMypageCommunityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CommunityItem.CommentItem) {
            val truncatedBody = if (item.comment.content.length > 20) {
                "${item.comment.content.take(20)}..."
            } else {
                item.comment.content
            }

            binding.tvMypageCommunityTitle.text = truncatedBody
            binding.tvMypageCommunityCategory.text = item.comment.postType
            binding.tvMypageCommunityShortcuts.text = "해당 댓글 바로가기"
            binding.executePendingBindings()
        }
    }
}

class CommunityItemDiffCallback : DiffUtil.ItemCallback<CommunityItem>() {
    override fun areItemsTheSame(oldItem: CommunityItem, newItem: CommunityItem): Boolean {
        return when {
            oldItem is CommunityItem.PostItem && newItem is CommunityItem.PostItem -> oldItem.post.id == newItem.post.id
            oldItem is CommunityItem.CommentItem && newItem is CommunityItem.CommentItem -> oldItem.comment.id == newItem.comment.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: CommunityItem, newItem: CommunityItem): Boolean {
        return oldItem == newItem
    }
}
