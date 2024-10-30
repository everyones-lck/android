package umc.everyones.lck.presentation.lck

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.about_lck.AboutLckMatchDetailsModel
import umc.everyones.lck.domain.model.about_lck.AboutLckRankingDetailsModel
import umc.everyones.lck.domain.repository.about_lck.AboutLckRepository

import javax.inject.Inject


@HiltViewModel
class AboutLckViewModel @Inject constructor(
    private val repository: AboutLckRepository
) : ViewModel() {

    private val _matchDetails = MutableStateFlow<AboutLckMatchDetailsModel?>(null)
    val matchDetails: StateFlow<AboutLckMatchDetailsModel?> get() = _matchDetails

    private val _rankingDetails = MutableStateFlow<AboutLckRankingDetailsModel?>(null)
    val rankingDetails: StateFlow<AboutLckRankingDetailsModel?> get() = _rankingDetails

    fun fetchLckMatchDetails(searchDate: String){
        viewModelScope.launch{
            val result = repository.fetchLckMatchDetails(searchDate)

            result.onSuccess { response ->
                _matchDetails.value = response
            }.onFailure { exception ->
                Timber.e(exception, "fetchLckMatchDetails API 호출 실패")
            }
        }
    }

    fun fetchLckRanking(seasonName: String, page: Int, size: Int) {
        viewModelScope.launch {
            val result = repository.fetchLckRanking(seasonName, page, size)

            result.onSuccess { response ->
                _rankingDetails.value = response
            }.onFailure { exception ->
                _rankingDetails.value = null
                Timber.e(exception, "fetchLckRanking API 호출 실패")
            }
        }
    }
}

