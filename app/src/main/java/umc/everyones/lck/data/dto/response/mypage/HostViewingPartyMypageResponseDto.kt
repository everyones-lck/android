package umc.everyones.lck.data.dto.response.mypage

import umc.everyones.lck.domain.model.response.mypage.CommentsMypageModel
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.ParticipateViewingPartyMypageModel

data class HostViewingPartyMypageResponseDto(
    val viewingParties: List<HostViewingPartyMypageResponseElementDto>,
    val last: Boolean
) {
    data class HostViewingPartyMypageResponseElementDto(
        val id: Long,
        val name: String,
        val date: String
    ){
        fun toHostViewingPartyMypageElementModel() =
            HostViewingPartyMypageModel.HostViewingPartyMypageElementModel(id, name, date)
    }

    fun toHostViewingPartyMypageModel() =
        HostViewingPartyMypageModel(viewingParties.map { it.toHostViewingPartyMypageElementModel() }, last)
}

