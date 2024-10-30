package umc.everyones.lck.presentation.party.write

import android.util.Log
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import umc.everyones.lck.domain.repository.NaverRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import umc.everyones.lck.domain.model.naver.GeocodingModel
import umc.everyones.lck.domain.model.request.party.WriteViewingPartyModel
import umc.everyones.lck.domain.repository.party.ViewingPartyRepository
import umc.everyones.lck.util.network.EventFlow
import umc.everyones.lck.util.network.MutableEventFlow
import umc.everyones.lck.util.network.UiState
import java.lang.IndexOutOfBoundsException

@HiltViewModel
class WriteViewingPartyViewModel @Inject constructor(
    private val naverRepository: NaverRepository,
    private val repository: ViewingPartyRepository
) : ViewModel() {

    private val _date = MutableEventFlow<String>()
    val date: EventFlow<String> get() = _date

    private val _writeViewingPartyEvent =
        MutableStateFlow<UiState<WriteViewingPartyEvent>>(UiState.Empty)
    val writeViewingPartyEvent: StateFlow<UiState<WriteViewingPartyEvent>> get() = _writeViewingPartyEvent

    sealed class WriteViewingPartyEvent {
        data class WriteDoneViewingParty(val isWriteDone: Boolean) : WriteViewingPartyEvent()

        data class Geocoding(val geoCodingResult: GeocodingModel) : WriteViewingPartyEvent()
    }

    fun setDate(date: String) {
        viewModelScope.launch {
            _date.emit(date)
        }
    }

    fun fetchGeocoding(query: String) {
        viewModelScope.launch {
            _writeViewingPartyEvent.value = UiState.Loading
            naverRepository.fetchGeocoding(query).onSuccess { response ->
                Timber.d("fetchGeocoding", response.toString())
                _writeViewingPartyEvent.value =
                    UiState.Success(WriteViewingPartyEvent.Geocoding(response))
            }.onFailure {
                if (it is IndexOutOfBoundsException) {
                    _writeViewingPartyEvent.value = UiState.Failure(GEOCODING_FAIL)
                }
                Timber.d("fetchGeocoding", it.stackTraceToString())
            }
        }
    }

    fun writeViewingParty(
        isEdit: Boolean,
        postId: Long = 0L,
        name: String,
        date: String,
        latitude: Double,
        longitude: Double,
        location: String,
        shortLocation: String,
        price: String,
        lowParticipate: String,
        highParticipate: String,
        qualify: String,
        etc: String
    ) {
        val writeViewingPartyModel = WriteViewingPartyModel(
            name,
            date,
            latitude,
            longitude,
            location,
            shortLocation,
            price,
            lowParticipate,
            highParticipate,
            qualify,
            etc
        )
        Timber.d("isEdit", isEdit.toString())
        Timber.d("writeViewingPartyModel", writeViewingPartyModel.toString())
        viewModelScope.launch {
            _writeViewingPartyEvent.value = UiState.Loading
            if (isEdit) {
                repository.editViewingParty(
                    postId,
                    writeViewingPartyModel
                ).onSuccess { response ->
                    Timber.d("editViewingParty", response.toString())
                    _writeViewingPartyEvent.value =
                        UiState.Success(WriteViewingPartyEvent.WriteDoneViewingParty(true))
                }.onFailure {
                    Timber.d("editViewingParty", it.stackTraceToString())
                    _writeViewingPartyEvent.value = UiState.Failure(EDIT_FAIL)
                }
            } else {
                repository.writeViewingParty(
                    writeViewingPartyModel
                ).onSuccess { response ->
                    Timber.d("writeViewingParty", response.toString())
                    _writeViewingPartyEvent.value =
                        UiState.Success(WriteViewingPartyEvent.WriteDoneViewingParty(true))
                }.onFailure {
                    Timber.d("writeViewingParty", it.stackTraceToString())
                    _writeViewingPartyEvent.value = UiState.Failure(HOST_FAIL)
                }
            }
        }
    }

    companion object {
        const val GEOCODING_FAIL = "네이버 지도에서 정확한 주소를 확인 후, 다시 입력해주세요"
        const val HOST_FAIL = "뷰잉파티 개최에 실패했습니다"
        const val EDIT_FAIL = "뷰잉파티 수정에 실패했습니다"
    }
}