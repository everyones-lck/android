package umc.everyones.lck.data.datasourceImpl

import umc.everyones.lck.data.datasource.TodayMatchDataSource
import umc.everyones.lck.data.dto.BaseResponse
import umc.everyones.lck.data.dto.request.match.CommonPogRequestDto
import umc.everyones.lck.data.dto.response.match.CommonTodayMatchPogResponseDto
import umc.everyones.lck.data.dto.response.match.MatchPogTodayMatchResponseDto
import umc.everyones.lck.data.dto.response.match.MatchTodayMatchResponseDto
import umc.everyones.lck.data.dto.response.match.SetPogTodayMatchResponseDto
import umc.everyones.lck.data.dto.response.match.TodayMatchInformationResponseDto
import umc.everyones.lck.data.service.TodayMatchService
import javax.inject.Inject

class TodayMatchDataSourceImpl @Inject constructor(
    private val todayMatchService: TodayMatchService
): TodayMatchDataSource {
    override suspend fun fetchTodayMatchInformation(): BaseResponse<TodayMatchInformationResponseDto> =
        todayMatchService.fetchTodayMatchInformation()

    override suspend fun fetchTodayMatchVoteSetPog(
        matchId: Long,
        setIndex: Int,
    ): BaseResponse<SetPogTodayMatchResponseDto> =
        todayMatchService.fetchTodayMatchVoteSetPog(matchId, setIndex)

    override suspend fun fetchTodayMatchVoteMatch(matchId: Long): BaseResponse<MatchTodayMatchResponseDto> =
        todayMatchService.fetchTodayMatchVoteMatch(matchId)

    override suspend fun fetchTodayMatchVoteMatchPog(matchId: Long): BaseResponse<MatchPogTodayMatchResponseDto> =
        todayMatchService.fetchTodayMatchVoteMatchPog(matchId)

    override suspend fun voteTodayMatchSetPog(
        set: Int,
        request: CommonPogRequestDto,
    ): BaseResponse<CommonTodayMatchPogResponseDto> =
        todayMatchService.voteTodayMatchSetPog(set, request)

    override suspend fun voteTodayMatchMatchPog(request: CommonPogRequestDto): BaseResponse<CommonTodayMatchPogResponseDto> =
        todayMatchService.voteTodayMatchMatchPog(request)
}