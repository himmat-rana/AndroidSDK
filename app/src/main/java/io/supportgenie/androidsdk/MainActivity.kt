package io.supportgenie.androidsdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.supportgenie.androidlibrary.LibraryActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test.setOnClickListener {
            startActivity(Intent(this,LibraryActivity::class.java).putExtra("userId","5dc94f77830f782bc687b8b5"))
        }
    }
}
