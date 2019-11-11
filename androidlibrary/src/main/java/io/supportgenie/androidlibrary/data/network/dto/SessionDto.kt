package io.supportgenie.androidlibrary.data.network.dto

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SessionsWrapper<T> {
    var sessions: T? = null
}

@JsonClass(generateAdapter = true)
class SessionsDataDto<T> {
    var participants: T? = null
    lateinit var sessionId: String
    lateinit var companyId: String
    lateinit var createdAt: String
    lateinit var updatedAt: String
    lateinit var status: String
    lateinit var type: String
}


@JsonClass(generateAdapter = true)
class SessionDataDto {
    var success: Boolean = false
    lateinit var sessionId: String
    lateinit var companyId: String
    lateinit var createdAt: String
    lateinit var updatedAt: String
    lateinit var status: String
    lateinit var type: String
}

@JsonClass(generateAdapter = true)
class SessionParticipantWrapper {
    var success: Boolean = false
    var participants: List<ParticipantDto>? = null
}

@JsonClass(generateAdapter = true)
class SendMessageWrapper {
    var success: Boolean = false
    var messageId: String? = null
}

@JsonClass(generateAdapter = true)
class UpdateSessionWrapper {
    var success: Boolean = false
    var sessionId: String? = null
//    var messageId: String? = null
}

@JsonClass(generateAdapter = true)
class ParticipantDto {
    lateinit var participant: String
    lateinit var participantType: String
    lateinit var status: String
    lateinit var createdAt: String
    lateinit var updatedAt: String
}

typealias SessionDto = SessionsDataDto<List<ParticipantDto>>
typealias UserSessionsWrapper = SessionsWrapper<List<SessionDto>>

// uses Gson
//data class UpdateSessionData(
//    @SerializedName("messageId") var messageId: String,
//    @SerializedName("mimeType") var mimeType: String,
//    @SerializedName("sender") var sender: String,
//    @SerializedName("senderType") var senderType: String,
////    @SerializedName("message") var message: String,
//    @SerializedName("status") var message: String,
//    @SerializedName("sessionId") var sessionId: String,
//    @SerializedName("companyId") var companyId: String
//)

//data class UpdateSessionData(
//    @SerializedName("sessionId") var sessionId: String
//)

data class UpdateSessionData(
    @SerializedName("status") var status: String,
    @SerializedName("sessionId") var sessionId: String
)

@JsonClass(generateAdapter = true)
class NewSessionWrapper<T, N>{
    var success: Boolean = false
    var participants: T? = null
    var session: N? = null
}

@JsonClass(generateAdapter = true)
class NewSessionDataDto {
    lateinit var sessionId: String
    lateinit var companyId: String
    lateinit var createdAt: String
    lateinit var updatedAt: String
    lateinit var status: String
    lateinit var type: String
    lateinit var event: String
    lateinit var lastActivityTime: String
    lateinit var lastSeqNumber: String
}

