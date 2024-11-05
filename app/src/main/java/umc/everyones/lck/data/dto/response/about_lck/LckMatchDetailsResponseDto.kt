package umc.everyones.lck.data.dto.response.about_lck

import umc.everyones.lck.domain.model.about_lck.AboutLckMatchDetailsModel

data class LckMatchDetailsResponseDto(
    val matchByDateList: List<LckMatchByDateDto>
) {
    data class LckMatchByDateDto(
        val matchDate: String,
        val matchDetailList: List<LckMatchDetailsElementDto>,
        val matchDetailSize: Int
    ) {
        fun toAboutLckMatchByDateModel() =
            AboutLckMatchDetailsModel.AboutLckMatchByDateModel(
                matchDate = matchDate,
                matchDetailList = matchDetailList.map { it.toAboutLckMatchDetailsElementModel() },
                matchDetailSize = matchDetailSize
            )
    }

    data class LckMatchDetailsElementDto(
        val team1: TeamElementDto,
        val team2: TeamElementDto,
        val matchFinished: Boolean,
        val season: String,
        val matchNumber: Int,
        val matchTime: String,
        val matchDate: String
    ) {
        data class TeamElementDto(
            val teamName: String,
            val teamLogoUrl: String,
            val winner: Boolean
        ) {
            fun toTeamElementModel() =
                AboutLckMatchDetailsModel.AboutLckMatchDetailsElementModel.TeamElementModel(
                    teamName, teamLogoUrl, winner
                )
        }

        fun toAboutLckMatchDetailsElementModel() =
            AboutLckMatchDetailsModel.AboutLckMatchDetailsElementModel(
                team1.toTeamElementModel(),
                team2.toTeamElementModel(),
                matchFinished,
                season,
                matchNumber,
                matchTime,
                matchDate
            )
    }

    fun toAboutLckMatchDetailsModel() =
        AboutLckMatchDetailsModel(
            matchByDateList = matchByDateList.map { it.toAboutLckMatchByDateModel() }
        )
}