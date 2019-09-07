package io.supportgenie.androidlibrary.viewmodel

import android.app.Application
import androidx.lifecycle.*
import io.supportgenie.androidlibrary.data.db.*
import io.supportgenie.androidlibrary.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatMessagesViewModel(app: Application) : AndroidViewModel(app) {
    private val agentDao by lazy { AppDatabase.getDatabase(getApplication()).agentDao()}
    private val sessionDao by lazy { AppDatabase.getDatabase(getApplication()).sessionDao()}
    private val chatMessageDao by lazy { AppDatabase.getDatabase(getApplication()).chatMessageDao()}
    private val sessionParticipantDao by lazy { AppDatabase.getDatabase(getApplication()).sessionParticipantDao()}

    suspend fun getAgentsForCompany(companyId: String) : LiveData<List<Agent>> = withContext(Dispatchers.IO) {
        agentDao.loadForCompany(companyId)
    }

    suspend fun getSession(sessionId: String) : LiveData<Session> = withContext(Dispatchers.IO) {
        sessionDao.getSession(sessionId)
    }

    suspend fun getSessionParticipants(sessionId: String) : LiveData<List<SessionParticipant>> = withContext(Dispatchers.IO) {
        sessionParticipantDao.loadForSession(sessionId)
    }

    suspend fun getMessages(sessionId: String) : LiveData<List<ChatMessage>> = withContext(Dispatchers.IO) {
        chatMessageDao.loadForSession(sessionId)
    }
}
