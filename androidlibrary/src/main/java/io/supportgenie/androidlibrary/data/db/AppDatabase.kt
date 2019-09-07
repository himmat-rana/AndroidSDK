package io.supportgenie.androidlibrary.data.db

import android.content.Context  // Needs access to Android context to build DB object
import androidx.room.*
import io.supportgenie.androidlibrary.data.db.*
import io.supportgenie.androidlibrary.data.network.dto.AppUsersWrapper
import io.supportgenie.androidlibrary.data.network.sgApi
import io.supportgenie.androidlibrary.model.*
import kotlinx.coroutines.*

@Database(entities = [Company::class, Agent::class, User::class, Session::class, SessionParticipant::class, ChatMessage::class], version = 4)  // TodoItem is only DB entity
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDao(): CompanyDao
    abstract fun userDao(): UserDao
    abstract fun agentDao(): AgentDao
    abstract fun sessionDao(): SessionDao
    abstract fun sessionParticipantDao(): SessionParticipantDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(ctx: Context): AppDatabase {      // Builds and caches DB object
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(ctx, AppDatabase::class.java, "AppDatabase")
                    //.addCallback(prepopulateCallback(ctx))
                    .build()
            }

            return INSTANCE!!
        }
    }

    fun syncFromServer(appUserId: String) {
        GlobalScope.launch {
            //val appUserId = "5c01c77a830f787e5e22135d"
            withContext(Dispatchers.IO) {
                val appUsersData: AppUsersWrapper? =
                    sgApi.getAppUsers(appUserId).execute().body()  // Logs results to Logcat due to interceptor
                val companies = appUsersData?.companies

                companies?.forEach {
                    companyDao().insertOrUpdate(Company(it))
                    val companyAgentsWrapper = sgApi.getCompanyAgents(it.companyId).execute().body()
                    val agents = companyAgentsWrapper?.agents
                    agents?.forEach {
                        agentDao().insertOrUpdate(Agent(it))
                    }
                }

                val users = appUsersData?.users
                users?.forEach {
                    userDao().insertOrUpdate(User(it))
                    val userId = it.userId
                    val sessions = sgApi.getUserSessions(userId).execute().body()?.sessions  // Logs results to Logcat due to interceptor
                    sessions?.forEach {
                        println("got session for user $userId $it")
                        val sessionId = it.sessionId
                        val companyId = it.companyId
                        sessionDao().insertOrUpdate(Session(userId, it))
                        val participants = it.participants
                        participants?.forEach {
                            sessionParticipantDao().insertOrUpdate(SessionParticipant(sessionId, companyId, it))
                        }
                        val messages = sgApi.getLatestMessages(sessionId).execute().body()?.messages
                        messages?.forEach {
                            chatMessageDao().insertOrUpdate(ChatMessage(companyId, it))
                        }
                    }
                }
            }
        }
    }
}

