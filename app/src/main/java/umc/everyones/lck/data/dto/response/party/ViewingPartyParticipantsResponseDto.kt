package umc.everyones.lck.data.dto.response.party

import umc.everyones.lck.domain.model.response.party.ViewingPartyParticipantsModel
import umc.everyones.lck.util.extension.combineNicknameAndTeam

data class ViewingPartyParticipantsResponseDto(
    val viewingPartyName: String,
    val ownerName: String,
    val ownerTeam: String,
    val ownerImage: String,
    val participantList: List<ParticipantsResponseDto>,
    val isLast: Boolean
){
    data class ParticipantsResponseDto(
        val kakaoUserId: String,
        val id: Long,
        val name: String,
        val team: String,
        val image: String,
        val isParticipating: Boolean,
        val isChatting: Boolean
    ){
        fun toParticipantsModel() =
            ViewingPartyParticipantsModel.ParticipantsModel(kakaoUserId, id, name, team, image, isParticipating, isChatting)
    }

    fun toViewingPartyParticipantsModel() =
        ViewingPartyParticipantsModel(viewingPartyName, ownerName.combineNicknameAndTeam(ownerTeam), ownerImage, participantList.map { it.toParticipantsModel() }, isLast)
}
