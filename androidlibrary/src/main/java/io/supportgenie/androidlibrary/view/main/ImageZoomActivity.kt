package io.supportgenie.androidlibrary.view.main

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import io.supportgenie.androidlibrary.R
import kotlinx.android.synthetic.main.activity_image_zoom.*

class ImageZoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_image_zoom)
        imageView = findViewById(R.id.ivZoom)
        val intent = intent
        val message = intent.getStringExtra("message")
        println("photoUrl inside ImageZoomActivity : $message")

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 7f
        circularProgressDrawable.centerRadius = 50f
        circularProgressDrawable.setColorFilter((Color.rgb(255, 0, 0)), PorterDuff.Mode.SRC_OVER)
        circularProgressDrawable.start()

        if (message != "") {
            Glide.with(this)
                .asBitmap()
                .load(message)
                .placeholder(circularProgressDrawable)
                .error(R.mipmap.image_not_found)
//                .thumbnail(0.1f)
                .into(ivZoom)
        } else {
            Glide.with(this)
                .asBitmap()
                .load(R.mipmap.image_not_found)
                .placeholder(circularProgressDrawable)
                .into(ivZoom)
        }
    }

    companion object {
        lateinit var imageView: ImageView
    }

}
