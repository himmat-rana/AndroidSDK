package io.supportgenie.androidlibrary.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.barteksc.pdfviewer.PDFView
import android.os.AsyncTask
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
//import io.supportgenie.androidlibrary.MainActivity
import io.supportgenie.androidlibrary.R
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_pdf_viewer)

        pdfView = findViewById<View>(R.id.pdfView) as PDFView
        val bundle = intent.getStringExtra("message")
        println("pdfUrl inside PdfViewerActivity : $bundle")
        RetrievePDFStream().execute(bundle)
    }
    companion object {
        lateinit var pdfView:PDFView
        class RetrievePDFStream : AsyncTask<String, Void, InputStream>() {
            override fun doInBackground(vararg strings: String): InputStream? {
                var inputStream: InputStream? = null
                try {
                    val url = URL(strings[0])
                    val urlConnection = url.openConnection() as HttpURLConnection
                    if (urlConnection.responseCode == 200) {
                        inputStream = BufferedInputStream(urlConnection.inputStream)
                    }
                } catch (e: IOException) {
                    return null
                }
                return inputStream
            }

            override fun onPostExecute(inputStream: InputStream) {
                pdfView.fromStream(inputStream).load()
            }
        }
    }
//    override fun onBackPressed() {
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//    }
}


//class PdfViewerActivity : AppCompatActivity() {
//    //    var pdfView: PDFView? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pdf_viewer)
//        pdfView = findViewById<View>(R.id.pdfView) as PDFView
//        RetrievePDFStream().execute("https://kotlinlang.org/docs/kotlin-docs.pdf")
//    }
//
//    companion object {
//        lateinit var pdfView: PDFView
//
//        class RetrievePDFStream : AsyncTask<String, Void, InputStream>() {
//            override fun doInBackground(vararg strings: String): InputStream? {
//                var inputStream: InputStream? = null
//                try {
//                    val url = URL(strings[0])
//                    val urlConnection = url.openConnection() as HttpURLConnection
//                    if (urlConnection.responseCode == 200) {
//                        inputStream = BufferedInputStream(urlConnection.inputStream)
//                    }
//                } catch (e: IOException) {
//                    return null
//                }
//                return inputStream
//            }
//
//            override fun onPostExecute(inputStream: InputStream) {
//                pdfView!!.fromStream(inputStream).load()
//            }
//        }
//    }

//        pdfView!!.fromAsset("pdfexample.pdf")
//            .enableSwipe(true) // allows to block changing pages using swipe
//            .swipeHorizontal(false)
//            .enableDoubletap(true)
//            .defaultPage(0)
//            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//            .password(null)
//            .scrollHandle(null)
//            .enableAntialiasing(true)
//            .spacing(0)
//            .pageFitPolicy(FitPolicy.WIDTH)
//            .load()

//             pdfView!!.fromUri(Uri.parse("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"))
//            .enableSwipe(true) // allows to block changing pages using swipe
//            .swipeHorizontal(false)
//            .enableDoubletap(true)
//            .onDraw { canvas, pageWidth, pageHeight, displayedPage -> }
//            .onDrawAll { canvas, pageWidth, pageHeight, displayedPage -> }
//            .onPageChange { page, pageCount -> }
//            .onPageError { page, t ->
//                Toast.makeText(this, "Error: while opening page." + page, Toast.LENGTH_LONG).show()
//            }
//            .onTap { false }
//            .defaultPage(0)
//            .enableAnnotationRendering(true)
//            .password(null)
//            .scrollHandle(null)
//            .enableAntialiasing(true)
//            .spacing(0)
//            .load()
//}
//}



