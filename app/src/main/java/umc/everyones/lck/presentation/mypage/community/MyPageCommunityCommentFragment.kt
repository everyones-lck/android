package umc.everyones.lck.presentation.mypage.community

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
    private var _myPostListRVA: MyPostListRVA? = null
    private val myPostListRVA get() = _myPostListRVA

    private var readResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data?.getBooleanExtra("isReadMenuDone", false) == true) {
                myPostListRVA?.refresh() // Refresh the list after reading a post
                binding.rvMypageCommunityComment.scrollToPosition(0) // Scroll to the top
            }
        }
    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchCommentMypage.collectLatest { pagingData ->
                myPostListRVA?.submitData(pagingData) // PagingData를 어댑터에 제출
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            viewModel.categoryNeedsRefresh.collect { categoryNeedsRefresh ->
                Timber.d("COMMENT", categoryNeedsRefresh)
                if (categoryNeedsRefresh == CATEGORY) {
                    myPostListRVA?.refresh()
                    binding.rvMypageCommunityComment.scrollToPosition(0)
                }
            }
        }
    }

    override fun initView() {
        initPostListRVA()
    }

    private fun initPostListRVA() {
        _myPostListRVA = MyPostListRVA { postId ->
            readResultLauncher.launch(ReadPostActivity.newIntent(requireContext(), postId))
        }
        binding.rvMypageCommunityComment.adapter = myPostListRVA
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myPostListRVA = null // Prevent memory leak
    }

    companion object {
        private const val CATEGORY = "COMMENT"
    }
}
