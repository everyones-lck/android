package umc.everyones.lck.presentation.community.read

import android.util.Log
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import umc.everyones.lck.domain.repository.community.CommunityRepository

@HiltViewModel
class ReadPostViewModel @Inject constructor(
    private val repository: CommunityRepository
) : ViewModel() {
    private val _postId = MutableStateFlow<Long>(-1)
    val postId: StateFlow<Long> get() = _postId

    fun setPostId(postId: Long){
        _postId.value = postId
    }

    fun fetchCommunityPost(){
        viewModelScope.launch {
            repository.fetchCommunityPost(postId.value).onSuccess { response ->
                Log.d("fetchCommunityPost", response.toString())
            }.onFailure {
                Log.d("fetchCommunityPost error", it.stackTraceToString())
            }
        }
    }

    fun deleteCommunityPost(){
        viewModelScope.launch{
            repository.deleteCommunityPost(postId.value).onSuccess { response ->
                Log.d("deleteCommunityPost", response.toString())
            }.onFailure {
                Log.d("deleteCommunityPost error", it.stackTraceToString())
            }
        }
    }
}