package umc.everyones.lck.domain.model.response.mypage

import java.io.Serializable

data class MyPost(
    val id: Long,
    val title: String,
    val postType: String
): Serializable
