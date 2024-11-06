package umc.everyones.lck.presentation.community.read

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.request.community.CreateCommentRequestModel
import umc.everyones.lck.domain.model.request.community.EditCommunityRequestModel
import umc.everyones.lck.domain.model.response.community.ReadCommunityResponseModel
import umc.everyones.lck.domain.repository.community.CommunityRepository
import umc.everyones.lck.presentation.party.chat.ViewingPartyChatViewModel
import umc.everyones.lck.util.network.EventFlow
import umc.everyones.lck.util.network.MutableEventFlow
import umc.everyones.lck.util.network.UiState

@HiltViewModel
class ReadPostViewModel @Inject constructor(
    private val repository: CommunityRepository,
    private val spf: SharedPreferences
) : ViewModel() {
    private val _postId = MutableStateFlow<Long>(-1)
    val postId: StateFlow<Long> get() = _postId

    fun setPostId(postId: Long){
        _postId.value = postId
    }

    private val _readCommunityEvent = MutableStateFlow<UiState<ReadCommunityEvent>>(UiState.Empty)
    val readCommunityEvent: StateFlow<UiState<ReadCommunityEvent>> get() = _readCommunityEvent

    private val _isWriter = MutableEventFlow<Boolean>()
    val isWriter: EventFlow<Boolean> get() = _isWriter

    private val _imageUrl = MutableStateFlow<String>("")
    val imageUrl: StateFlow<String> get() = _imageUrl

    sealed class ReadCommunityEvent{
        data class ReadPost(val post: ReadCommunityResponseModel): ReadCommunityEvent()

        data object EditPost: ReadCommunityEvent()

        data object DeletePost : ReadCommunityEvent()

        data object ReportPost : ReadCommunityEvent()

        data object ReportComment : ReadCommunityEvent()

        data object CreateComment : ReadCommunityEvent()

        data object DeleteComment : ReadCommunityEvent()
    }

    fun setImageUrl(url: String){
        _imageUrl.value = ""
        _imageUrl.value = url
    }

    fun fetchCommunityPost(){
        viewModelScope.launch {
            _readCommunityEvent.value = UiState.Loading
            repository.fetchCommunityPost(postId.value).onSuccess { response ->
                Timber.d("fetchCommunityPost", response.toString())
                _readCommunityEvent.value = UiState.Success(ReadCommunityEvent.ReadPost(response))
                _isWriter.emit(spf.getString("nickName", "") == response.writerInfo.split("|")[0].trim())
            }.onFailure {
                Timber.tag("fetchCommunityPost error").d(it.stackTraceToString())
                _readCommunityEvent.value = UiState.Failure("커뮤니티 게시글 상세조회에 실패했습니다")
            }
        }
    }

    fun deleteCommunityPost(){
        viewModelScope.launch{
            _readCommunityEvent.value = UiState.Loading
            repository.deleteCommunityPost(postId.value).onSuccess { response ->
                Timber.d("deleteCommunityPost", response.toString())
                _readCommunityEvent.value = UiState.Success(ReadCommunityEvent.DeletePost)
            }.onFailure {
                Timber.d("deleteCommunityPost error", it.stackTraceToString())
                _readCommunityEvent.value = UiState.Failure("커뮤니티 게시글 삭제에 실패했습니다")
            }
        }
    }

    fun reportCommunityPost(){
        viewModelScope.launch {
            _readCommunityEvent.value = UiState.Loading
            repository.reportCommunityPost(postId.value).onSuccess { response ->
                Timber.d("reportCommunityPost", response.toString())
                _readCommunityEvent.value = UiState.Success(ReadCommunityEvent.ReportPost)
            }.onFailure {
                Timber.d("reportCommunityPost error", it.message.toString())
                if(it.message?.trim() == "HTTP 404"){
                    _readCommunityEvent.value = UiState.Failure("이미 신고한 게시글입니다")
                } else {
                    _readCommunityEvent.value = UiState.Failure("커뮤니티 게시글 신고에 실패했습니다")
                }
            }
        }
    }

    fun reportCommunityComment(commentId: Long){
        viewModelScope.launch {
            _readCommunityEvent.value = UiState.Loading
            repository.reportCommunityComment(commentId).onSuccess { response ->
                Timber.d("reportCommunityComment", response.toString())
                _readCommunityEvent.value = UiState.Success(ReadCommunityEvent.ReportComment)
            }.onFailure {
                Timber.d("reportCommunityComment error", it.message.toString())
                if(it.message?.trim() == "HTTP 404"){
                    _readCommunityEvent.value = UiState.Failure("이미 신고한 댓글입니다")
                } else {
                    _readCommunityEvent.value = UiState.Failure("커뮤니티 댓글 신고에 실패했습니다")
                }
            }
        }
    }

    fun createComment(content: String){
        viewModelScope.launch {
            _readCommunityEvent.value = UiState.Loading
            repository.createComment(postId.value, CreateCommentRequestModel(content)).onSuccess { response ->
                Timber.d("createComment", response.toString())
                _readCommunityEvent.value = UiState.Success(ReadCommunityEvent.CreateComment)
                fetchCommunityPost()
            }.onFailure {
                Timber.d("createComment error", it.message.toString())
                _readCommunityEvent.value = UiState.Failure("커뮤니티 댓글 생성에 실패했습니다")
            }
        }
    }

    fun deleteComment(commentId: Long){
        viewModelScope.launch {
            _readCommunityEvent.value = UiState.Loading
            repository.deleteComment(commentId).onSuccess { response ->
                Timber.d("deleteComment", response.toString())
                _readCommunityEvent.value = UiState.Success(ReadCommunityEvent.DeleteComment)
                fetchCommunityPost()
            }.onFailure {
                Timber.d("deleteComment error", it.message.toString())
                _readCommunityEvent.value = UiState.Failure("커뮤니티 댓글 삭제에 실패했습니다")
            }
        }
    }
}