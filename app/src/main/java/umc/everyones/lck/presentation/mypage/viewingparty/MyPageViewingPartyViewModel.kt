package umc.everyones.lck.presentation.mypage.viewingparty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.repository.MypageRepository
import umc.everyones.lck.presentation.party.read.ReadViewingPartyViewModel
import umc.everyones.lck.util.network.UiState
import javax.inject.Inject

@HiltViewModel
class MyPageViewingPartyViewModel @Inject constructor(
    private val repository: MypageRepository
): ViewModel() {
    val myPageHostPage = repository.fetchHostPagingSource("HOST").cachedIn(viewModelScope)
    val myPageParticipatePage = repository.fetchParticipatePagingSource("GUEST").cachedIn(viewModelScope)

    private val _categoryMyPageRefresh = MutableStateFlow<String>("HOST")
    val categoryMypageRefresh:StateFlow<String> get() = _categoryMyPageRefresh

    fun fetchMypageViewingPartyHostList(page: Int, size: Int){
        viewModelScope.launch {
            repository.hostViewingPartyMypage(page,size).onSuccess { response->
                Timber.d("fetchMypageViewingPartyHostList", response.toString())
            }.onFailure {
                Timber.tag("fetchMypageViewingPartyHostList Error").d(it.stackTraceToString())
            }
        }
    }

    fun fetchMypageViewingPartyParticipateList(page: Int, size: Int){
        viewModelScope.launch {
            repository.participateViewingPartyMypage(page,size).onSuccess { response->
                Timber.d("fetchMypageViewingPartyParticipateList", response.toString())
            }.onFailure {
                Timber.tag("fetchMypageViewingPartyParticipateList Error").d(it.stackTraceToString())
            }
        }
    }

    fun cancleHostViewingPartyMypage(id:Long){
        viewModelScope.launch {
            repository.cancelHostViewingPartyMypage(id).onSuccess { response ->
                Timber.d("deleteViewingParty", response.toString())
            }.onFailure {
                Timber.tag("deleteViewingParty error").d(it.stackTraceToString())
            }
        }
    }

    fun cancleGuestViewingPartyMypage(id:Long){
        viewModelScope.launch {
            repository.cancelParticipateViewingPartyMypage(id).onSuccess { response->
                Timber.d("cancleGuestViewingPartyMypage", response.toString())
            }.onFailure {
                Timber.tag("cancleGuestViewingPartyMypage error").d(it.stackTraceToString())
            }
        }
    }

    fun refreshCategoryPage(category: String){
        _categoryMyPageRefresh.value = ""
        _categoryMyPageRefresh.value = category
    }
}
