package umc.everyones.lck.data.datasourceImpl.mypage

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import umc.everyones.lck.data.service.MypageService
import umc.everyones.lck.domain.model.response.mypage.PostsMypageModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MyPostPagingSource @Inject constructor(
    private val mypageService: MypageService,
    private val category: String
) : PagingSource<Int, PostsMypageModel.PostsMypageElementModel>() {

    override fun getRefreshKey(state: PagingState<Int, PostsMypageModel.PostsMypageElementModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostsMypageModel.PostsMypageElementModel> {
        val page = params.key ?: 0 // 첫 페이지는 0
        if (page != 0) delay(100L)

        return runCatching {
            delay(300L)
            mypageService.postsMypage(page, 10).data.toPostsMypageModel()
        }.fold(
            onSuccess = { response ->
                val sortedPosts = response.posts.sortedByDescending { it.id }

                LoadResult.Page(
                    data = sortedPosts,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (response.isLast) null else page + 1
                )
            },
            onFailure = {
                Log.d("Paging 3 Load Error", it.stackTraceToString())
                LoadResult.Error(it)
            }
        )
    }
}
