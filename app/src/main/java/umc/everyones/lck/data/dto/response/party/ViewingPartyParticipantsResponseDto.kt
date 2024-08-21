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
        val id: Long,
        val name: String,
        val team: String,
        val image: String
    ){
        fun toParticipantsModel() =
            ViewingPartyParticipantsModel.ParticipantsModel(id, name, team, image)
    }

    fun toViewingPartyParticipantsModel() =
        ViewingPartyParticipantsModel(viewingPartyName, ownerName.combineNicknameAndTeam(ownerTeam), ownerImage, participantList.map { it.toParticipantsModel() }, isLast)
}
