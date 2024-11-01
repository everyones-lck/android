package umc.everyones.lck.presentation.match

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import umc.everyones.lck.domain.model.request.match.VoteMatchModel
import umc.everyones.lck.domain.model.request.match.VoteMatchPogModel
import umc.everyones.lck.domain.model.response.match.CommonVotePogModel
import umc.everyones.lck.domain.model.response.match.MatchTodayMatchModel
import umc.everyones.lck.domain.repository.match.TodayMatchRepository
import javax.inject.Inject

@HiltViewModel
class TodayMatchPredictionViewModel @Inject constructor(
    private val repository: TodayMatchRepository
): ViewModel() {
    // 현재 경기 데이터를 저장하는 LiveData
    private val _matchData = MutableLiveData<MatchTodayMatchModel>()
    val matchData: LiveData<MatchTodayMatchModel> get() = _matchData
    // 투표 결과 메시지를 저장하는 LiveData
    private val _voteResponse = MutableLiveData<String>()
    val voteResponse: LiveData<String> get() = _voteResponse

    // 투표 데이터를 가져오는 함수
    fun fetchTodayMatchVoteMatch(matchId: Long) {
        viewModelScope.launch {
            repository.fetchTodayMatchVoteMatch(matchId).onSuccess { response ->
                Timber.d("fetchTodayMatchVoteMatch %s", response.toString())
                _matchData.value = response
            }.onFailure {
                Timber.d("fetchTodayMatchVoteMatch %s", it.stackTraceToString())
            }
        }
    }
    // 투표를 처리하는 함수
    fun voteMatch(matchId: Long, teamId: Int) {
        viewModelScope.launch {
            repository.voteMatch(VoteMatchModel(matchId, teamId)).onSuccess { response ->
                Timber.d("voteMatch %s", response.toString())
                _voteResponse.value = response.message // API에서 전달된 메시지를 ViewModel에 저장
            }.onFailure { exception ->
                Timber.d("voteMatch %s", exception.stackTraceToString())
                _voteResponse.value = getErrorMessageFromException(exception) // 예외 메시지 처리
            }
        }
    }
    // 예외로부터 에러 메시지를 추출하는 함수
    private fun getErrorMessageFromException(exception: Throwable): String {
        return if (exception is HttpException) {
            val errorBody = exception.response()?.errorBody()?.string()
            parseErrorMessage(errorBody) ?: "이미 투표한 경기입니다."
        } else {
            "투표 되었습니다!"
        }
    }

    // 서버에서 반환한 에러 메시지를 파싱하는 함수
    private fun parseErrorMessage(errorBody: String?): String? {
        return errorBody?.let {
            try {
                // JSON 파싱을 통해 메시지 추출
                val jsonObject = JSONObject(it)
                jsonObject.optString("message")
            } catch (e: JSONException) {
                null // 파싱에 실패한 경우 null 반환
            }
        }
    }
}