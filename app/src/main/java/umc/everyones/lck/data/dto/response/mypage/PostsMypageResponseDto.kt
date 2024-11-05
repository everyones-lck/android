package umc.everyones.lck.data.dto.response.mypage

import umc.everyones.lck.domain.model.response.mypage.PostsMypageModel

data class PostsMypageResponseDto(
    val posts: List<PostsMypageResponseElementDto>,
    val isLast: Boolean
){
    data class PostsMypageResponseElementDto(
        val id: Long,
        val title: String,
        val postType: String
    ){
        fun toPostsMypageElementModel() =
            PostsMypageModel.PostsMypageElementModel(id, title, postType)
    }
    fun toPostsMypageModel() =
        PostsMypageModel(posts.map{it.toPostsMypageElementModel()},isLast)
}
