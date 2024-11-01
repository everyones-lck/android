package umc.everyones.lck.data.datasourceImpl.party

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import umc.everyones.lck.data.service.party.ViewingPartyService
import umc.everyones.lck.domain.model.response.party.ViewingPartyListModel
import javax.inject.Inject

class ViewingPartyListPagingSource @Inject constructor(
    private val viewingPartyService: ViewingPartyService
) : PagingSource<Int, ViewingPartyListModel.ViewingPartyElementModel>() {
    override fun getRefreshKey(state: PagingState<Int, ViewingPartyListModel.ViewingPartyElementModel>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ViewingPartyListModel.ViewingPartyElementModel> {
        val page = params.key ?: 0
        if(page != 0) delay(100L)
        runCatching {
            delay(300L)
            viewingPartyService.fetchViewingPartyList(page, 10).data.toViewingPartyListModel()
        }.fold(
            onSuccess = { response ->
                return LoadResult.Page(
                    data = response.partyList,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (response.isLast) null else page + 1
                )
            }, onFailure = {
                Log.d("Paging 3 Load Error", it.stackTraceToString())
                return LoadResult.Error(it)
            }
        )
    }
}
