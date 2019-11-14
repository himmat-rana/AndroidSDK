package io.supportgenie.androidlibrary

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.supportgenie.androidlibrary.data.db.AppDatabase
import io.supportgenie.androidlibrary.data.network.dto.SendMessageData
import io.supportgenie.androidlibrary.data.network.dto.UpdateSessionData
import io.supportgenie.androidlibrary.data.network.pubsub.listenForAgentEvents
import io.supportgenie.androidlibrary.data.network.pubsub.listenForSessionEvents
import io.supportgenie.androidlibrary.data.network.pubsub.listenForUserNewMessageEvents
import io.supportgenie.androidlibrary.data.network.sgApi
import io.supportgenie.androidlibrary.model.ChatMessage
import io.supportgenie.androidlibrary.model.Session
import io.supportgenie.androidlibrary.model.SessionParticipant
import io.supportgenie.androidlibrary.utils.getCurTimeInMillisecs
import io.supportgenie.androidlibrary.view.common.getViewModel
import io.supportgenie.androidlibrary.viewmodel.PubSubListenersViewModel
import io.supportgenie.androidlibrary.viewmodel.SessionsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId

fun startPubSubListeners(viewModal: PubSubListenersViewModel, activity: FragmentActivity) {
    GlobalScope.launch {
        run {
            val usersData = viewModal.getUsers()
            withContext(Dispatchers.Main) {
                usersData.observe(activity as LifecycleOwner, Observer { users ->
                    users?.forEach() {
                        listenForUserNewMessageEvents(it.userId, activity.applicationContext)
                        listenForSessionEvents(it.userId, activity.applicationContext)
                    }
                })
            }
        }

        run {
            val companyIdsData = viewModal.getCompanyIds()
            withContext(Dispatchers.Main) {
                companyIdsData.observe(activity, Observer { companyIds ->
                    companyIds?.forEach() {
                        listenForAgentEvents(it, activity.applicationContext)
                    }
                })
            }
        }
    }
}


fun sendTextMessage(message: String, sessionId: String, companyId: String, context: Context) {
    println("inside sendTextMessage message: $message, sessionId : $sessionId, companyId: $companyId")
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            // add the message to database
            val chaMessageDao = AppDatabase.getDatabase(context).chatMessageDao()
            val userDao = AppDatabase.getDatabase(context).userDao()
            val userId = userDao.getUserId(companyId)
            val messageId: String = ObjectId.get().toString()
            println("messageId is ${messageId}")
            println("userId is ${userId}")
            val mimeType = "text/plain"
            val senderType = "customer"
            val localNow = getCurTimeInMillisecs()
            val chatMessage = ChatMessage(
                messageId, sessionId, companyId, userId, senderType, message, mimeType,
                "", localNow, localNow, "", localNow, 0, "", 0.0F, "sending"
            )
            chaMessageDao.insert(chatMessage)
            println("message added to db")

            // send the message
            val sendMessageData =
                SendMessageData(messageId, mimeType, userId, senderType, message, sessionId)
            val sendMessageResult = sgApi.sendMessage(sendMessageData).execute().body()
            val messageSent: Boolean = sendMessageResult?.success?.let {
                it
            } ?: false

            if (messageSent) {
                println("message sent success")
                updateMessageToDelivered(messageId)
            } else {
                println("message sent failed")
                updateMessageToDeliveryFailed(messageId)
            }
        }
    }
}

