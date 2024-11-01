package umc.everyones.lck.domain.repository.party

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import umc.everyones.lck.domain.model.request.party.WriteViewingPartyModel
import umc.everyones.lck.domain.model.response.party.CommonViewingPartyModel
import umc.everyones.lck.domain.model.response.party.JoinViewingPartyModel
import umc.everyones.lck.domain.model.response.party.ReadViewingPartyModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyChatLogModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyChatRoomModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyListModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyParticipantsModel

interface ViewingPartyRepository {
    suspend fun fetchViewingPartyList(page: Int, size: Int): Result<ViewingPartyListModel>

    suspend fun fetchViewingParty(viewingPartyId: Long): Result<ReadViewingPartyModel>

    suspend fun joinViewingParty(viewingPartyId: Long): Result<JoinViewingPartyModel>

    suspend fun writeViewingParty(request: WriteViewingPartyModel): Result<CommonViewingPartyModel>

    suspend fun editViewingParty(viewingPartyId: Long, request: WriteViewingPartyModel): Result<CommonViewingPartyModel>

    suspend fun deleteViewingParty(viewingPartyId: Long): Result<CommonViewingPartyModel>

    suspend fun fetchViewingPartyParticipants(viewingPartyId: Long, page: Int, size: Int): Result<ViewingPartyParticipantsModel>

    suspend fun createViewingPartyChatRoom(viewingPartyId: Long, participantsId: String): Result<ViewingPartyChatRoomModel>

    suspend fun fetchViewingPartyChatLog(roomId: String, page: Int, size: Int): Result<ViewingPartyChatLogModel>

    suspend fun createViewingPartyChatRoomAsParticipant(viewingPartyId: Long): Result<ViewingPartyChatRoomModel>

    fun fetchViewingPartyListPagingSource(): Flow<PagingData<ViewingPartyListModel.ViewingPartyElementModel>>

    fun fetchViewingPartyParticipantsPagingSource(viewingPartyId: Long): Flow<PagingData<ViewingPartyParticipantsModel.ParticipantsModel>>

    fun fetchChatLogPagingSource(roomId: String): Flow<PagingData<ViewingPartyChatLogModel.ChatLogModel>>
}