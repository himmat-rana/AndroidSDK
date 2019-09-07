package io.supportgenie.androidlibrary.view.main

import android.text.format.DateFormat
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import io.supportgenie.androidlibrary.R
import io.supportgenie.androidlibrary.model.*
//import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.rv_session_item.*
import com.bumptech.glide.*
import io.supportgenie.androidlibrary.utils.*

import io.supportgenie.androidlibrary.view.main.LayoutContainer1
import kotlinx.android.synthetic.main.rv_session_item.view.*

class SessionsListAdapter(
    private var sessions: List<Session>,
    private var companies: List<Company>,
    private var lastMessages: List<ChatMessage>,
    private var onOpenChatSession: (sessionId: String, companyId: String) -> Unit // Uses a read-only list of items to display
) : RecyclerView.Adapter<SessionsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)  // Inflates layout to create view
            .inflate(R.layout.rv_session_item, parent, false)
        return ViewHolder(view)  // Creates view holder that manages the list item view
    }

    override fun getItemCount(): Int = sessions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = sessions[position]
        val companyId = session.companyId
        val sessionId = session.sessionId
        var company: Company? = null
        companies.forEach {
            if (it.companyId == companyId) {
                company = it
            }
        }

        var chatMessage: ChatMessage? = null
        lastMessages.forEach {
            if (it.sessionId == sessionId) {
                chatMessage = it
            }
        }

        holder.containerView.setOnClickListener {
            println("clicked open session $sessionId")
            onOpenChatSession(sessionId, companyId)
        }

        holder.bindTo(session, company, chatMessage)  // Binds data to a list item
    }

    // In this app, we'll usually replace all items so DiffUtil has little use
    fun setSessions(newSessions: List<Session>) {
        this.sessions = newSessions   // Replaces whole list
        notifyDataSetChanged()  // Notifies recycler view of data changes to re-render
    }

    // In this app, we'll usually replace all items so DiffUtil has little use
    fun setCompanies(newCompanies: List<Company>) {
        this.companies = newCompanies   // Replaces whole list
        notifyDataSetChanged()  // Notifies recycler view of data changes to re-render
    }

    // In this app, we'll usually replace all items so DiffUtil has little use
    fun setLastMessages(newLastMessages: List<ChatMessage>) {
        this.lastMessages = newLastMessages   // Replaces whole list
        notifyDataSetChanged()  // Notifies recycler view of data changes to re-render
    }

    inner class ViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer1 {

        fun bindTo(
            session: Session,
            company: Company?,
            message: ChatMessage?
        ) {  // Populates text views and star image to show a food
            itemView.tvCompanyName.text = company?.name
            itemView.tvSessionStatus.text = session.status
            val dateFormat = DateFormat.getDateFormat(itemView.tvSessionStatus.getContext())

            val isSenderMe = message?.senderType == "customer"
            var messageDisplay: String? = null
            message?.mimeType?.let { mimeType ->
                message.message.let { messageContent ->
                    if (isMimeTypeText(mimeType)) {
                        if (isSenderMe) {
                            messageDisplay = "You: $messageContent"
                        } else {
                            messageDisplay = "$messageContent"
                        }
                    } else if (isMimeTypeImage(mimeType)) {
                        if (isSenderMe) {
                            messageDisplay = "Image Sent"
                        } else {
                            messageDisplay = "Image Received"
                        }
                    } else if (isMimeTypeVideo(mimeType)) {
                        if (isSenderMe) {
                            messageDisplay = "Video Sent"
                        } else {
                            messageDisplay = "Video Received"
                        }
                    } else if (isMimeTypeDocument(mimeType)) {
                        if (isSenderMe) {
                            messageDisplay = "Document Sent"
                        } else {
                            messageDisplay = "Document Received"
                        }
                    } else if (isMimeTypeAdaptiveCard(mimeType)) {
                        if (isSenderMe) {
                            messageDisplay = "Message Sent"
                        } else {
                            messageDisplay = "Message Received"
                        }
                    }
                }
            }
            val maxMessageLength = 50
            if (messageDisplay != null) {
                if (messageDisplay!!.length < maxMessageLength) {
                    itemView.tvLatestMessage.text = messageDisplay
                } else {
                    messageDisplay = messageDisplay!!.take(maxMessageLength)
                    itemView.tvLatestMessage.text = "$messageDisplay ..."
                }
            }
            try {
                itemView.tvSessionTime.text = dateFormat.format(session.localUpdatedAt) ?: ""
            } catch (e: IllegalArgumentException) {
                println(e.message)
                println("got exception ")
                print(message?.localMessageTime)
            }
            Glide.with(containerView)
                .asBitmap()
                .load(company?.photoUrl)
                .into(itemView.ivCompanyLogo)
        }
    }
}
