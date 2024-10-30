package umc.everyones.lck.presentation.party.chat

import android.util.Log
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.response.party.ViewingPartyChatLogModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyChatRoomModel
import umc.everyones.lck.domain.repository.party.ViewingPartyRepository
import umc.everyones.lck.presentation.party.adapter.ChatRVA.Companion.TIME_STAMP
import umc.everyones.lck.util.chat.WebSocketResource
import umc.everyones.lck.util.network.EventFlow
import umc.everyones.lck.util.network.MutableEventFlow
import umc.everyones.lck.util.network.UiState

@HiltViewModel
class ViewingPartyChatViewModel @Inject constructor(
    private val repository: ViewingPartyRepository,
) : ViewModel() {
    private var _pagingSource = repository.fetchChatLogPagingSource("")
    val pagingSource get() = _pagingSource

    private val _postId = MutableStateFlow<Long>(-1)
    val postId: StateFlow<Long> get() = _postId

    private val _participantsId = MutableStateFlow<String>("")
    val participantsId: StateFlow<String> get() = _participantsId

    private val _roomId = MutableStateFlow<String>("")
    val roomId: StateFlow<String> get() = _roomId

    private val _viewingPartyChatEvent =
        MutableStateFlow<UiState<ViewingPartyChatEvent>>(UiState.Empty)
    val viewingPartyChatEvent: StateFlow<UiState<ViewingPartyChatEvent>> get() = _viewingPartyChatEvent

    private val _webSocketEvent = MutableSharedFlow<WebSocketResource>()
    val webSocketEvent: SharedFlow<WebSocketResource> get() = _webSocketEvent

    private val isPageLast = MutableStateFlow<Boolean>(false)
    private val page = MutableStateFlow<Int>(0)
    private val temp = MutableStateFlow<List<ViewingPartyChatLogModel.ChatLogModel>>(emptyList())
    private val previousDate = MutableStateFlow("")


    sealed class ViewingPartyChatEvent {
        data class CreateChatRoom(val result: ViewingPartyChatRoomModel) : ViewingPartyChatEvent()
        data class FetchChatLog(val chatLog: ViewingPartyChatLogModel) : ViewingPartyChatEvent()

        data class RefreshChatLog(val chatLog: ViewingPartyChatLogModel) : ViewingPartyChatEvent()
    }

    fun eventWebsocket(event: WebSocketResource) {
        viewModelScope.launch {
            _webSocketEvent.emit(event)
        }
    }

    fun setPostId(postId: Long, participantsId: String) {
        _postId.value = postId
        _participantsId.value = participantsId
    }

    fun createViewingPartyChatRoom() {
        viewModelScope.launch {
            _viewingPartyChatEvent.value = UiState.Loading
            repository.createViewingPartyChatRoom(postId.value, participantsId.value)
                .onSuccess { response ->
                    _viewingPartyChatEvent.value =
                        UiState.Success(ViewingPartyChatEvent.CreateChatRoom(response))
                    Timber.d("createViewingPartyChatRoom", response.toString())
                    _roomId.value = response.roomId
                }.onFailure {
                    Timber.d("createViewingPartyChatRoom error", it.stackTraceToString())
                }
        }
    }

    fun createViewingPartyChatRoomAsParticipant() {
        viewModelScope.launch {
            _viewingPartyChatEvent.value = UiState.Loading
            repository.createViewingPartyChatRoomAsParticipant(postId.value).onSuccess { response ->
                _viewingPartyChatEvent.value =
                    UiState.Success(ViewingPartyChatEvent.CreateChatRoom(response))
                Timber.d("createViewingPartyChatRoomAsParticipant", response.toString())
                _roomId.value = response.roomId
            }.onFailure {
                Timber.d("createViewingPartyChatRoomAsParticipant error", it.stackTraceToString())
            }
        }
    }

    fun fetchViewingPartyChatLog() {
        if (!isPageLast.value) {
            viewModelScope.launch {
                _viewingPartyChatEvent.value = UiState.Loading
                delay(200)
                repository.fetchViewingPartyChatLog(roomId.value, page.value, 20)
                    .onSuccess { response ->
                        Timber.d("fetchViewingPartyChatLog", response.toString())
                        temp.value += response.chatMessageList.filter { !it.message.contains("입장했습니다.") }
                        isPageLast.value = response.isLast
                        page.value += 1

                        val list = if (response.isLast) {
                            temp.value.toMutableList().apply {
                                if(this.isNotEmpty()) {
                                    this[lastIndex].isLastIndex = true
                                }
                            }
                        } else {
                            temp.value
                        }

                        _viewingPartyChatEvent.value =
                            UiState.Success(
                                ViewingPartyChatEvent.FetchChatLog(
                                    response.copy(chatMessageList = list)
                                )
                            )
                    }.onFailure {
                        Timber.d("createViewingPartyChatRoom error", it.stackTraceToString())
                    }
            }
        }
    }

    fun refreshViewingPartyChatLog() {
        viewModelScope.launch {
            _viewingPartyChatEvent.value = UiState.Loading
            repository.fetchViewingPartyChatLog(roomId.value, 0, 1)
                .onSuccess { response ->
                    Timber.d("fetchViewingPartyChatLog", response.toString())
                    temp.value = response.chatMessageList + temp.value

                    val list = if (response.isLast) {
                        temp.value.toMutableList().apply {
                            if(this.isNotEmpty()) {
                                this[lastIndex].isLastIndex = true
                            }
                        }
                    } else {
                        temp.value
                    }

                    _viewingPartyChatEvent.value =
                        UiState.Success(
                            ViewingPartyChatEvent.FetchChatLog(
                                response.copy(chatMessageList = list)
                            )
                        )

                }.onFailure {
                    Timber.d("createViewingPartyChatRoom error", it.stackTraceToString())
                }
        }
    }
}