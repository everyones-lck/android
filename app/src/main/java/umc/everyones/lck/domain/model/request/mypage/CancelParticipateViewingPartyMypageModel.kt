package umc.everyones.lck.domain.model.request.mypage

import umc.everyones.lck.data.dto.request.mypage.CancelParticipateViewingPartyMypageRequestDto
import java.io.Serializable

data class CancelParticipateViewingPartyMypageModel(
    val viewingPartyId: Long
): Serializable {
    fun toCancelParticipateViewingPartyMypageRequestDto() =
        CancelParticipateViewingPartyMypageRequestDto(viewingPartyId)
}
