package io.supportgenie.androidlibrary.viewmodel

import android.app.Application
import androidx.lifecycle.*
import io.supportgenie.androidlibrary.data.db.*
import io.supportgenie.androidlibrary.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SessionsViewModel(app: Application) : AndroidViewModel(app) {
    private val companyDao by lazy { AppDatabase.getDatabase(getApplication()).companyDao()}
    private val sessionDao by lazy { AppDatabase.getDatabase(getApplication()).sessionDao()}
    private val chatMessageDao by lazy { AppDatabase.getDatabase(getApplication()).chatMessageDao()}

    suspend fun getCompanies() : LiveData<List<Company>> = withContext(Dispatchers.IO) {
        companyDao.loadAll()
    }

    suspend fun getSessions() : LiveData<List<Session>> = withContext(Dispatchers.IO) {
        sessionDao.loadAll()
    }

    suspend fun getLastMessages() : LiveData<List<ChatMessage>> = withContext(Dispatchers.IO) {
        chatMessageDao.loadAllLast()
    }
}
