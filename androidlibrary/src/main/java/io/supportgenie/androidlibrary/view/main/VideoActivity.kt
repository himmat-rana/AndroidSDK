package io.supportgenie.androidlibrary.view.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import io.supportgenie.androidlibrary.R
import kotlinx.android.synthetic.main.activity_video.*
import java.io.File

class VideoActivity : AppCompatActivity() {
    private var player: SimpleExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_video)
    }

    override fun onStart() {
        super.onStart()
        playVideo()
    }

    private fun playVideo() {
        player = ExoPlayerFactory.newSimpleInstance(this@VideoActivity, DefaultTrackSelector())
        playerView.player = player
        player!!.addVideoListener(object : VideoListener {
            override fun onVideoSizeChanged(
                width: Int,
                height: Int,
                unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {
            }

            override fun onRenderedFirstFrame() {
//            Log.d("appLog", "onRenderedFirstFrame")
            }
        })
        player!!.addListener(object : PlayerEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                when (playbackState) {
                    Player.STATE_READY -> Log.d("appLog", "STATE_READY")
                    Player.STATE_BUFFERING -> Log.d("appLog", "STATE_BUFFERING")
                    Player.STATE_IDLE -> Log.d("appLog", "STATE_IDLE")
                    Player.STATE_ENDED -> Log.d("appLog", "STATE_ENDED")
                }
            }
        })
        player!!.volume = 1f
        playerView.useController = true
        player!!.playWhenReady = true
        player!!.repeatMode = Player.REPEAT_MODE_ALL
//      player!!.playVideoFromUrl(this@VideoActivity, "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4")

        val intent = intent
        val message = intent.getStringExtra("message")
        val uri = Uri.parse(message)
//        val uri = Uri.parse("https://www.youtube.com/watch?v=EngW7tLk6R8")
        println("videoUrl inside VideoActivity : $message")

        player!!.playVideoFromUri(this@VideoActivity, uri)
    }

    override fun onStop() {
        super.onStop()
        playerView.player = null
        player!!.release()
        player = null
    }


    abstract class PlayerEventListener : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
        override fun onSeekProcessed() {}
        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}
        override fun onPlayerError(error: ExoPlaybackException?) {}
        override fun onLoadingChanged(isLoading: Boolean) {}
        override fun onPositionDiscontinuity(reason: Int) {}
        override fun onRepeatModeChanged(repeatMode: Int) {}
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {}
    }

    companion object {
        @JvmStatic
        fun getUserAgent(context: Context): String {
            val packageManager = context.packageManager
            val info = packageManager.getPackageInfo(context.packageName, 0)
            val appName = info.applicationInfo.loadLabel(packageManager).toString()
            return Util.getUserAgent(context, appName)
        }
    }

    fun SimpleExoPlayer.playVideoFromUri(context: Context, uri: Uri) {
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            getUserAgent(context)
        )
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        prepare(mediaSource)
    }


    fun SimpleExoPlayer.playVideoFromUrl(context: Context, url: String) = playVideoFromUri(context, Uri.parse(url))

    fun SimpleExoPlayer.playVideoFile(context: Context, file: File) = playVideoFromUri(context, Uri.fromFile(file))
}

