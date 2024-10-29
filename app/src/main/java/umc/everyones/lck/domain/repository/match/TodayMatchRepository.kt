package umc.everyones.lck.domain.repository.match

import umc.everyones.lck.domain.model.request.match.CommonPogModel
import umc.everyones.lck.domain.model.request.match.VoteMatchModel
import umc.everyones.lck.domain.model.request.match.VoteMatchPogModel
import umc.everyones.lck.domain.model.request.match.VoteSetPogModel
import umc.everyones.lck.domain.model.response.match.CommonTodayMatchPogModel
import umc.everyones.lck.domain.model.response.match.CommonVotePogModel
import umc.everyones.lck.domain.model.response.match.MatchTodayMatchModel
import umc.everyones.lck.domain.model.response.match.PogPlayerTodayMatchModel
import umc.everyones.lck.domain.model.response.match.TodayMatchInformationModel
import umc.everyones.lck.domain.model.response.match.TodayMatchSetCountModel

interface TodayMatchRepository {
    suspend fun fetchTodayMatchInformation(): Result<TodayMatchInformationModel>
    suspend fun fetchTodayMatchVoteMatch(matchId: Long): Result<MatchTodayMatchModel>
    suspend fun fetchTodayMatchPogPlayer(matchId: Long): Result<PogPlayerTodayMatchModel>
    suspend fun fetchTodayMatchPog(request: CommonPogModel): Result<CommonTodayMatchPogModel>
    suspend fun voteSetPog(request: VoteSetPogModel): Result<CommonVotePogModel>
    suspend fun voteMatch(request: VoteMatchModel): Result<CommonVotePogModel>
    suspend fun voteMatchPog(request: VoteMatchPogModel): Result<CommonVotePogModel>
    suspend fun fetchTodayMatchSetCount(matchId: Long): Result<TodayMatchSetCountModel>
}
