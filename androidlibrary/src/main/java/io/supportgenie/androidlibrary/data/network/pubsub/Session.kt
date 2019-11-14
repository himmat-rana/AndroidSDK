package io.supportgenie.androidlibrary.data.network.pubsub

import android.content.Context
import io.supportgenie.androidlibrary.data.db.AppDatabase
import io.supportgenie.androidlibrary.data.network.sgApi
import io.supportgenie.androidlibrary.model.Agent
import io.supportgenie.androidlibrary.model.ChatMessage
import io.supportgenie.androidlibrary.model.Session
import io.supportgenie.androidlibrary.model.SessionParticipant
import io.supportgenie.androidlibrary.utils.parseJsonValue
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PubSubSessionDto {
    lateinit var sessionId: String
}

@JsonClass(generateAdapter = true)
class PubSubSessionParticipantDto {
    lateinit var sessionId: String
    lateinit var companyId: String
}

private fun updateSessionData(userId: String, sessionId: String, context:Context) {
    val db = AppDatabase.getDatabase(context)
    val sessionDataDto = sgApi.getSessionData(sessionId).execute().body()
    val sessionDao = db.sessionDao()
    if (sessionDataDto != null) {
        sessionDao.insert(Session(userId, sessionDataDto))
    }
    val companyId = sessionDataDto?.companyId
    if (companyId != null) {
        updateSessionParticipantData(userId, sessionId, companyId, context)
    }
}

private fun updateSessionParticipantData(userId: String, sessionId: String, companyId: String, context:Context) {
    val db = AppDatabase.getDatabase(context)
    val sessionParticipantsWrapper = sgApi.getSessionParticipants(sessionId).execute().body()
    val sessionParticipantsDto = sessionParticipantsWrapper?.participants
    val sessionParticipantdao = db.sessionParticipantDao()
    sessionParticipantsDto?.forEach {

        //TODO : used insertOrUpdate instead of insert
        sessionParticipantdao.insert(SessionParticipant(sessionId, companyId, it))
    }
}

fun listenForSessionEvents(userId:String, context: Context) {
    val pubsub:PubSub = PubSub.getPubsub()
    pubsub.registerListener("io.supportgenie.session.new.$userId") {
        if (it != null) {
            val dto: PubSubSessionDto? = parseJsonValue<PubSubSessionDto>(it, PubSubSessionDto::class.java)

            if (dto != null) {
                val sessionId = dto.sessionId
                updateSessionData(userId, sessionId, context)
            }
        }
    }

    pubsub.registerListener("io.supportgenie.session.updated.user.$userId") {
        if (it != null) {
            val dto: PubSubSessionDto? = parseJsonValue<PubSubSessionDto>(it, PubSubSessionDto::class.java)
            if (dto != null) {
                val sessionId = dto.sessionId
                updateSessionData(userId, sessionId, context)
            }
        }
    }

    pubsub.registerListener("io.supportgenie.session.participant.added.user.$userId") {
        if (it != null) {
            val dto: PubSubSessionParticipantDto? = parseJsonValue<PubSubSessionParticipantDto>(it, PubSubSessionParticipantDto::class.java)
            val sessionId = dto?.sessionId
            val companyId = dto?.companyId
            if ((sessionId != null) && (companyId != null)) {
                updateSessionParticipantData(userId, sessionId, companyId, context)
            }
        }
    }
}

