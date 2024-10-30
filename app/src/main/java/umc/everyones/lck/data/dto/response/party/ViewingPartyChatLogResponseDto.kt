package umc.everyones.lck.data.dto.response.party

import umc.everyones.lck.domain.model.response.party.ViewingPartyChatLogModel
import umc.everyones.lck.presentation.party.adapter.ChatRVA.Companion.RECEIVER
import umc.everyones.lck.presentation.party.adapter.ChatRVA.Companion.SENDER
import umc.everyones.lck.util.extension.toListViewingPartyDateFormat
import umc.everyones.lck.util.extension.toTimeFormat

data class ViewingPartyChatLogResponseDto(
    val isLast: Boolean,
    val totalPage: Int,
    val viewingPartyName: String,
    val receiverName: String,
    val receiverTeam: String,
    val receiverProfileImage: String,
    val chatMessageList: List<ChatLogResponseDto>
){
    data class ChatLogResponseDto(
        val senderName: String,
        val message: String,
        val createdAt: String
    ){
        fun toChatLogModel(nickname: String, receiverProfileImage: String): ViewingPartyChatLogModel.ChatLogModel{
            return ViewingPartyChatLogModel.ChatLogModel(senderName, message, if (nickname == senderName) SENDER else RECEIVER, createdAt.slice(0..15), receiverProfileImage)
        }
    }

    fun toViewingPartyChatLogModel(nickname: String) =
        ViewingPartyChatLogModel(isLast, totalPage, viewingPartyName, receiverName, receiverTeam, receiverProfileImage, chatMessageList.map { it.toChatLogModel(nickname, receiverProfileImage) })
}
