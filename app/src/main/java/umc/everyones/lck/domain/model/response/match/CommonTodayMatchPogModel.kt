package umc.everyones.lck.domain.model.response.match

data class CommonTodayMatchPogModel(
    val seasonInfo: String,
    val matchNumber: Int,
    val matchDate: String,
    val setPogResponses: List<PogPlayerModel.SetPogResponsesModel>,
    val matchPogResponse: PogPlayerModel.MatchPogResponseModel?,
    val tabIndex: Int = 0
) {
    sealed class PogPlayerModel {
        data class SetPogResponsesModel(
            val name: String,
            val profileImageUrl: String,
            val playerId: Int,
            val setIndex: Int
        ) : PogPlayerModel()

        data class MatchPogResponseModel(
            val name: String,
            val profileImageUrl: String,
            val playerId: Int
        ) : PogPlayerModel()
    }
}