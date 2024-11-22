package umc.everyones.lck.domain.model.response.mypage

data class ParticipateViewingPartyMypageModel(
    val viewingParties: List<ParticipateViewingPartyMypageElementModel>,
    val last: Boolean
) {
    data class ParticipateViewingPartyMypageElementModel(
        val id: Long,
        val name: String,
        val date: String
    )
}
