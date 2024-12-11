package umc.everyones.lck.data.datasourceImpl.mypage

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import umc.everyones.lck.data.service.MypageService
import umc.everyones.lck.domain.model.response.mypage.HostViewingPartyMypageModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HostPagingSource @Inject constructor(
    private val mypageService: MypageService,
    private val category: String
) : PagingSource<Int, HostViewingPartyMypageModel.HostViewingPartyMypageElementModel>() {

    override fun getRefreshKey(state: PagingState<Int, HostViewingPartyMypageModel.HostViewingPartyMypageElementModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HostViewingPartyMypageModel.HostViewingPartyMypageElementModel> {
        val page = params.key ?: 0 // 첫 페이지는 0
        if (page != 0) delay(100L)

        return runCatching {
            delay(300L)
            mypageService.hostViewingPartyMypage(page, 10).data.toHostViewingPartyMypageModel()
        }.fold(
            onSuccess = { response ->
                // 데이터를 역순으로 정렬
                val sortedData = response.viewingParties.sortedByDescending {
                    LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                }

                LoadResult.Page(
                    data = sortedData,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (response.last) null else page + 1
                )
            },
            onFailure = {
                Log.d("Paging 3 Load Error", it.stackTraceToString())
                LoadResult.Error(it)
            }
        )
    }
}
