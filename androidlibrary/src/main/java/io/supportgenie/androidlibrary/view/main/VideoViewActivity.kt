package io.supportgenie.androidlibrary.view.main

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import io.supportgenie.androidlibrary.R
import kotlinx.android.synthetic.main.activity_video_view.*

class VideoViewActivity : AppCompatActivity() {
    private var mediaController: MediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_video_view)

        val intent = intent
        val message = intent.getStringExtra("message")
        println("Inside Video View Activity:$message")

//        mediaController?.setPadding(40, 0, 40, 30)
////        videoViewActivity?.setPadding(40,0,40,10)
////        videoView.seekTo(1)
        mediaController = MediaController(this)
        mediaController?.setAnchorView(videoViewActivity)

//        mediaController?.setMediaPlayer(videoViewActivity)

        val uri = Uri.parse(message)
        videoViewActivity?.setMediaController(mediaController)
        videoViewActivity?.setVideoURI(uri)
//        videoViewActivity?.requestFocus()
//        videoViewActivity?.seekTo(10)
//        videoViewActivity?.setZOrderOnTop(true);
        videoViewActivity?.start()
        videoViewActivity?.setOnPreparedListener { mp ->
            mp?.isLooping = true
//            progress_bar.visibility= View.INVISIBLE
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
}
