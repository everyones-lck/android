package umc.everyones.lck.domain.model.response.home

data class HomeTodayMatchModel(
    val todayMatches: List<TodayMatchesModel>,
    val recentMatchResults: List<RecentMatchResultModel>
) {
    data class TodayMatchesModel(
        val matchId: Long,
        val matchDate: String,
        val team1Name: String,
        val team1LogoUrl: String,
        val team2Name: String,
        val team2LogoUrl: String,
        val seasonInfo: String,
        val matchNumber: Int
    )

    data class RecentMatchResultModel(
        val matchId: Long,
        val matchDate: String,
        val team1Name: String,
        val team1LogoUrl: String,
        val team2Name: String,
        val team2LogoUrl: String,
        val matchResult: String
    )
}
