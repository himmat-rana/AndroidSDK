package io.supportgenie.androidlibrary.view.main

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import io.supportgenie.androidlibrary.R

import kotlinx.android.synthetic.main.activity_chat.*
import android.content.Intent
import io.supportgenie.androidlibrary.view.common.addFragmentToState
import io.supportgenie.androidlibrary.view.common.replaceFragment

private const val CHAT_FRAGMENT_TAG = "CHAT_FRAGMENT"

class ChatActivity : AppCompatActivity() {
    private lateinit var chatFragment: ChatActivityFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        recoverOrBuildChatFragment()
        replaceFragment(R.id.chatView, chatFragment)  // Replaces the placeholder
    }

    private fun recoverOrBuildChatFragment() {
        val fragment = supportFragmentManager  // Tries to load fragment from state
            .findFragmentByTag(CHAT_FRAGMENT_TAG) as? ChatActivityFragment
        if (fragment == null) setUpChatFragment() else chatFragment = fragment
    }

    private fun setUpChatFragment() {  // Sets up search fragment and stores to state
        val companyId = intent.getStringExtra("companyId")
        val sessionId = intent.getStringExtra("sessionId")
        chatFragment = ChatActivityFragment(sessionId, companyId)
        addFragmentToState(R.id.chatView, chatFragment, CHAT_FRAGMENT_TAG)
    }


}
