package io.supportgenie.androidlibrary.data.db

import androidx.lifecycle.*
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import io.supportgenie.androidlibrary.model.*
import androidx.room.Transaction
import androidx.room.Delete
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Dao

@Dao
interface CompanyDao {

    @Query("SELECT * FROM company")
    fun loadAll(): LiveData<List<Company>>  // Note LiveData return type

    @Query("SELECT companyId FROM company")
    fun loadAllIds(): LiveData<List<String>>

    @Insert(onConflict = IGNORE)  // Do nothing if food with same NDBNO already exists
    fun insert(company: Company) : Long

    @Delete
    fun delete(company: Company)

    @Update
    fun update(entity: Company)

    @Transaction
    fun insertOrUpdate(entity: Company) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

}

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun loadAll(): LiveData<List<User>>  // Note LiveData return type

    @Query("SELECT userId FROM user")
    fun loadAllIds(): List<String>

    @Query("SELECT userId FROM user where companyId=:companyId")
    fun getUserId(companyId: String): String

    @Insert(onConflict = IGNORE)  // Do nothing if food with same NDBNO already exists
    fun insert(user: User) : Long

    @Delete
    fun delete(user: User)

    @Update
    fun update(entity: User)

    @Transaction
    fun insertOrUpdate(entity: User) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

}

@Dao
interface AgentDao {

    @Query("SELECT * FROM agent")
    fun loadAll(): LiveData<List<Agent>>  // Note LiveData return type

    @Query("SELECT * FROM agent where companyId=:companyId")
    fun loadForCompany(companyId: String): LiveData<List<Agent>>  // Note LiveData return type

    @Query("SELECT userId FROM agent")
    fun loadAllIds(): List<String>

    @Insert(onConflict = IGNORE)  // Do nothing if food with same NDBNO already exists
    fun insert(agent: Agent) : Long

    @Delete
    fun delete(agent: Agent)

    @Query("UPDATE agent set isAvailable=:isAvailable where userId=:userId")
    fun updateAvailable(isAvailable: Boolean, userId: String)

    @Update
    fun update(entity: Agent)

    @Transaction
    fun insertOrUpdate(entity: Agent) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

}

@Dao
interface SessionDao {

    @Query("SELECT * FROM session order by localUpdatedAt DESC")
    fun loadAll(): LiveData<List<Session>>  // Note LiveData return type

    @Query("SELECT sessionId FROM session")
    fun loadAllIds(): List<String>

    @Query("SELECT * FROM session where sessionId=:sessionId")
    fun getSession(sessionId: String): LiveData<Session>  // Note LiveData return type

    @Insert(onConflict = IGNORE)  // Do nothing if food with same NDBNO already exists
    fun insert(session: Session) : Long

    @Delete
    fun delete(session: Session)

    @Update
    fun update(entity: Session)

    @Transaction
    fun insertOrUpdate(entity: Session) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

}

@Dao
interface SessionParticipantDao {

    @Query("SELECT * FROM session_participant where sessionId=:sessionId")
    fun loadForSession(sessionId:String): LiveData<List<SessionParticipant>>  // Note LiveData return type

    @Insert(onConflict = IGNORE)  // Do nothing if food with same NDBNO already exists
    fun insert(sessionParticipant: SessionParticipant) : Long

    @Delete
    fun delete(sessionParticipant: SessionParticipant)

    @Update
    fun update(entity: SessionParticipant)

    @Transaction
    fun insertOrUpdate(entity: SessionParticipant) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

}

@Dao
interface ChatMessageDao {

    @Query("SELECT * FROM chat_message")
    fun loadAll(): LiveData<List<ChatMessage>>  // Note LiveData return type

    @Query("select * from chat_message group by sessionId order by updatedAt DESC")
    fun loadAllLast(): LiveData<List<ChatMessage>>  // Note LiveData return type

    @Query("select * from chat_message where sessionId=:sessionId order by localMessageTime ASC")
    fun loadForSession(sessionId: String): LiveData<List<ChatMessage>>  // Note LiveData return type

    @Query("SELECT messageId FROM chat_message")
    fun loadAllIds(): List<String>

    @Insert(onConflict = IGNORE)  // Do nothing if food with same NDBNO already exists
    fun insert(chatMessage: ChatMessage) : Long

    @Delete
    fun delete(chatMessage: ChatMessage)

    @Update
    fun update(entity: ChatMessage)

    @Transaction
    fun insertOrUpdate(entity: ChatMessage) {
        if (insert(entity) == -1L) {
            update(entity)
        }
    }

}

