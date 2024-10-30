package umc.everyones.lck.presentation.party.chat

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import timber.log.Timber
import umc.everyones.lck.data.dto.request.party.ChatMessage
import umc.everyones.lck.util.chat.WebSocketResource
import javax.inject.Inject
import kotlin.io.path.createTempDirectory

class WsClient @Inject constructor(
    private val viewModel: ViewingPartyChatViewModel,
    private val okHttpClient: OkHttpClient,
    private val request: Request,
    private val spf: SharedPreferences
) : WebSocketListener() {
    private var webSocket: WebSocket? = null
    private var nickname = spf.getString("nickName", "") ?: "알 수 없음"
    private val roomId = viewModel.roomId.value
    fun connectWebSocket() {
        webSocket = okHttpClient.newWebSocket(request, this)
    }

    fun enterChatRoom(text: String){
        val chatMessage = ChatMessage(
            type = ENTER,
            senderName = nickname,
            chatRoomId = roomId,
            message = text)
        val gson = Gson()
        val jsonMessage = gson.toJson(chatMessage)
        webSocket?.send(jsonMessage)
    }

    fun sendMessage(text: String){
        val chatMessage = ChatMessage(
            type = TALK,
            senderName = nickname,
            chatRoomId = roomId,
            message = text)

        val gson = Gson()
        val jsonMessage = gson.toJson(chatMessage)
        webSocket?.send(jsonMessage)
    }

    fun closeSocket(){
        webSocket?.close(1000, "")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Timber.tag("WebSocket").d( "Connection opened: $response")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Timber.tag("WebSocket").d("Message received: $text")
        if (!text.contains("님이 입장했습니다.")) {
            viewModel.refreshViewingPartyChatLog()
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
        Timber.tag("WebSocket").d("Message received (binary): ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Timber.tag("WebSocket").d( "Connection closing: $code / $reason")
        webSocket.close(code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Timber.tag("WebSocket").d( "Connection closed: $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Timber.tag("WebSocket").e(t, "Error: " + t.message)
            connectWebSocket()
            enterChatRoom("") // 필요시 다시 입장 시도
    }

    companion object {
        private const val ENTER = "ENTER"
        private const val TALK = "TALK"
    }

}