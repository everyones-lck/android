package umc.everyones.lck.domain.model.response.match

data class PogPlayerTodayMatchModel(
    val matchPogVoteCandidate: MatchPogVoteCandidateModel,
    val setPogVoteCandidates: List<SetPogVoteCandidatesModel>
) {
    data class MatchPogVoteCandidateModel(
        val information: List<InformationModel>
    )

    data class SetPogVoteCandidatesModel(
        val setIndex: Int,
        val information: List<InformationModel>
    )

    data class InformationModel(
        val playerId: Int,
        val playerProfileImageUrl: String,
        val playerName: String
    )
}