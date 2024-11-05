package umc.everyones.lck.domain.model.response.mypage

data class PostsMypageModel(
    val posts: List<PostsMypageElementModel>,
    val isLast: Boolean
){
    data class PostsMypageElementModel(
        val id: Long,
        val title: String,
        val postType: String
    )
}
