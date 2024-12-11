package umc.everyones.lck.data.dto.response.mypage

import umc.everyones.lck.domain.model.response.mypage.CommentsMypageModel
import umc.everyones.lck.domain.model.response.mypage.ParticipateViewingPartyMypageModel

data class ParticipateViewingPartyMypageResponseDto(
    val viewingParties: List<ParticipateViewingPartyMypageResponseElementDto>,
    val last: Boolean
) {
    data class ParticipateViewingPartyMypageResponseElementDto(
        val id: Long,
        val name: String,
        val date: String
    ){
        fun toParticipateViewingPartyMypageElementModel() =
            ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel(id, name, date)
    }

    fun toParticipateViewingPartyMypageModel() =
        ParticipateViewingPartyMypageModel(viewingParties.map { it.toParticipateViewingPartyMypageElementModel() }, last)
}

