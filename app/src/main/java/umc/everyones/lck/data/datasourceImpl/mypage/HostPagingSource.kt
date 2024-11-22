package umc.everyones.lck.data.datasourceImpl.mypage

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import umc.everyones.lck.data.service.MypageService
import umc.everyones.lck.data.service.community.CommunityService
import umc.everyones.lck.domain.model.community.CommunityListModel
import umc.everyones.lck.domain.model.response.mypage.CommentsMypageModel
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import umc.everyones.lck.domain.model.response.mypage.MyPost
import umc.everyones.lck.domain.model.response.mypage.PostsMypageModel
import javax.inject.Inject

class HostPagingSource @Inject constructor(
    private val mypageService: MypageService,
    private val category: String
) : PagingSource<Int, HostViewingPartyMypageModel.HostViewingPartyMypageElementModel>() {
    override fun getRefreshKey(state: PagingState<Int, HostViewingPartyMypageModel.HostViewingPartyMypageElementModel>): Int? {
        return 0/*state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }*/
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HostViewingPartyMypageModel.HostViewingPartyMypageElementModel> {
        val page = params.key ?: 0 // 첫 페이지는 0
        if(page != 0) delay(100L)
        runCatching {
            delay(300L)
            mypageService.hostViewingPartyMypage(page, 10).data.toHostViewingPartyMypageModel()
        }.fold(
            onSuccess = { response ->
                return LoadResult.Page(
                    data = response.viewingParties,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (response.last) null else page + 1
                )
            }, onFailure = {
                Log.d("Paging 3 Load Error", it.stackTraceToString())
                return LoadResult.Error(it)
            }
        )

    }
}