fun sendPdfFile(message: String, sessionId: String, companyId: String, context: Context) {
    println("inside sendPdfFile message: $message, sessionId : $sessionId, companyId: $companyId")
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            // add the message to database
            val chaMessageDao = AppDatabase.getDatabase(context).chatMessageDao()
            val userDao = AppDatabase.getDatabase(context).userDao()
            val userId = userDao.getUserId(companyId)
            val messageId: String = ObjectId.get().toString()
            println("messageId is ${messageId}")
            println("userId is ${userId}")
            val mimeType = "application/pdf"
            val senderType = "customer"
            val localNow = getCurTimeInMillisecs()
            val chatMessage = ChatMessage(
                messageId, sessionId, companyId, userId, senderType, message, mimeType,
                "", localNow, localNow, "", localNow, 0, "", 0.0F, "sending"
            )
            chaMessageDao.insert(chatMessage)
            println("message added to db")

            // send the message
            val sendMessageData =
                SendMessageData(messageId, mimeType, userId, senderType, message, sessionId)
            val sendMessageResult = sgApi.sendMessage(sendMessageData).execute().body()
            val messageSent: Boolean = sendMessageResult?.success?.let {
                it
            } ?: false
            if (messageSent) {
                println("message sent success")
                updateMessageToDelivered(messageId)
            } else {
                println("message sent failed")
                updateMessageToDeliveryFailed(messageId)
            }
        }
    }
}

fun sendPngImage(message: String, sessionId: String, companyId: String, context: Context) {
    println("inside sendPngImage message: $message, sessionId : $sessionId, companyId: $companyId")
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            // add the message to database
            val chaMessageDao = AppDatabase.getDatabase(context).chatMessageDao()
            val userDao = AppDatabase.getDatabase(context).userDao()
            val userId = userDao.getUserId(companyId)
            val messageId: String = ObjectId.get().toString()
            println("messageId is ${messageId}")
            println("userId is ${userId}")
            val mimeType = "image/png"
            val senderType = "customer"
            val localNow = getCurTimeInMillisecs()
            val chatMessage = ChatMessage(
                messageId, sessionId, companyId, userId, senderType, message, mimeType,
                "", localNow, localNow, "", localNow, 0, "", 0.0F, "sending"
            )
            chaMessageDao.insert(chatMessage)
            println("message added to db")

            // send the message
            val sendMessageData =
                SendMessageData(messageId, mimeType, userId, senderType, message, sessionId)
            val sendMessageResult = sgApi.sendMessage(sendMessageData).execute().body()
            val messageSent: Boolean = sendMessageResult?.success?.let {
                it
            } ?: false
            if (messageSent) {
                println("message sent success")
                updateMessageToDelivered(messageId)
            } else {
                println("message sent failed")
                updateMessageToDeliveryFailed(messageId)
            }
        }
    }
}

fun sendJpgImage(message: String, sessionId: String, companyId: String, context: Context) {
    println("inside sendJpgImage message: $message, sessionId : $sessionId, companyId: $companyId")
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            // add the message to database

            val chaMessageDao = AppDatabase.getDatabase(context).chatMessageDao()
            val userDao = AppDatabase.getDatabase(context).userDao()
            val userId = userDao.getUserId(companyId)
            val messageId: String = ObjectId.get().toString()
            println("messageId is ${messageId}")
            println("userId is ${userId}")
            val mimeType = "image/jpg"
            val senderType = "customer"
            val localNow = getCurTimeInMillisecs()
            val chatMessage = ChatMessage(
                messageId, sessionId, companyId, userId, senderType, message, mimeType,
                "", localNow, localNow, "", localNow, 0, "", 0.0F, "sending"
            )
            chaMessageDao.insert(chatMessage)
            println("message added to db")

            // send the message
            val sendMessageData =
                SendMessageData(messageId, mimeType, userId, senderType, message, sessionId)
            val sendMessageResult = sgApi.sendMessage(sendMessageData).execute().body()
            val messageSent: Boolean = sendMessageResult?.success?.let {
                it
            } ?: false
            if (messageSent) {
                println("message sent success")
                updateMessageToDelivered(messageId)
            } else {
                println("message sent failed")
                updateMessageToDeliveryFailed(messageId)
            }
        }
    }
}

