package io.supportgenie.androidlibrary.data.network

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

import io.supportgenie.androidlibrary.data.network.dto.*
import io.supportgenie.androidlibrary.model.SessionParticipant
import okhttp3.RequestBody
import org.json.JSONObject

// json handling taken from
// https://stackoverflow.com/questions/21398598/how-to-post-raw-whole-json-in-the-body-of-a-retrofit-request

interface SgApi {

    @GET("user/app/{appUserId}")              // Is appended to the base URL
    fun getAppUsers(
        @Path("appUserId") appUserId: String
    ): Call<AppUsersWrapper>

    @GET("user/sessions/{userId}")              // Is appended to the base URL
    fun getUserSessions(
        @Path("userId") userId: String
    ): Call<UserSessionsWrapper>

    @GET("agent/company/{companyId}")              // Is appended to the base URL
    fun getCompanyAgents(
        @Path("companyId") companyId: String
    ): Call<CompanyAgentsWrapper>

    @GET("message/latest/{sessionId}")              // Is appended to the base URL
    fun getLatestMessages(
        @Path("sessionId") sessionId: String
    ): Call<ChatMessagesWrapper>

    @GET("session/{sessionId}")              // Is appended to the base URL
    fun getSessionData(
        @Path("sessionId") sessionId: String
    ): Call<SessionDataDto>

    @GET("session/participants/{sessionId}")              // Is appended to the base URL
    fun getSessionParticipants(
        @Path("sessionId") sessionId: String
    ): Call<SessionParticipantWrapper>

    @Headers("Content-Type: application/json")
    @POST("message/send")              // Is appended to the base URL
    fun sendMessage(
        @Body sendMessageData: SendMessageData
    ): Call<SendMessageWrapper>


    @Headers("Content-Type: application/json")
    @POST("session/end/{sessionId}")
    fun updateStatus(
        @Path("sessionId") sessionId: String
    ):
//       Call<SessionParticipantWrapper>
            Call<UpdateSessionWrapper>
}

