package umc.everyones.lck.data.service.match

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import umc.everyones.lck.data.dto.BaseResponse
import umc.everyones.lck.data.dto.request.match.CommonPogRequestDto
import umc.everyones.lck.data.dto.request.match.VoteMatchPogRequestDto
import umc.everyones.lck.data.dto.request.match.VoteMatchRequestDto
import umc.everyones.lck.data.dto.request.match.VoteSetPogRequestDto
import umc.everyones.lck.data.dto.response.match.CommonTodayMatchPogResponseDto
import umc.everyones.lck.data.dto.response.match.CommonVotePogResponseDto
import umc.everyones.lck.data.dto.response.match.MatchTodayMatchResponseDto
import umc.everyones.lck.data.dto.response.match.PogPlayerTodayMatchResponseDto
import umc.everyones.lck.data.dto.response.match.TodayMatchInformationResponseDto
import umc.everyones.lck.data.dto.response.match.TodayMatchSetCountResponseDto

interface TodayMatchService {
    @GET("match/today/information")
    suspend fun fetchTodayMatchInformation(): BaseResponse<TodayMatchInformationResponseDto>

    @GET("votes/pog/candidates")
    suspend fun fetchTodayMatchPogPlayer(
        @Query("match-id") matchId: Long
    ): BaseResponse<PogPlayerTodayMatchResponseDto>

    @GET("votes/match/candidates")
    suspend fun fetchTodayMatchVoteMatch(
        @Query("match-id") matchId: Long
    ): BaseResponse<MatchTodayMatchResponseDto>

    @POST("pog/result")
    suspend fun fetchTodayMatchPog(
        @Body request: CommonPogRequestDto
    ): BaseResponse<CommonTodayMatchPogResponseDto>

    @POST("votes/set-pog/making")
    suspend fun voteSetPog(
        @Body request: VoteSetPogRequestDto
    ): BaseResponse<CommonVotePogResponseDto>

    @POST("votes/match/making")
    suspend fun voteMatch(
        @Body request: VoteMatchRequestDto
    ): BaseResponse<CommonVotePogResponseDto>

    @POST("votes/match-pog/making")
    suspend fun voteMatchPog(
        @Body request: VoteMatchPogRequestDto
    ): BaseResponse<CommonVotePogResponseDto>

    @GET("match/set-count")
    suspend fun fetchTodayMatchSetCount(
        @Query("match-id") matchId: Long
    ): BaseResponse<TodayMatchSetCountResponseDto>
}