package umc.everyones.lck.presentation.lck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.about_lck.AboutLckPlayerModel
import umc.everyones.lck.domain.repository.about_lck.AboutLckRepository
import javax.inject.Inject

@HiltViewModel
class AboutLckPlayerCareerViewModel @Inject constructor(
    private val repository: AboutLckRepository
) : ViewModel() {
    private val _winningCareer = MutableStateFlow<List<String>>(emptyList())
    val winningCareer: StateFlow<List<String>> get() = _winningCareer

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> get() = _history

    private val _player = MutableStateFlow<AboutLckPlayerModel?>(null)
    val player: StateFlow<AboutLckPlayerModel?> get() = _player

    fun fetchLckWinningCareer(playerId: Int, page: Int, size: Int) {
        viewModelScope.launch {
            val result = repository.fetchLckWinningCareer(playerId, page, size)

            result.onSuccess { response ->
                Timber.d("fetchLckWinningCareer API 호출 성공")
                val seasonName = response.seasonNames
                _winningCareer.update { seasonName }
            }.onFailure {
                Timber.e(it, "fetchLckWinningCareer API 호출 실패")
            }
        }
    }

    fun fetchLckHistory(playerId: Int, page: Int, size: Int) {
        viewModelScope.launch {
            val result = repository.fetchLckHistory(playerId, page, size)

            result.onSuccess { response ->
                Timber.d("fetchLckHistory API 호출 성공")
                val history = response.seasonTeamDetails.map { detail ->
                    "${detail.seasonName.substring(0, 4)} ${detail.teamName}"
                }
                _history.update { history }
            }.onFailure {
                Timber.e(it, "fetchLckHistory API 호출 실패")
            }
        }
    }

    fun fetchLckPlayer(playerId: Int) {
        viewModelScope.launch {
            val result = repository.fetchLckPlayer(playerId)

            result.onSuccess { response ->
                Timber.d("fetchLckPlayer API 호출 성공")
                _player.update { response }
            }.onFailure { exception ->
                Timber.e(exception, "fetchLckPlayer API 호출 실패")
                _player.update { null }
            }
        }
    }
}