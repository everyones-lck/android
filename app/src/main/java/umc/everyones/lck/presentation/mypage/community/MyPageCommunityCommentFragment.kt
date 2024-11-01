package umc.everyones.lck.presentation.mypage.community

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageCommunityCommentBinding
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.presentation.base.BaseFragment

@AndroidEntryPoint
class MyPageCommunityCommentFragment : BaseFragment<FragmentMypageCommunityCommentBinding>(R.layout.fragment_mypage_community_comment) {

    private val viewModel: MyPageCommunityViewModel by activityViewModels()
    private lateinit var adapter: MyPageCommunityItemAdapter

    override fun initView() {
        adapter = MyPageCommunityItemAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun initObserver() {
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts.map { postElement ->
                CommunityItem.PostItem(
                    post = MyPost(
                        id = postElement.id,
                        title = postElement.title,
                        postType = postElement.postType
                    )
                )
            })
        }
    }
}
