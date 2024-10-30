package umc.everyones.lck.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.data.dto.response.home.HomeTodayMatchResponseDto
import umc.everyones.lck.domain.model.response.home.HomeTodayMatchModel
import umc.everyones.lck.domain.repository.TestRepository
import umc.everyones.lck.domain.repository.home.HomeRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor( // @Inject : 의존성 주입을 받겠다.
    private val repository: HomeRepository
) : ViewModel() {
    private val _navigateEvent = MutableSharedFlow<Int>()
    val navigateEvent: SharedFlow<Int> get() = _navigateEvent

    private val _matchData = MutableLiveData<HomeTodayMatchModel?>()
    val matchData: LiveData<HomeTodayMatchModel?> get() = _matchData

    fun setNavigateEvent(navigateEvent: Int) {
        viewModelScope.launch {
            _navigateEvent.emit(navigateEvent)
        }
    }
    fun fetchHomeTodayMatchInformation(){
        viewModelScope.launch {
            repository.fetchHomeTodayMatchInformation().onSuccess {response ->
                Timber.d("fetchHomeTodayMatchInformation %s", response.toString())
                _matchData.value = response
            }.onFailure {
                Timber.d("fetchHomeTodayMatchInformation %s", it.stackTraceToString())
                _matchData.value = null // 실패시 null 처리
            }
        }
    }
}