package umc.everyones.lck.data.datasourceImpl.party

import android.content.SharedPreferences
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import umc.everyones.lck.data.service.party.ViewingPartyService
import umc.everyones.lck.domain.model.response.party.ViewingPartyChatLogModel
import umc.everyones.lck.domain.model.response.party.ViewingPartyListModel
import javax.inject.Inject

class ViewingPartyChatLogPagingSource @Inject constructor(
    private val viewingPartyService: ViewingPartyService,
    private val roomId: String,
    private val spf: SharedPreferences
) : PagingSource<Int, ViewingPartyChatLogModel.ChatLogModel>() {
    override fun getRefreshKey(state: PagingState<Int, ViewingPartyChatLogModel.ChatLogModel>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ViewingPartyChatLogModel.ChatLogModel> {
        val page = params.key ?: 0
        if(page != 0) delay(500)
        runCatching {
            viewingPartyService.fetchViewingPartyChatLog(roomId, page, 10).data.toViewingPartyChatLogModel(spf.getString("nickName", "")?:"")
        }.fold(
            onSuccess = { response ->
                return LoadResult.Page(
                    data = response.chatMessageList,
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