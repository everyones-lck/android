package umc.everyones.lck.data.dto.response.match

import umc.everyones.lck.domain.model.response.match.CommonTodayMatchPogModel

data class CommonTodayMatchPogResponseDto(
    val seasonInfo: String,
    val matchNumber: Int,
    val matchDate: String,
    val setPogResponses: List<SetPogResponsesDto>,
    val matchPogResponse: MatchPogResponseDto
) {
    data class SetPogResponsesDto(
        val name: String,
        val profileImageUrl: String,
        val playerId: Int,
        val setIndex: Int
    ) {
        fun toSetPogResponsesModel() =
            CommonTodayMatchPogModel.PogPlayerModel.SetPogResponsesModel(
                name, profileImageUrl, playerId, setIndex
            )
    }

    data class MatchPogResponseDto (
        val name: String,
        val profileImageUrl: String,
        val playerId: Int
    ) {
        fun toMatchPogResponseModel() =
            CommonTodayMatchPogModel.PogPlayerModel.MatchPogResponseModel(
                name, profileImageUrl, playerId
            )
    }

    fun toCommonTodayMatchPogModel() =
        CommonTodayMatchPogModel(
            seasonInfo,
            matchNumber,
            matchDate.replace("T", " ").dropLast(3),
            setPogResponses.map { it.toSetPogResponsesModel() },
            matchPogResponse?.toMatchPogResponseModel()
        )
}