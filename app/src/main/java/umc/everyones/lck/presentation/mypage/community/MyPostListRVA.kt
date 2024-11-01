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
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.domain.model.response.mypage.PostsMypageModel
import umc.everyones.lck.util.extension.setOnSingleClickListener

class MyPostListRVA(val readPost: (Int) -> Unit) : PagingDataAdapter<PostsMypageModel.PostsMypageElementModel, MyPostListRVA.PostViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemMypageCommunityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        if (post != null) {
            holder.bind(post)
        }
    }

    inner class PostViewHolder(private val binding: ItemMypageCommunityBinding) :
        RecyclerView.ViewHolder(binding.tvMypageCommunityShortcuts) {
        fun bind(postListItem: PostsMypageModel.PostsMypageElementModel) { // PostsMypageElementModel로 변경
            with(binding) {
                tvMypageCommunityTitle.text = postListItem.title
                tvMypageCommunityCategory.text = postListItem.postType

                // 게시글 postId 전달
                tvMypageCommunityShortcuts.setOnSingleClickListener {
                    readPost(postListItem.id) // id는 Int로 되어 있으므로 toLong() 필요 없음
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PostsMypageModel.PostsMypageElementModel>() { // 타입 변경
        override fun areItemsTheSame(oldItem: PostsMypageModel.PostsMypageElementModel, newItem: PostsMypageModel.PostsMypageElementModel) =
            oldItem.id == newItem.id // id 비교

        override fun areContentsTheSame(oldItem: PostsMypageModel.PostsMypageElementModel, newItem: PostsMypageModel.PostsMypageElementModel) =
            oldItem == newItem
    }
}
