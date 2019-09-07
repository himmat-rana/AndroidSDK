package io.supportgenie.androidlibrary.data.network.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ChatMessagesWrapper {
    lateinit var messages: List<MessageDto> // Navigates down the ‘list’ object
    var totalMessages: Int = 0
}

@JsonClass(generateAdapter = true)
class MessageDto {
    lateinit var sessionId: String
    lateinit var messageId: String
    lateinit var message: String
    lateinit var messageTime: String
    lateinit var extraMessageData: Map<String, Any>
    lateinit var mimeType: String
    lateinit var sender: String
    lateinit var senderType: String
    lateinit var transport: String
    lateinit var createdAt: String
    lateinit var updatedAt: String
}

// uses Gson
data class SendMessageData(
    @SerializedName("messageId") var messageId: String,
    @SerializedName("mimeType") var mimeType: String,
    @SerializedName("sender") var sender: String,
    @SerializedName("senderType") var senderType: String,
    @SerializedName("message") var message: String,
    @SerializedName("sessionId") var sessionId: String
)



