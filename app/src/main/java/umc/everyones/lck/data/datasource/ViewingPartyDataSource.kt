package umc.everyones.lck.data.datasource

import umc.everyones.lck.data.dto.BaseResponse
import umc.everyones.lck.data.dto.request.party.WriteViewingPartyRequestDto
import umc.everyones.lck.data.dto.response.party.JoinViewingPartyResponseDto
import umc.everyones.lck.data.dto.response.party.ReadViewingPartyResponseDto
import umc.everyones.lck.data.dto.response.party.ViewingPartyListResponseDto
import umc.everyones.lck.data.dto.response.party.CommonViewingPartyResponseDto
import umc.everyones.lck.data.dto.response.party.ViewingPartyChatLogResponseDto
import umc.everyones.lck.data.dto.response.party.ViewingPartyChatRoomResponseDto
import umc.everyones.lck.data.dto.response.party.ViewingPartyParticipantsResponseDto

interface ViewingPartyDataSource {
    suspend fun fetchViewingPartyList(page: Int, size: Int): BaseResponse<ViewingPartyListResponseDto>

    suspend fun fetchViewingParty(viewingPartyId: Long): BaseResponse<ReadViewingPartyResponseDto>

    suspend fun joinViewingParty(viewingPartyId: Long): BaseResponse<JoinViewingPartyResponseDto>

    suspend fun writeViewingParty(requestDto: WriteViewingPartyRequestDto): BaseResponse<CommonViewingPartyResponseDto>

    suspend fun editViewingParty(viewingPartyId: Long, requestDto: WriteViewingPartyRequestDto): BaseResponse<CommonViewingPartyResponseDto>

    suspend fun deleteViewingParty(viewingPartyId: Long): BaseResponse<CommonViewingPartyResponseDto>

    suspend fun fetchViewingPartyParticipants(viewingPartyId: Long, page: Int, size: Int): BaseResponse<ViewingPartyParticipantsResponseDto>

    suspend fun createViewingPartyChatRoom(viewingPartyId: Long, participantsId: String): BaseResponse<ViewingPartyChatRoomResponseDto>

    suspend fun fetchViewingPartyChatLog(roomId: String, page: Int, size: Int): BaseResponse<ViewingPartyChatLogResponseDto>

    suspend fun createViewingPartyChatRoomAsParticipant(viewingPartyId: Long): BaseResponse<ViewingPartyChatRoomResponseDto>
}