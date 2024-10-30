package umc.everyones.lck.presentation.match

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.response.match.MatchTodayMatchModel
import umc.everyones.lck.domain.model.response.match.TodayMatchInformationModel
import umc.everyones.lck.domain.repository.match.TodayMatchRepository
import javax.inject.Inject

@HiltViewModel
class TodayMatchLckMatchViewModel @Inject constructor(
    private val repository: TodayMatchRepository
): ViewModel() {
    private val _matchData = MutableLiveData<TodayMatchInformationModel?>()
    val matchData: LiveData<TodayMatchInformationModel?> get() = _matchData
    fun fetchTodayMatchInformation(){
        viewModelScope.launch {
            repository.fetchTodayMatchInformation().onSuccess {response ->
                Timber.d("fetchTodayMatchInformation: %s", response.toString())
                _matchData.value = response
            }.onFailure {
                Timber.d("fetchTodayMatchInformation", it.stackTraceToString())
                _matchData.value = null // 실패 시 null 처리
            }
        }
    }
}