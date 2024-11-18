package umc.everyones.lck.domain.model.response.mypage

data class CommentsMypageModel(
    val comments: List<CommentsMypageElementModel>,
    val isLast: Boolean
) {
    data class CommentsMypageElementModel(
        val id: Long,
        val content: String,
        val postType: String
    )
}