fun sendMp4Video(message: String, sessionId: String, companyId: String, context: Context) {
    println("inside sendMp4Video message: $message, sessionId : $sessionId, companyId: $companyId")
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            // add the message to database
            val chaMessageDao = AppDatabase.getDatabase(context).chatMessageDao()
            val userDao = AppDatabase.getDatabase(context).userDao()
            val userId = userDao.getUserId(companyId)
            val messageId: String = ObjectId.get().toString()
            println("messageId is ${messageId}")
            println("userId is ${userId}")
            val mimeType = "video/mp4"
            val senderType = "customer"
            val localNow = getCurTimeInMillisecs()
            val chatMessage = ChatMessage(
                messageId, sessionId, companyId, userId, senderType, message, mimeType,
                "", localNow, localNow, "", localNow, 0, "", 0.0F, "sending"
            )
            chaMessageDao.insert(chatMessage)
            println("message added to db")

            // send the message
            val sendMessageData =
                SendMessageData(messageId, mimeType, userId, senderType, message, sessionId)
            val sendMessageResult = sgApi.sendMessage(sendMessageData).execute().body()
            val messageSent: Boolean = sendMessageResult?.success?.let {
                it
            } ?: false
            if (messageSent) {
                println("message sent success")
                updateMessageToDelivered(messageId)
            } else {
                println("message sent failed")
                updateMessageToDeliveryFailed(messageId)
            }
        }
    }
}


fun endChatSession(message: String, sessionId: String, companyId: String, context: Context) {
    println("inside endChatSession message: $message, sessionId : $sessionId, companyId: $companyId")
    GlobalScope.launch {
        withContext(Dispatchers.IO) {
            // add the message to database
            val sessionDao = AppDatabase.getDatabase(context).sessionDao()

//            val sessionDao = AppDatabase.getDatabase(context).sessionParticipantDao()
            val userDao = AppDatabase.getDatabase(context).userDao()
            val userId = userDao.getUserId(companyId)

            val messageId: String = ObjectId.get().toString()
            val status = message
            println("status---------->$status")

            println("messageId is ${messageId}")
            println("companyId is ${companyId}")
            println("sessionId is ${sessionId}")
            println("userId is ${userId}")

//            val mimeType = "text/plain"
//            val mimeType = "application/json"
            val senderType = "customer"
            val localNow = getCurTimeInMillisecs()
            val statusUpdate = Session(
                sessionId, companyId, userId, senderType, message, localNow, "", localNow
            )
//            val statusUpdate = SessionParticipant(
//                sessionId, companyId, userId, senderType, message, localNow, "", localNow
//            )
//           sessionDao.insert(statusUpdate)
//           sessionDao.insertOrUpdate(statusUpdate)

            sessionDao.update(statusUpdate)
            println("statusUpdate------->${statusUpdate}")
            println("status updated to db")

//            // send the message
//            val updateSessionData =
//                UpdateSessionData(
//                    messageId,
//                    mimeType,
//                    userId,
//                    senderType,
//                    message,
//                    sessionId,
//                    companyId
//                )

//            val updateSessionData =
//                UpdateSessionData(
//                    sessionId
//                )

//            val updateSessionData =
//                UpdateSessionData(
//                    status,
//                    sessionId
//                )
//            println("updateSessionData------->${updateSessionData}")
//            val updateSessionResult = sgApi.updateStatus(updateSessionData).execute().body()

            val updateSessionResult = sgApi.updateStatus(sessionId).execute().body()
            val sessionUpdate: Boolean = updateSessionResult?.success?.let {
                it
            } ?: false
            if (sessionUpdate) {
                println("session status update success")
                updateMessageToDelivered(messageId)
            } else {
                println("session status update failed")
                updateMessageToDeliveryFailed(messageId)
            }
        }
    }
}

fun updateMessageToDelivered(messageId: String) {
    updateMessageDeliveryStatus(messageId, "delivered")
}

fun updateMessageToDeliveryFailed(messageId: String) {
    updateMessageDeliveryStatus(messageId, "failed")
}

fun updateMessageDeliveryStatus(messageId: String, status: String) {

}

