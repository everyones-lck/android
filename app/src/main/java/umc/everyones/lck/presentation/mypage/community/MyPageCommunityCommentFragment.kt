package umc.everyones.lck.presentation.mypage.community

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageCommunityCommentBinding
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.community.read.ReadPostActivity
import umc.everyones.lck.util.extension.repeatOnStarted

@AndroidEntryPoint
class MyPageCommunityCommentFragment : BaseFragment<FragmentMypageCommunityCommentBinding>(R.layout.fragment_mypage_community_comment) {

    private val viewModel: MyPageCommunityViewModel by activityViewModels()
    private var _myCommentListRVA: MyCommentListRVA? = null
    private val myCommentListRVA get() = _myCommentListRVA

    private var readResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data?.getBooleanExtra("isReadMenuDone", false) == true) {
                myCommentListRVA?.refresh() // Refresh the list after reading a post
                binding.rvMypageCommunityComment.scrollToPosition(0) // Scroll to the top
            }
        }
    }

    override fun initObserver() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.myCommentPage.collectLatest { pagingData ->
                myCommentListRVA?.submitData(pagingData) // PagingData를 어댑터에 제출
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            viewModel.categoryNeedsRefresh.collect { categoryNeedsRefresh ->
                Timber.d("MY COMMENT", categoryNeedsRefresh)
                if (categoryNeedsRefresh == CATEGORY) {
                    myCommentListRVA?.refresh()
                    binding.rvMypageCommunityComment.scrollToPosition(0)
                }
            }
        }
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvMypageCommunityComment.layoutManager = layoutManager // RecyclerView에 레이아웃 매니저 설정
        initPostListRVA()
    }

    private fun initPostListRVA() {
        _myCommentListRVA = MyCommentListRVA { postId ->
            readResultLauncher.launch(ReadPostActivity.newIntent(requireContext(), postId))
        }
        binding.rvMypageCommunityComment.adapter = myCommentListRVA

        _myCommentListRVA?.addLoadStateListener { combinedLoadStates ->
            with(binding){
                rvMypageCommunityComment.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                noCommentsLayout.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading &&
                        combinedLoadStates.append.endOfPaginationReached &&
                        myCommentListRVA?.itemCount == 0
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myCommentListRVA = null // Prevent memory leak
    }

    companion object {
        private const val CATEGORY = "MY COMMENT"
    }
}
