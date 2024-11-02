package umc.everyones.lck.presentation.mypage.community

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.community.Comment
import umc.everyones.lck.domain.model.community.Post
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
    private val _posts = MutableLiveData<List<PostsMypageModel.PostsMypageElementModel>>() // PostsMypageElementModel 타입 리스트
    val posts: LiveData<List<PostsMypageModel.PostsMypageElementModel>> get() = _posts

    fun postMypage(page: Int, size: Int) {
        viewModelScope.launch {
            repository.postsMypage(page, size).onSuccess { response ->
                _posts.value = response.posts // API 응답에서 posts 리스트를 가져옴
                Timber.d("postMypage", response.toString())
            }.onFailure { error ->
                Timber.d("postMypage error", error.stackTraceToString())
            }
        }
    }
}
