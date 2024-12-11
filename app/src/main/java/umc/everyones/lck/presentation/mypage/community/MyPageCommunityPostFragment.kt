package umc.everyones.lck.presentation.mypage.community

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageCommunityPostBinding
import umc.everyones.lck.domain.model.community.Post
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.community.adapter.PostListRVA
import umc.everyones.lck.presentation.community.list.FreeAgentListFragment
import umc.everyones.lck.presentation.community.read.ReadPostActivity
import umc.everyones.lck.util.extension.repeatOnStarted

@AndroidEntryPoint
class MyPageCommunityPostFragment : BaseFragment<FragmentMypageCommunityPostBinding>(R.layout.fragment_mypage_community_post) {
    private val viewModel: MyPageCommunityViewModel by activityViewModels()
    private var _myPostListRVA: MyPostListRVA? = null
    private val myPostListRVA get() = _myPostListRVA

    private var readResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data?.getBooleanExtra("isReadMenuDone", false) == true) {
                myPostListRVA?.refresh() // Refresh the list after reading a post
                binding.rvMypageCommunityPost.scrollToPosition(0) // Scroll to the top
            }
        }
    }

    override fun initObserver() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.myPostPage.collectLatest { pagingData ->
                myPostListRVA?.submitData(pagingData) // PagingData를 어댑터에 제출
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            viewModel.categoryNeedsRefresh.collect { categoryNeedsRefresh ->
                Timber.d("MY POST", categoryNeedsRefresh)
                if (categoryNeedsRefresh == CATEGORY) {
                    myPostListRVA?.refresh()
                    binding.rvMypageCommunityPost.scrollToPosition(0)
                }
            }
        }
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvMypageCommunityPost.layoutManager = layoutManager // RecyclerView에 레이아웃 매니저 설정
        initPostListRVA()
    }

    private fun initPostListRVA() {
        _myPostListRVA = MyPostListRVA { id ->
            readResultLauncher.launch(ReadPostActivity.newIntent(requireContext(), id))
        }
        binding.rvMypageCommunityPost.adapter = myPostListRVA

        _myPostListRVA?.addLoadStateListener { combinedLoadStates ->
            with(binding){
                rvMypageCommunityPost.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                noPostsLayout.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading &&
                        combinedLoadStates.append.endOfPaginationReached &&
                        myPostListRVA?.itemCount == 0
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myPostListRVA = null // Prevent memory leak
    }

    companion object {
        private const val CATEGORY = "MY POST"
    }
}
