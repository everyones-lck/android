package umc.everyones.lck.presentation.mypage.community

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.community.Comment
import umc.everyones.lck.domain.model.community.Post
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.domain.model.response.mypage.PostsMypageModel
import umc.everyones.lck.domain.repository.MypageRepository
import umc.everyones.lck.domain.repository.community.CommunityRepository
import umc.everyones.lck.presentation.community.read.ReadPostViewModel
import umc.everyones.lck.presentation.mypage.MyPageViewModel
import umc.everyones.lck.util.network.UiState
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MyPageCommunityViewModel @Inject constructor(
    private val repository: MypageRepository,
) : ViewModel() {
    private val _categoryNeedsRefresh = MutableStateFlow<String>("")
    val categoryNeedsRefresh: StateFlow<String> get() = _categoryNeedsRefresh
    private val _posts = MutableStateFlow<List<MyPost>>(emptyList())

    val posts: StateFlow<List<MyPost>> get() = _posts
    val fetchPostsMypage = repository.fetchPagingSource("POST").cachedIn(viewModelScope)
    val fetchCommentMypage = repository.fetchPagingSource("COMMENT").cachedIn(viewModelScope)

    fun postMypage(page: Int, size: Int): Flow<List<MyPost>> = flow {
        repository.postsMypage(page, size).onSuccess { response ->
            val postList = response.posts.map { postElement ->
                MyPost(
                    id = postElement.id,
                    title = postElement.title,
                    postType = postElement.postType,
                )
            }
            _posts.value = postList
            emit(postList)
        }.onFailure { error ->
            Timber.d("postMypage error", error.stackTraceToString())
            emit(emptyList())
        }
    }

    fun refreshCategoryPage(category: String){
        _categoryNeedsRefresh.value = ""
        _categoryNeedsRefresh.value = category
    }
}