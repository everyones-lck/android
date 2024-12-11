package umc.everyones.lck.presentation.party.read

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.response.party.ReadViewingPartyModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyParticipantsModel
import umc.everyones.lck.domain.repository.party.ViewingPartyRepository
import umc.everyones.lck.util.network.EventFlow
import umc.everyones.lck.util.network.MutableEventFlow
import umc.everyones.lck.util.network.UiState

@HiltViewModel
class ReadViewingPartyViewModel @Inject constructor(
    private val repository: ViewingPartyRepository,
    private val spf: SharedPreferences
) : ViewModel() {
    private val _title = MutableStateFlow<String>("")
    val title: StateFlow<String> get() = _title

    private val _postId = MutableStateFlow<Long>(-1)
    val postId: StateFlow<Long> get() = _postId

    private val _readViewingPartyEvent = MutableStateFlow<UiState<ReadViewingPartyEvent>>(UiState.Empty)
    val readViewingPartyEvent: StateFlow<UiState<ReadViewingPartyEvent>> get() = _readViewingPartyEvent

    private val _isWriter = MutableEventFlow<Boolean>()
    val isWriter: EventFlow<Boolean> get() = _isWriter

    private var _viewingPartyParticipantsPage = repository.fetchViewingPartyParticipantsPagingSource(postId.value).cachedIn(viewModelScope)

    val viewingPartyParticipantsPage get() = _viewingPartyParticipantsPage
    sealed class ReadViewingPartyEvent {
        data class ReadViewingParty(val viewingParty: ReadViewingPartyModel): ReadViewingPartyEvent()
        data object JoinViewingParty: ReadViewingPartyEvent()

        data object DeleteViewingParty: ReadViewingPartyEvent()

        data class ReadParticipants(val participants: ViewingPartyParticipantsModel): ReadViewingPartyEvent()

        data class WriteDoneViewingParty(val isWriteDone: Boolean): ReadViewingPartyEvent()
    }
    fun setTitle(title: String) {
        _title.value = title
    }

    fun setPostId(postId: Long){
        _postId.value = postId
        _viewingPartyParticipantsPage = repository.fetchViewingPartyParticipantsPagingSource(postId).cachedIn(viewModelScope)
    }

    fun fetchViewingParty() {
        viewModelScope.launch {
            _readViewingPartyEvent.value = UiState.Loading
            repository.fetchViewingParty(postId.value).onSuccess { response ->
                Timber.d("fetchViewingParty", response.toString())
                _readViewingPartyEvent.value = UiState.Success(ReadViewingPartyEvent.ReadViewingParty(response))
                val writerName = response.writerInfo.split("|").first().trim()
                Timber.d("writername", writerName.toString())
                _isWriter.emit(spf.getString("nickName", "").toString() == writerName)
            }.onFailure {
                Timber.d("fetchViewingParty error", it.stackTraceToString())
                _readViewingPartyEvent.value = UiState.Failure("뷰잉파티를 조회하지 못했습니다")
            }
        }
    }

    fun joinViewingParty(){
        viewModelScope.launch{
            _readViewingPartyEvent.value = UiState.Loading
            repository.joinViewingParty(postId.value).onSuccess { response ->
                Timber.d("joinViewingParty", response.toString())
                _readViewingPartyEvent.value = UiState.Success(ReadViewingPartyEvent.JoinViewingParty)
            }.onFailure {
                Timber.d("joinViewingParty error", it.stackTraceToString())
                _readViewingPartyEvent.value = UiState.Failure("뷰잉파티에 참여하지 못했습니다")
            }
        }
    }

    fun deleteViewingParty(){
        viewModelScope.launch {
            _readViewingPartyEvent.value = UiState.Loading
            repository.deleteViewingParty(postId.value).onSuccess { response ->
                Timber.d("deleteViewingParty", response.toString())
                _readViewingPartyEvent.value = UiState.Success(ReadViewingPartyEvent.DeleteViewingParty)
            }.onFailure {
                Timber.d("deleteViewingParty error", it.stackTraceToString())
                _readViewingPartyEvent.value = UiState.Failure("뷰잉파티를 삭제하지 못했습니다")
            }
        }
    }

    fun fetchViewingPartyParticipants(){
        viewModelScope.launch {
            _readViewingPartyEvent.value = UiState.Loading
            repository.fetchViewingPartyParticipants(postId.value, 0 ,10).onSuccess { response ->
                _readViewingPartyEvent.value = UiState.Success(ReadViewingPartyEvent.ReadParticipants(response))
                Timber.d("fetchViewingPartyParticipants", response.toString())
            }.onFailure {
                Timber.d("deleteViewingParty error", it.stackTraceToString())
                _readViewingPartyEvent.value = UiState.Failure("뷰잉파티 참가자를 조회하지 못했습니다")
            }
        }
    }

    private var _viewingParty: ReadViewingPartyModel? = null
    val viewingParty: ReadViewingPartyModel?
        get() = _viewingParty

    fun fetchViewingParty(postId: Long) {
        viewModelScope.launch {
            Timber.d("fetchViewingParty called with id: $postId") // 로그 추가
            _readViewingPartyEvent.value = UiState.Loading
            repository.fetchViewingParty(postId).onSuccess { response ->
                Timber.d("fetchViewingParty", response.toString())
                _viewingParty = ReadViewingPartyModel(
                    name = response.name,
                    writerInfo = response.writerInfo,
                    ownerImage = response.ownerImage,
                    qualify = response.qualify,
                    partyDate = response.partyDate,
                    place = response.place,
                    latitude = response.latitude,
                    longitude = response.longitude,
                    price = response.price,
                    participants = response.participants,
                    etc = response.etc,
                    isParticipated = response.isParticipated
                )
                _readViewingPartyEvent.value = UiState.Success(ReadViewingPartyEvent.ReadViewingParty(response))
            }.onFailure {
                Timber.d("fetchViewingParty error", it.stackTraceToString())
                _readViewingPartyEvent.value = UiState.Failure("뷰잉파티를 조회하지 못했습니다")
            }
        }
    }
}