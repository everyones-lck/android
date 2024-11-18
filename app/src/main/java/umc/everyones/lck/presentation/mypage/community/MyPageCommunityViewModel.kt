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
    val myPostPage = repository.fetchPostPagingSource("MY POST").cachedIn(viewModelScope)
    val myCommentPage = repository.fetchCommentPagingSource("MY COMMENT").cachedIn(viewModelScope)

    private val _categoryNeedsRefresh = MutableStateFlow<String>("MY POST")
    val categoryNeedsRefresh:StateFlow<String> get() = _categoryNeedsRefresh

    fun fetchMypageCommunityPostList(page: Int, size: Int){
        viewModelScope.launch {
            repository.postsMypage(page,size).onSuccess {response ->
                Timber.d("fetchMypageCommunityPostList", response.toString())
            }.onFailure {
                Timber.tag("fetchMypageCommunityPostList Error").d(it.stackTraceToString())
            }
        }
    }

    fun fetchMypageCommunityCommentList(page: Int, size: Int){
        viewModelScope.launch {
            repository.commentsMypage(page,size).onSuccess { response->
                Timber.d("fetchMypageCommunityCommentList", response.toString())
            }.onFailure {
                Timber.tag("fetchMypageCommunityCommentList Error").d(it.stackTraceToString())
            }
        }
    }

    fun refreshCategoryPage(category: String){
        _categoryNeedsRefresh.value = ""
        _categoryNeedsRefresh.value = category
    }
}