package umc.everyones.lck.domain.model.response.party

import umc.everyones.lck.data.dto.response.party.ViewingPartyParticipantsResponseDto

data class ViewingPartyParticipantsModel(
    val viewingPartyName: String,
    val ownerInfo: String,
    val ownerImage: String,
    val participantList: List<ParticipantsModel>,
    val isLast: Boolean
) {
    data class ParticipantsModel(
        val kakaoUserId: String,
        val id: Long,
        val name: String,
        val team: String,
        val image: String,
        val isParticipating: Boolean,
        val isChatting: Boolean
    )
}