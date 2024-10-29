package umc.everyones.lck.presentation.match

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.request.match.CommonPogModel
import umc.everyones.lck.domain.model.response.match.CommonTodayMatchPogModel
import umc.everyones.lck.domain.model.response.match.TodayMatchSetCountModel
import umc.everyones.lck.domain.repository.match.TodayMatchRepository
import javax.inject.Inject

@HiltViewModel
class TodayMatchLckPogViewModel @Inject constructor(
    private val repository: TodayMatchRepository
): ViewModel() {
    private val _pogData = MutableLiveData<CommonTodayMatchPogModel>()
    val pogData: LiveData<CommonTodayMatchPogModel> get() = _pogData

    // 세트 수를 저장하는 LiveData
    private val _setCount = MutableLiveData<TodayMatchSetCountModel>()
    val setCount: LiveData<TodayMatchSetCountModel> get() = _setCount

    fun fetchTodayMatchPog(matchId: Long) {
        viewModelScope.launch {
            repository.fetchTodayMatchPog(CommonPogModel(matchId)).onSuccess { response ->
                Timber.d("fetchTodayMatchPog %s", response.toString())
                _pogData.value = response
            }.onFailure {
                Timber.d("fetchTodayMatchPog %s", it.stackTraceToString())
            }
        }
    }

    // 세트 수를 불러오는 함수
    fun fetchTodayMatchSetCount(matchId: Long) {
        viewModelScope.launch {
            repository.fetchTodayMatchSetCount(matchId).onSuccess { response ->
                Timber.d("fetchTodayMatchSetCount %s", response.toString())
                _setCount.value = response
            }.onFailure {
                Timber.d("fetchTodayMatchSetCount %s", it.stackTraceToString())
            }
        }
    }
}