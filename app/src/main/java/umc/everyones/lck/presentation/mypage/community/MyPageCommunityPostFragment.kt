package umc.everyones.lck.presentation.mypage.community

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import umc.everyones.lck.R
import umc.everyones.lck.databinding.FragmentMypageCommunityPostBinding
import umc.everyones.lck.domain.model.community.Post
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.presentation.base.BaseFragment
import umc.everyones.lck.presentation.community.adapter.PostListRVA
import umc.everyones.lck.presentation.community.read.ReadPostActivity
import umc.everyones.lck.util.extension.repeatOnStarted

@AndroidEntryPoint
class MyPageCommunityPostFragment : BaseFragment<FragmentMypageCommunityPostBinding>(R.layout.fragment_mypage_community_post) {

    private val viewModel: MyPageCommunityViewModel by activityViewModels()
    private var _myPostListRVA: MyPostListRVA? = null
    private val myPostListRVA
        get() = _myPostListRVA

    private var readResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data?.getBooleanExtra("isReadMenuDone", false) == true) {
                myPostListRVA?.refresh() // Refresh the list after reading a post
                binding.recyclerView.scrollToPosition(0) // Scroll to the top
            }
        }
    }

    override fun initView() {
        initPostListRVAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.viewModel = viewModel
    }

    private fun initPostListRVAdapter() {
        // MyPostListRVA에서 PostsMypageElementModel을 사용하도록 수정
        _myPostListRVA = MyPostListRVA { id ->
            readResultLauncher.launch(ReadPostActivity.newIntent(requireContext(), id.toLong()))
        }
        binding.recyclerView.adapter = myPostListRVA
    }

    override fun initObserver() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _myPostListRVA = null // Prevent memory leak
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.postMypage(page = 0, size = 10) // Load initial data
    }
}
