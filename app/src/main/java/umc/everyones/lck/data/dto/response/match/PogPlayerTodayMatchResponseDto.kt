package umc.everyones.lck.data.dto.response.match

import umc.everyones.lck.domain.model.response.match.PogPlayerTodayMatchModel

data class PogPlayerTodayMatchResponseDto(
    val matchPogVoteCandidate: MatchPogVoteCandidateDto,
    val setPogVoteCandidates: List<SetPogVoteCandidatesDto>
) {
    data class MatchPogVoteCandidateDto(
        val information: List<InformationDto>
    ) {
        fun toMatchPogVoteCandidateModel() =
            PogPlayerTodayMatchModel.MatchPogVoteCandidateModel(
                information = information.map { it.toInformationModel() }
            )
    }

    data class SetPogVoteCandidatesDto(
        val setIndex: Int,
        val information: List<InformationDto>
    ) {
        fun toSetPogVoteCandidatesModel() =
            PogPlayerTodayMatchModel.SetPogVoteCandidatesModel(
                setIndex,
                information = information.map { it.toInformationModel() }
            )
    }

    data class InformationDto(
        val playerId: Int,
        val playerProfileImageUrl: String,
        val playerName: String
    ) {
        fun toInformationModel() =
            PogPlayerTodayMatchModel.InformationModel(
                playerId,
                playerProfileImageUrl,
                playerName)
    }

    fun toPogPlayerTodayMatchModel() =
        PogPlayerTodayMatchModel(
            matchPogVoteCandidate = matchPogVoteCandidate?.toMatchPogVoteCandidateModel(),
            setPogVoteCandidates = setPogVoteCandidates.map { it.toSetPogVoteCandidatesModel() }
        )

}