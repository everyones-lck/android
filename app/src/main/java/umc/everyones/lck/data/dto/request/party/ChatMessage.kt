package umc.everyones.lck.data.dto.request.party

data class ChatMessage(
    val type: String,
    val senderName: String,
    val chatRoomId: Int,
    val message: String
)
