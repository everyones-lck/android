package umc.everyones.lck.domain.model.response.party

data class ViewingPartyChatLogModel(
    val isLast: Boolean,
    val totalPage: Int,
    val viewingPartyName: String,
    val receiverName: String,
    val receiverTeam: String,
    val receiverProfileImage: String,
    val chatMessageList: List<ChatLogModel>
){
    data class ChatLogModel(
        val senderName: String,
        val message: String,
        val viewType: Int,
        val createdAt: String,
        val receiverProfileImage: String,
        var isLastIndex: Boolean = false
    )
}