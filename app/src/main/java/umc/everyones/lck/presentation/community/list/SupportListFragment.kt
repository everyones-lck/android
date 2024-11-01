package umc.everyones.lck.presentation.community.list

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentPostListBinding
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.community.CommunityViewModel
import umc.everyones.lck.presentation.community.adapter.PostListRVA
import umc.everyones.lck.presentation.community.read.ReadPostActivity
import umc.everyones.lck.util.extension.repeatOnStarted

class SupportListFragment  : BaseFragment<FragmentPostListBinding>(R.layout.fragment_post_list) {
    private val viewModel: CommunityViewModel by activityViewModels()
    private var _postListRVA: PostListRVA? = null
    private val postListRVA
        get() = _postListRVA

    private var readResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            if(result.data?.getBooleanExtra("isReadMenuDone", false) == true){
                _postListRVA?.refresh()
                binding.rvPostList.scrollToPosition(0)
            }
        }
    }

    override fun initObserver() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.supportListPage.collectLatest { data ->
                postListRVA?.submitData(data)
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            viewModel.categoryNeedsRefresh.collect { categoryNeedsRefresh ->
                Timber.d("support", categoryNeedsRefresh)
                if (categoryNeedsRefresh == CATEGORY) {
                    postListRVA?.refresh()
                    binding.rvPostList.scrollToPosition(0)
                }
            }
        }
    }

    override fun initView() {
        initPostListRVAdapter()
    }

    private fun initPostListRVAdapter() {
        _postListRVA = PostListRVA { postId ->
            readResultLauncher.launch(ReadPostActivity.newIntent(requireContext(), postId))
        }
        binding.rvPostList.adapter = postListRVA

        _postListRVA?.addLoadStateListener { combinedLoadStates ->
            with(binding){
                layoutShimmer.isVisible = combinedLoadStates.source.refresh is LoadState.Loading
                rvPostList.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                if(combinedLoadStates.source.refresh is LoadState.Loading){
                    layoutShimmer.startShimmer()
                }

                if(combinedLoadStates.source.refresh is LoadState.NotLoading){
                    layoutShimmer.stopShimmer()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _postListRVA = null
    }

    companion object {
        private const val CATEGORY = "응원"
    }
}