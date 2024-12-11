package umc.everyones.lck.data.datasourceImpl.mypage

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import umc.everyones.lck.data.service.MypageService
import umc.everyones.lck.domain.model.response.mypage.ParticipateViewingPartyMypageModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ParticipatePagingSource @Inject constructor(
    private val mypageService: MypageService,
    private val category: String
) : PagingSource<Int, ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel>() {

    override fun getRefreshKey(state: PagingState<Int, ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ParticipateViewingPartyMypageModel.ParticipateViewingPartyMypageElementModel> {
        val page = params.key ?: 0 // 첫 페이지는 0
        if (page != 0) delay(100L)

        return runCatching {
            delay(300L)
            mypageService.participateViewingPartyMypage(page, 10).data.toParticipateViewingPartyMypageModel()
        }.fold(
            onSuccess = { response ->
                // 참여한 Viewing Party 데이터를 역순으로 정렬
                val sortedViewingParties = response.viewingParties.sortedByDescending {
                    LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy.MM.dd")) // 날짜 형식에 맞게 변환
                }

                LoadResult.Page(
                    data = sortedViewingParties,
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
