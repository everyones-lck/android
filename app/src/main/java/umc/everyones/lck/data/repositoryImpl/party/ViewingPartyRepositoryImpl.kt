package umc.everyones.lck.data.repositoryImpl.party

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import umc.everyones.lck.data.datasource.ViewingPartyDataSource
import umc.everyones.lck.data.datasourceImpl.party.ViewingPartyListPagingSource
import umc.everyones.lck.data.service.party.ViewingPartyService
import umc.everyones.lck.domain.model.request.party.WriteViewingPartyModel
import umc.everyones.lck.domain.model.response.party.CommonViewingPartyModel
import umc.everyones.lck.domain.model.response.party.JoinViewingPartyModel
import umc.everyones.lck.domain.model.response.party.ReadViewingPartyModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyChatLogModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyChatRoomModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyListModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyParticipantsModel
import umc.everyones.lck.domain.repository.party.ViewingPartyRepository
import javax.inject.Inject

class ViewingPartyRepositoryImpl @Inject constructor(
    private val viewingPartyDataSource: ViewingPartyDataSource,
    private val viewingPartyService: ViewingPartyService
) : ViewingPartyRepository {
    override suspend fun fetchViewingPartyList(
        page: Int,
        size: Int
    ): Result<ViewingPartyListModel> = runCatching {
        viewingPartyDataSource.fetchViewingPartyList(
            page,
            size
        ).data.toViewingPartyListModel()
    }

    override suspend fun fetchViewingParty(viewingPartyId: Long): Result<ReadViewingPartyModel> =
        runCatching {
            viewingPartyDataSource.fetchViewingParty(viewingPartyId).data.toReadViewingPartyModel()
        }

    override suspend fun joinViewingParty(viewingPartyId: Long): Result<JoinViewingPartyModel> =
        runCatching { viewingPartyDataSource.joinViewingParty(viewingPartyId).data.toJoinViewingPartyModel() }

    override suspend fun writeViewingParty(request: WriteViewingPartyModel): Result<CommonViewingPartyModel> =
        runCatching { viewingPartyDataSource.writeViewingParty(request.toWriteViewingPartyRequestDto()).data.toCommonViewingPartyModel() }

    override suspend fun editViewingParty(
        viewingPartyId: Long,
        request: WriteViewingPartyModel
    ): Result<CommonViewingPartyModel> =
        runCatching { viewingPartyDataSource.editViewingParty(viewingPartyId, request.toWriteViewingPartyRequestDto()).data.toCommonViewingPartyModel() }

    override suspend fun deleteViewingParty(viewingPartyId: Long): Result<CommonViewingPartyModel> =
        runCatching { viewingPartyDataSource.deleteViewingParty(viewingPartyId).data.toCommonViewingPartyModel() }

    override suspend fun fetchViewingPartyParticipants(
        viewingPartyId: Long,
        page: Int,
        size: Int
    ): Result<ViewingPartyParticipantsModel> =
        runCatching { viewingPartyDataSource.fetchViewingPartyParticipants(viewingPartyId, page, size).data.toViewingPartyParticipantsModel() }

    override suspend fun createViewingPartyChatRoom(viewingPartyId: Long): Result<ViewingPartyChatRoomModel> =
        runCatching { viewingPartyDataSource.createViewingPartyChatRoom(viewingPartyId).data.toViewingPartyChatRoomModel() }

    override suspend fun fetchViewingPartyChatLog(
        roomId: Long,
        page: Int,
        size: Int
    ): Result<ViewingPartyChatLogModel> =
        runCatching { viewingPartyDataSource.fetchViewingPartyChatLog(roomId, page, size).data.toViewingPartyChatLogModel() }

    override suspend fun createViewingPartyChatRoomAsParticipant(viewingPartyId: Long): Result<ViewingPartyChatRoomModel> =
        runCatching { viewingPartyDataSource.createViewingPartyChatRoomAsParticipant(viewingPartyId).data.toViewingPartyChatRoomModel() }

    override fun fetchPagingSource(): Flow<PagingData<ViewingPartyListModel.ViewingPartyElementModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { ViewingPartyListPagingSource(viewingPartyService) }
        ).flow

}