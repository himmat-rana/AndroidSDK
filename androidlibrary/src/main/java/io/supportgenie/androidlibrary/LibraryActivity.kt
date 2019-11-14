package io.supportgenie.androidlibrary

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import io.supportgenie.androidlibrary.data.db.AppDatabase
import io.supportgenie.androidlibrary.data.network.dto.NewSessionWrapper
import io.supportgenie.androidlibrary.data.network.networkScope
import io.supportgenie.androidlibrary.data.network.pubsub.PubSub
import io.supportgenie.androidlibrary.data.network.sgApi
import io.supportgenie.androidlibrary.model.NewSessionModel
import io.supportgenie.androidlibrary.model.Session

import kotlinx.android.synthetic.main.activity_library.*
import io.supportgenie.androidlibrary.view.common.*
import io.supportgenie.androidlibrary.view.main.ChatActivity
import io.supportgenie.androidlibrary.view.main.SessionsFragment
import io.supportgenie.androidlibrary.viewmodel.PubSubListenersViewModel
import io.supportgenie.androidlibrary.viewmodel.SessionsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SESSIONS_FRAGMENT_TAG = "SESSIONS_FRAGMENT"


class LibraryActivity : AppCompatActivity() {
    private lateinit var sessionsFragment: SessionsFragment
    private lateinit var pubsubListenersViewModel: PubSubListenersViewModel
    private lateinit var appUserId: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appUserId = intent.getStringExtra("userId")!!
        val db = AppDatabase.getDatabase(application)

        networkScope.launch {

            val sessionDao = db.sessionDao()

            val listSession : List<String> = sessionDao.loadAllIds()

            if(listSession.isEmpty()){


                val body = sgApi.createNewSession(NewSessionModel(appUserId,"5c01af56830f7879b727607d")).execute()?.body()

                if(body!=null && body.success){
                    db.syncFromServerTemp(appUserId)

                    val intent = Intent(this@LibraryActivity, ChatActivity::class.java)
                    intent.putExtra("sessionId", body.session!!.sessionId)
                    intent.putExtra("companyId", body.session!!.companyId)
                    startActivity(intent)

                }
            }


        }

        setContentView(R.layout.activity_library)
        setSupportActionBar(toolbar)

        //sessionsViewModel = getViewModel(SessionsViewModel)

        println("inside Main Activity")

        recoverOrBuildSearchFragment()

        replaceFragment(R.id.mainView, sessionsFragment)  // Replaces the placeholder

        //val appUserId = "5c01c77a830f787e5e22135d"
        //TODO: getting the appUserId from intent from where this activity is started. maybe change in future




        db.syncFromServerTemp(appUserId)

        fab.setOnClickListener { view ->
            val dialog = ProgressDialog(this)
            dialog.setCancelable(false)
            dialog.setMessage("Creating new Session...")


            val userDao = AppDatabase.getDatabase(this).userDao()
            dialog.show()

            networkScope.launch {
                val body = sgApi.createNewSession(NewSessionModel(appUserId,"5c01af56830f7879b727607d")).execute()?.body()

                if(body!=null && body.success){
                    db.syncFromServerTemp(appUserId)

                    val intent = Intent(this@LibraryActivity, ChatActivity::class.java)
                    intent.putExtra("sessionId", body.session!!.sessionId)
                    intent.putExtra("companyId", body.session!!.companyId)
                    startActivity(intent)
                    dialog.cancel()
                }
            }
        }

        PubSub.getPubsub().connect("staging-webservice.supportgenie.io")
        pubsubListenersViewModel = getViewModel(PubSubListenersViewModel::class)
        startPubSubListeners(pubsubListenersViewModel, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun recoverOrBuildSearchFragment() {
        val fragment = supportFragmentManager  // Tries to load fragment from state
            .findFragmentByTag(SESSIONS_FRAGMENT_TAG) as? SessionsFragment
        if (fragment == null) setUpSessionsFragment() else sessionsFragment = fragment
    }

    private fun setUpSessionsFragment() {  // Sets up search fragment and stores to state
        sessionsFragment = SessionsFragment { sessionId, companyId ->
            println("open session $sessionId, company $companyId")
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("sessionId", sessionId)
            intent.putExtra("companyId", companyId)
            startActivity(intent)
        }
        addFragmentToState(R.id.mainView, sessionsFragment, SESSIONS_FRAGMENT_TAG)
    }
}
