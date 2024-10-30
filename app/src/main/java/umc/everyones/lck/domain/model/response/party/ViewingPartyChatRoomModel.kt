package umc.everyones.lck.domain.model.response.party

data class ViewingPartyChatRoomModel(
    val roomId: String,
    val viewingPartyName: String,
    val sessions: Set<String>
)