package umc.everyones.lck.domain.model.response.mypage

import umc.everyones.lck.data.dto.response.mypage.ParticipateViewingPartyMypageResponseDto

data class HostViewingPartyMypageModel(
    val viewingParties: List<HostViewingPartyMypageElementModel>,
    val last: Boolean
) {
    data class HostViewingPartyMypageElementModel(
        val id: Long,
        val name: String,
        val date: String
    )
}
