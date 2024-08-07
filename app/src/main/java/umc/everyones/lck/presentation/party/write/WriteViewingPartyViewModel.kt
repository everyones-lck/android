package umc.everyones.lck.presentation.party.write

import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.umc.ttoklip.data.repository.naver.NaverRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import umc.everyones.lck.util.network.onSuccess

@HiltViewModel
class WriteViewingPartyViewModel @Inject constructor(
    private val naverRepository: NaverRepository
) : ViewModel() {
    private val _latLng = MutableSharedFlow<LatLng>()
    val latLng: SharedFlow<LatLng> get() = _latLng

    private val _date = MutableSharedFlow<String>()
    val date: SharedFlow<String> get() = _date

    fun setDate(date: String){
        viewModelScope.launch {
            _date.emit(date)
        }
    }
    fun fetchGeoCoding(query: String){
        viewModelScope.launch {
            naverRepository.fetchGeocoding(query).onSuccess { response ->
                val result = response.addresses.firstOrNull()
                if(result != null){
                    val latLng = LatLng(result.y.toDouble(), result.x.toDouble())
                    _latLng.emit(latLng)
                }
            }
        }
    }
}