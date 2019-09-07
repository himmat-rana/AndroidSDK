package io.supportgenie.androidlibrary.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.*
import android.view.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import io.supportgenie.androidlibrary.R
import io.supportgenie.androidlibrary.model.*
import io.supportgenie.androidlibrary.view.common.*
import io.supportgenie.androidlibrary.viewmodel.*
import kotlinx.android.synthetic.main.fragment_sessions.*
import kotlinx.coroutines.*

class SessionsFragment(private val onOpenChatSession: (sessionId: String, companyId: String) -> Unit) : Fragment() {

    private lateinit var sessionsViewModal: SessionsViewModel

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        sessionsViewModal = getViewModel(SessionsViewModel::class)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sessions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSesessionsRecyclerView()  // Will come from MainActivity
        //setUpSwipeRefresh()        // Implemented later
        observeCompanies()
        observeSessions()
        observeLastMessages()
    }

    private fun setUpSesessionsRecyclerView() = with(rvSessions) {
        //adapter = SessionsListAdapter(emptyList<SessionData>())
        adapter = SessionsListAdapter(emptyList<Session>(), emptyList<Company>(), emptyList<ChatMessage>(), onOpenChatSession)
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(DividerItemDecoration(
            context, LinearLayoutManager.VERTICAL
        ))
        setHasFixedSize(true)
    }

    private fun observeCompanies() = GlobalScope.launch {  // Updates list when favorites change
        val companiesData = sessionsViewModal.getCompanies()
        withContext(Dispatchers.Main) {
            companiesData.observe(this@SessionsFragment, Observer { companies ->
                companies?.let {
                    (rvSessions.adapter as? SessionsListAdapter)?.setCompanies(companies)
                }
            })
        }
    }

    private fun observeSessions() = GlobalScope.launch {  // Updates list when favorites change
        val sessionsData = sessionsViewModal.getSessions()
        withContext(Dispatchers.Main) {
            sessionsData.observe(this@SessionsFragment, Observer { sessions ->
                sessions?.let {
                    (rvSessions.adapter as? SessionsListAdapter)?.setSessions(sessions)
                }
            })
        }
    }

    private fun observeLastMessages() = GlobalScope.launch {  // Updates list when favorites change
        val lastMessagesData = sessionsViewModal.getLastMessages()
        withContext(Dispatchers.Main) {
            lastMessagesData.observe(this@SessionsFragment, Observer { lastMessages ->
                lastMessages?.let {
                    (rvSessions.adapter as? SessionsListAdapter)?.setLastMessages(lastMessages)
                }
            })
        }
    }
}

