package umc.everyones.lck.data.service.party

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import umc.everyones.lck.data.dto.BaseResponse
import umc.everyones.lck.data.dto.request.party.WriteViewingPartyRequestDto
import umc.everyones.lck.data.dto.response.party.JoinViewingPartyResponseDto
import umc.everyones.lck.data.dto.response.party.ReadViewingPartyResponseDto
import umc.everyones.lck.data.dto.response.party.ViewingPartyListResponseDto
import umc.everyones.lck.data.dto.response.party.CommonViewingPartyResponseDto
import umc.everyones.lck.data.dto.response.party.ViewingPartyChatLogResponseDto
import umc.everyones.lck.data.dto.response.party.ViewingPartyChatRoomResponseDto
import umc.everyones.lck.data.dto.response.party.ViewingPartyParticipantsResponseDto

interface ViewingPartyService {
    @GET("viewing/list")
    suspend fun fetchViewingPartyList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): BaseResponse<ViewingPartyListResponseDto>

    @GET("viewing/{viewing_party_id}/detail")
    suspend fun fetchViewingParty(
        @Path("viewing_party_id") viewingPartyId: Long
    ): BaseResponse<ReadViewingPartyResponseDto>

    @POST("viewing/{viewing_party_id}/join")
    suspend fun joinViewingParty(
        @Path("viewing_party_id") viewingPartyId: Long
    ): BaseResponse<JoinViewingPartyResponseDto>

    @POST("viewing/create")
    suspend fun writeViewingParty(
        @Body request: WriteViewingPartyRequestDto
    ): BaseResponse<CommonViewingPartyResponseDto>

    @PATCH("viewing/{viewing_party_id}/update")
    suspend fun editViewingParty(
        @Path("viewing_party_id") viewingPartyId: Long,
        @Body request: WriteViewingPartyRequestDto
    ): BaseResponse<CommonViewingPartyResponseDto>

    @DELETE("viewing/{viewing_party_id}/delete")
    suspend fun deleteViewingParty(
        @Path("viewing_party_id") viewingPartyId: Long
    ): BaseResponse<CommonViewingPartyResponseDto>

    @GET("viewing/{viewing_party_id}/participants")
    suspend fun fetchViewingPartyParticipants(
        @Path("viewing_party_id") viewingPartyId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): BaseResponse<ViewingPartyParticipantsResponseDto>

    @POST("viewing/{viewing_party_id}/chatroom/{participant_kakao_id}")
    suspend fun createViewingPartyChatRoom(
        @Path("viewing_party_id") viewingPartyId: Long,
        @Path("participant_kakao_id") participantKakaoId: String
    ): BaseResponse<ViewingPartyChatRoomResponseDto>

    @GET("viewing/chat_log/{room_id}")
    suspend fun fetchViewingPartyChatLog(
        @Path("room_id") roomId: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): BaseResponse<ViewingPartyChatLogResponseDto>

    @POST("viewing/{viewing_party_id}/chatroom")
    suspend fun createViewingPartyChatRoomAsParticipant(
        @Path("viewing_party_id") viewingPartyId: Long,
    ): BaseResponse<ViewingPartyChatRoomResponseDto>
}