package umc.everyones.lck.presentation.party.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import umc.everyones.lck.R
import umc.everyones.lck.databinding.ActivityViewingPartyChatBinding
import umc.everyones.lck.domain.model.party.ChatItem
import umc.everyones.lck.presentation.party.adapter.ChatRVA
import umc.everyones.lck.presentation.party.adapter.ChatRVA.Companion.RECEIVER
import umc.everyones.lck.presentation.party.adapter.ChatRVA.Companion.SENDER
import umc.everyones.lck.util.KeyboardUtil
import umc.everyones.lck.util.chat.WebSocketResource
import umc.everyones.lck.util.extension.combineNicknameAndTeam
import umc.everyones.lck.util.extension.drawableOf
import umc.everyones.lck.util.extension.hideKeyboardOnOutsideTouch
import umc.everyones.lck.util.extension.repeatOnStarted
import umc.everyones.lck.util.extension.setOnSingleClickListener
import umc.everyones.lck.util.extension.showCustomSnackBar
import umc.everyones.lck.util.extension.showKeyboard
import umc.everyones.lck.util.extension.textToString
import umc.everyones.lck.util.network.UiState
import javax.inject.Inject

@AndroidEntryPoint
class ViewingPartyChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewingPartyChatBinding

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var request: Request

    @Inject
    lateinit var spf: SharedPreferences

    private val chatRVA by lazy {
        ChatRVA()
    }

    private val postId by lazy {
        intent.getLongExtra("postId", 0L)
    }

    private val participantsId by lazy {
        intent.getStringExtra("participantsId")
    }

    private val isParticipant by lazy {
        intent.getBooleanExtra("isParticipant", false)
    }

    private val viewModel: ViewingPartyChatViewModel by viewModels()

    private lateinit var wsClient: WsClient

    private var isEntered = false

    private var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewingPartyChatBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }
        initObserver()
        initView()
    }

    private fun initView() {
        viewModel.setPostId(postId, participantsId?:"")
        if (isParticipant) {
            viewModel.createViewingPartyChatRoomAsParticipant()
        } else {
            viewModel.createViewingPartyChatRoom()
        }
        validateMessageSend()
        initChatRVAdapter()
        sendMessage()
        binding.ivChatBackBtn.setOnSingleClickListener {
            finish()
        }

        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (totalItemCount - lastVisibleItemPosition <= 2) {
                        viewModel.fetchViewingPartyChatLog()
                    }
                }
            }
        })

        chatRVA.registerAdapterDataObserver(object : AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if(positionStart == 0){
                    binding.rvChat.scrollToPosition(0)
                }
            }
        })
        KeyboardUtil.registerRecyclerViewKeyboardVisibilityListener(binding.root, binding.rvChat)
    }

    private fun initChatRVAdapter() {
        binding.rvChat.adapter = chatRVA
        binding.rvChat.itemAnimator = null
    }

    private fun validateMessageSend() {
        binding.etChatInput.addTextChangedListener(
            onTextChanged = { text: CharSequence?, _: Int, _: Int, _: Int ->
                if (text != null) {

                    // 댓글 작성 여부에 따른 전송 버튼 활성화 제어
                    if (text.isEmpty()) {
                        binding.ivChatSendBtn.setImageDrawable(drawableOf(R.drawable.ic_send))
                    } else {
                        binding.ivChatSendBtn.setImageDrawable(drawableOf(R.drawable.ic_send_enabled))
                    }

                    if (text.length >= 1000) {
                        showCustomSnackBar(
                            binding.ivChatSendBtn,
                            "채팅은 최대 1,000자까지 입력할 수 있어요"
                        )
                    }
                }
            }
        )
    }

    private fun sendMessage() {
        with(binding) {
            ivChatSendBtn.setOnSingleClickListener {
                wsClient.sendMessage(etChatInput.textToString())
                etChatInput.setText("")
            }
        }
    }

    private fun initObserver() {
        repeatOnStarted {
            viewModel.viewingPartyChatEvent.collect { state ->
                when (state) {
                    is UiState.Success -> handleViewingPartyChatEvent(state.data)
                    is UiState.Failure -> {
                        showCustomSnackBar(binding.root, state.msg)
                    }
                    else -> Unit
                }
            }
        }

        repeatOnStarted {
            viewModel.roomId.collect{
                it.ifEmpty { return@collect }
                initWsClient()
                viewModel.fetchViewingPartyChatLog()
            }
        }

        repeatOnStarted {
            viewModel.webSocketEvent.collect{ event ->
                handleWebsocketEvent(event)
            }
        }
    }

    private fun handleWebsocketEvent(event: WebSocketResource){
        when(event){
            WebSocketResource.Enter -> {
                isEntered = true
            }
             else -> Unit
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleViewingPartyChatEvent(event: ViewingPartyChatViewModel.ViewingPartyChatEvent){
        when(event){
            is ViewingPartyChatViewModel.ViewingPartyChatEvent.CreateChatRoom -> {
                with(event.result) {
                    binding.tvChatTitle.text = viewingPartyName
                }
            }
            is ViewingPartyChatViewModel.ViewingPartyChatEvent.FetchChatLog -> {
                Timber.tag("chat_log").d(event.chatLog.chatMessageList.toString())
                binding.tvChatWriter.text = event.chatLog.receiverName.combineNicknameAndTeam(event.chatLog.receiverTeam)
                val prevLastIndex = chatRVA.currentList.lastIndex
                chatRVA.submitList(event.chatLog.chatMessageList){
                    if(isFirst) {
                        binding.rvChat.scrollToPosition(0)
                        isFirst = false
                    }
                    chatRVA.notifyItemChanged(prevLastIndex)
                }
            }

            is ViewingPartyChatViewModel.ViewingPartyChatEvent.RefreshChatLog -> {
                val prevLastIndex = chatRVA.currentList.lastIndex
                chatRVA.submitList(event.chatLog.chatMessageList){
                    chatRVA.notifyItemChanged(prevLastIndex)
                }
            }
        }
    }

    private fun initWsClient() {
        wsClient = WsClient(viewModel, okHttpClient, request, spf)
        wsClient.connectWebSocket()
    }

    companion object {
        fun newIntent(context: Context, postId: Long, isParticipant: Boolean, participantsId: String): Intent =
            Intent(context, ViewingPartyChatActivity::class.java).apply {
                putExtra("postId", postId)
                putExtra("isParticipant", isParticipant)
                putExtra("participantsId", participantsId)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        wsClient.closeSocket()
        KeyboardUtil.unregisterRecyclerViewKeyboardVisibilityListener(binding.root)
    }

}