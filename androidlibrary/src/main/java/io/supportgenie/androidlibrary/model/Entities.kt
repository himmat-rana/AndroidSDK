package io.supportgenie.androidlibrary.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import io.supportgenie.androidlibrary.data.network.dto.*
import io.supportgenie.androidlibrary.utils.getDbDateFromApiDate
import com.google.gson.Gson
import io.supportgenie.androidlibrary.data.network.pubsub.PubSubChatMessageDto

// refer to https://stackoverflow.com/questions/7363112/best-way-to-work-with-dates-in-android-sqlite
// for an eplanation of why we use long to store date

@Entity(tableName = "company", primaryKeys = ["companyId"])
data class Company(val companyId: String, val name : String, val photoUrl: String, val website:String,
                   val updatedAt: String, val localUpdatedAt: Long) {
    constructor(companyDto: CompanyDto) : this(companyDto.companyId, companyDto.name, companyDto.photoUrl,
        companyDto.website, companyDto.updatedAt, getDbDateFromApiDate(companyDto.updatedAt)
    )
    //TODO: added temp function for anonymous user
    constructor(userWrapper: UserWrapper): this(userWrapper.companyId,"Naanmatic",userWrapper.photoUrl,"https://www.naanmatic.com",userWrapper.updatedAt,
        getDbDateFromApiDate(userWrapper.updatedAt))

}


@Entity(tableName = "agent", primaryKeys = ["userId"]
    /*indices = arrayOf(
        Index(value = ["companyId", "userId"], unique = true)
    ),
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["companyId"]
        )]*/
    )
data class Agent(val userId: String, val companyId : String, val name: String, val email: String, val phone: String,
                 val status: String, val isAvailable:Boolean, val photoUrl: String, val updatedAt: String, val localUpdatedAt: Long) {
    constructor(agentDto: AgentDto) : this(agentDto.userId, agentDto.companyId, agentDto.name, agentDto.email,
        "", agentDto.status, false, agentDto.photoUrl, agentDto.updatedAt,
        getDbDateFromApiDate(agentDto.updatedAt)
    )
}

@Entity(tableName = "user", primaryKeys = ["userId"]
    /*indices = arrayOf(
        Index(value = ["companyId", "userId"], unique = true)
    ),
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["companyId"]
        )]*/
    )
data class User(val userId: String, val companyId : String, val isActive: Boolean, val updatedAt: String,
                val localUpdatedAt: Long) {
    constructor(userDto: UserDto) : this(userDto.userId, userDto.companyId, userDto.isActive, userDto.updatedAt,
        getDbDateFromApiDate(userDto.updatedAt)
    )

    //TODO: added constructor for anonymous user
    constructor(user: UserWrapper): this(user.userId,user.companyId,user.isActive,user.updatedAt, getDbDateFromApiDate(user.updatedAt))

}

@Entity(tableName = "session", primaryKeys = ["sessionId"]
    /*foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["companyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"]
        )]*/
    )
data class Session(val sessionId: String, val companyId : String, val userId: String, val type: String,
                   val status: String, val localCreatedAt: Long, val updatedAt: String, val localUpdatedAt: Long) {
    constructor(userId:String, sessionDto: SessionDto) : this(sessionDto.sessionId, sessionDto.companyId, userId,
        sessionDto.type, sessionDto.status,
        getDbDateFromApiDate(sessionDto.createdAt), sessionDto.updatedAt,
        getDbDateFromApiDate(sessionDto.updatedAt)
    )
    constructor(userId:String, sessionDataDto: SessionDataDto) : this(sessionDataDto.sessionId, sessionDataDto.companyId, userId,
        sessionDataDto.type, sessionDataDto.status,
        getDbDateFromApiDate(sessionDataDto.createdAt), sessionDataDto.updatedAt,
        getDbDateFromApiDate(sessionDataDto.updatedAt)
    )
}

@Entity(tableName = "session_participant", primaryKeys = ["sessionId", "participant"]
    /*indices = arrayOf(
        Index(value = ["companyId"],unique = false)
    ),
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["companyId"]
        ),
        ForeignKey(
            entity = Session::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"]
        )]*/
    )
data class SessionParticipant(val sessionId: String, val companyId : String, val participant: String,
                              val participantType: String, val status: String, val localCreatedAt: Long,
                              val updatedAt: String, val localUpdatedAt: Long) {
    constructor(sessionId:String, companyId: String, participantDto: ParticipantDto) : this(sessionId,
        companyId, participantDto.participant, participantDto.participantType, participantDto.status,
        getDbDateFromApiDate(participantDto.createdAt), participantDto.updatedAt,
        getDbDateFromApiDate(participantDto.updatedAt)
    )
}


@Entity(tableName = "chat_message", primaryKeys = ["messageId"]
    /*indices = arrayOf(
        Index(value = ["sessionId"], unique = false),
        Index(value = ["companyId"], unique = false)
    ),
    foreignKeys = [
        ForeignKey(
            entity = Company::class,
            parentColumns = ["companyId"],
            childColumns = ["companyId"]
        ),
        ForeignKey(
            entity = Session::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"]
        )]*/
    )
data class ChatMessage(val messageId:String, val sessionId: String, val companyId : String, val sender: String,
                       val senderType: String, val message:String, val mimeType:String, val extraMessageData:String,
                       val localMessageTime: Long, val localCreatedAt: Long,
                       val updatedAt: String, val localUpdatedAt: Long, val retries:Int, val localFile:String,
                       val progress:Float, val deliveryStatus:String) {
    constructor(companyId: String, messageDto: MessageDto) : this(messageDto.messageId, messageDto.sessionId, companyId,
        messageDto.sender, messageDto.senderType, messageDto.message, messageDto.mimeType,
        Gson().toJson(messageDto.extraMessageData),
        getDbDateFromApiDate(messageDto.messageTime),
        getDbDateFromApiDate(messageDto.createdAt), messageDto.updatedAt,
        getDbDateFromApiDate(messageDto.updatedAt),
        0, "", 0.0f, "")
    constructor(pubsubChatMessage: PubSubChatMessageDto) : this(pubsubChatMessage.messageId, pubsubChatMessage.sessionId,
        pubsubChatMessage.companyId, pubsubChatMessage.sender, pubsubChatMessage.senderType,
        pubsubChatMessage.message, pubsubChatMessage.mimeType, Gson().toJson(pubsubChatMessage.extraMessageData),
        getDbDateFromApiDate(pubsubChatMessage.messageTime),
        getDbDateFromApiDate(pubsubChatMessage.createdAt),
        pubsubChatMessage.updatedAt,
        getDbDateFromApiDate(pubsubChatMessage.updatedAt),
        0, "", 0.0f, "")

}

