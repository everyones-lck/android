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
//    private val _setPogData = MutableLiveData<CommonTodayMatchPogModel>()
//    val setPogData: LiveData<CommonTodayMatchPogModel> get() = _setPogData
//
//    private val _matchPogData = MutableLiveData<CommonTodayMatchPogModel>()
//    val matchPogData: LiveData<CommonTodayMatchPogModel> get() = _matchPogData

    private val _pogData = MutableLiveData<CommonTodayMatchPogModel>()
    val pogData: LiveData<CommonTodayMatchPogModel> get() = _pogData

//    private val _selectedTabIndex = MutableLiveData<Int>()
//    val selectedTabIndex: LiveData<Int> get() = _selectedTabIndex
//
//    fun updateSelectedTab(tabIndex: Int) {
//        _selectedTabIndex.value = tabIndex
//        Timber.d("tabIndex %s", tabIndex.toString())
//    }
    // 세트 수를 저장하는 LiveData
    private val _setCount = MutableLiveData<TodayMatchSetCountModel>()
    val setCount: LiveData<TodayMatchSetCountModel> get() = _setCount

//    // 세트별 POG 데이터를 불러오는 함수
//    fun fetchTodayMatchSetPog(setIndex: Int, matchId: Long) {
//        viewModelScope.launch {
//            repository.fetchTodayMatchSetPog(setIndex, CommonPogModel(matchId)).onSuccess { response ->
//                Timber.d("fetchTodayMatchSetPog %s", response.toString())
//                _setPogData.value = response // 데이터를 LiveData에 저장
//            }.onFailure {
//                Timber.d("fetchTodayMatchSetPog %s", it.stackTraceToString())
//            }
//        }
//    }
//
//    // 매치별 POG 데이터를 불러오는 함수
//    fun fetchTodayMatchMatchPog(matchId: Long) {
//        viewModelScope.launch {
//            repository.fetchTodayMatchMatchPog(CommonPogModel(matchId)).onSuccess { response ->
//                Timber.d("fetchTodayMatchMatchPog %s", response.toString())
//                _matchPogData.value = response // 데이터를 LiveData에 저장
//            }.onFailure {
//                Timber.d("fetchTodayMatchMatchPog %s", it.stackTraceToString())
//            }
//        }
//    }

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