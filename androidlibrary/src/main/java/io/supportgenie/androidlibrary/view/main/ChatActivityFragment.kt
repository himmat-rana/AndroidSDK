package io.supportgenie.androidlibrary.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import io.supportgenie.androidlibrary.*
import io.supportgenie.androidlibrary.model.*
import io.supportgenie.androidlibrary.view.common.getViewModel
import io.supportgenie.androidlibrary.view.common.onSubmit
import io.supportgenie.androidlibrary.viewmodel.ChatMessagesViewModel
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.rv_chat_doc_item.*
import kotlinx.android.synthetic.main.rv_chat_image_item.*
import kotlinx.android.synthetic.main.rv_chat_video_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
private const val GALLERY = 1

class ChatActivityFragment(private val sessionId: String, private val companyId: String) :
    Fragment() {
    private lateinit var chatMessagesViewModel: ChatMessagesViewModel

    private lateinit var imagePath: String
    private lateinit var videoPath: String
    private lateinit var pdfFullPath: String

    private lateinit var fileName: String
    private lateinit var pdfFileName: String

    private lateinit var msgPng: String
    private lateinit var msgJpg: String
    private lateinit var msgPdf: String
    private lateinit var msgMp4: String

    private lateinit var s3Client: AmazonS3
    private var bucket = "supportgeniemedia"

    private lateinit var transferUtility: TransferUtility

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        chatMessagesViewModel = getViewModel(ChatMessagesViewModel::class)

        FuelManager.instance.basePath = "https://staging-webservice.supportgenie.io/v3"
        s3credentialsProvider()
        setTransferUtility()

        val uniqueID = UUID.randomUUID().toString()
        println("uniqueID By UUID--->$uniqueID")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    private fun sendTextMessage() {
        val message = etSendMessage.text.toString()

        println("send text message is $message")
        if (message.isNotEmpty()) {
            println("send text message $message for session $sessionId company $companyId")
            this.context?.let {
                sendTextMessage(message, sessionId, companyId, it)
            }
            etSendMessage.setText("")
            rvChatMessages.adapter?.itemCount?.let {
                //rvChatMessages.scrollToPosition(it - 1)
            }
        }
    }

    private fun sendPngImage() {
        val message = msgPng
        println("send png image is $message")
        if (message.isNotEmpty()) {
            println("send png image $message for session $sessionId company $companyId")
            this.context?.let {
                sendPngImage(message, sessionId, companyId, it)
            }
            rvChatMessages.adapter?.itemCount?.let {
                //rvChatMessages.scrollToPosition(it - 1)
            }
        }
    }

    private fun sendJpgImage() {
        val message = msgJpg
        println("send jpg image is $message")
        if (message.isNotEmpty()) {
            println("send jpg image $message for session $sessionId company $companyId")
            this.context?.let {
                sendJpgImage(message, sessionId, companyId, it)
            }
            rvChatMessages.adapter?.itemCount?.let {
                //rvChatMessages.scrollToPosition(it - 1)
            }
        }
    }

    private fun sendMp4Video() {
        val message = msgMp4
        println("send mp4 video is $message")
        if (message.isNotEmpty()) {
            println("send mp4 video $message for session $sessionId company $companyId")
            this.context?.let {
                sendMp4Video(message, sessionId, companyId, it)
            }
            rvChatMessages.adapter?.itemCount?.let {
                //rvChatMessages.scrollToPosition(it - 1)
            }
        }
    }

    private fun sendPdfFile() {
        val message = msgPdf
        println("send pdf file is $message")
        if (message.isNotEmpty()) {
            println("send pdf file $message for session $sessionId company $companyId")
            this.context?.let {
                sendPdfFile(message, sessionId, companyId, it)
            }
            rvChatMessages.adapter?.itemCount?.let {
                //rvChatMessages.scrollToPosition(it - 1)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpChatMessageRecyclerView()  // Will come from MainActivity
        //setUpSwipeRefresh()        // Implemented later
        observeAgentCompanies()
        observeSessions()
        observeSessionParticipants()
        observeMessages()
        btSend.setOnClickListener {
            sendTextMessage()
        }
        etSendMessage.onSubmit {
            sendTextMessage()
        }

//        ibAttach.setOnClickListener {
//            attachFile()
//        }

        ibAdd.setOnClickListener {
            attachFile()
        }

        btEndChat.setOnClickListener {
            endChatSession()
        }
    }

    private fun endChatSession() {
        val status = "closed"
        this.context?.let {
            endChatSession(status, sessionId, companyId, it)
        }
        activity?.onBackPressed()
    }

    private fun setUpChatMessageRecyclerView() = with(rvChatMessages) {
        val linearLayoutManager: LinearLayoutManager? =
            androidx.recyclerview.widget.LinearLayoutManager(context)
        //linearLayoutManager?.stackFromEnd = true
        layoutManager = linearLayoutManager
        //adapter = SessionsListAdapter(emptyList<SessionData>())
        adapter = ChatListAdapter(
            kotlin.collections.emptyList<Agent>(),
            kotlin.collections.emptyList<Session>(),
            kotlin.collections.emptyList<SessionParticipant>(),
            kotlin.collections.emptyList<Company>(),
            kotlin.collections.emptyList<ChatMessage>(),
            fragmentManager
        )
    }

    private fun observeAgentCompanies() = GlobalScope.launch {
        // Updates list when favorites change

//      val companyId="5c01af56830f7879b727607d"
        val companiesData = chatMessagesViewModel.getAgentsForCompany(companyId)
        withContext(Dispatchers.Main) {
            companiesData.observe(this@ChatActivityFragment, Observer { companies ->
                companies?.let {
                    (rvChatMessages.adapter as? ChatListAdapter)?.setAgentsForCompany(companies)
                }
            })
        }
    }

    private fun observeSessions() = GlobalScope.launch {
        // Updates list when favorites change

//      val sessionId="5c99ce01b662766ce5e9a68e"
        val sessionsData = chatMessagesViewModel.getSession(sessionId)
        withContext(Dispatchers.Main) {
            sessionsData.observe(this@ChatActivityFragment, Observer { sessions ->
                sessions?.let {
                    (rvChatMessages.adapter as? ChatListAdapter)?.setSessions(sessions)

                    val sessionStatus = sessions.status

                    if (sessionStatus == "closed") {
                        etSendMessage.isEnabled = false

//                        btSend.isClickable=false
//                        ibCamera.isClickable = false
//                        ibAdd.isClickable=false
//                        btEndChat.isClickable=false
//                        ibAttach.isClickable=false

                        btSend.isEnabled = false
                        ibCamera.isEnabled = false
                        ibAdd.isEnabled = false
//                        btEndChat.isEnabled = false
                        btEndChat.isClickable = false
//                        ibAttach.isEnabled = false

                        val color = Color.parseColor("#D3D3D3")
//                        rvChatMessages.setBackgroundColor(color)

//                        etSendMessage.setBackgroundColor(color)
//                        ibCamera.setBackgroundColor(color)
//                        ibAdd.setBackgroundColor(color)
//                        btEndChat.setBackgroundColor(color)
//                        ibAttach.setBackgroundColor(color)
                        frag_chat.setBackgroundColor(color)
                        btEndChat.background = null
                        etSendMessage.setText("Chat Ended")
                        etSendMessage.setTextColor(Color.RED)
                        etSendMessage.setGravity(Gravity.CENTER_HORIZONTAL)
//                        etSendMessage.setBackgroundColor(Color.WHITE)


//                        btSend.visibility = View.GONE
                        etSendMessage.typeface = Typeface.DEFAULT_BOLD

//                        btSend.setBackgroundColor(Color.rgb(255, 255, 255))
//                        ivMessageImage?.setOnClickListener(null)

//                        clAdaptiveCard.setBackgroundColor(color)

//                        ivMessageImage?.isEnabled = false
//                        tvPdfMessage?.isEnabled = false
//                        imgThumbnail?.isEnabled = false

//                        ivMessageImage?.isClickable = false
//                        tvPdfMessage?.isClickable = false
//                        imgThumbnail?.isClickable = false

//                        ivMessageImage?.setOnClickListener {
//                            return@setOnClickListener
//                        }

                    } else {
                        btEndChat.setBackgroundColor(Color.WHITE)
                        btEndChat.background = null
                        ibCamera.setBackgroundColor(Color.WHITE)
                        ibCamera.background = null
                        ibAdd.setBackgroundColor(Color.WHITE)
                        ibAdd.background = null
                    }
                }
            })
        }
    }

    private fun observeSessionParticipants() = GlobalScope.launch {
        // Updates list when favorites change

//      val sessionId="5c99ce01b662766ce5e9a68e"
        val sessionsData = chatMessagesViewModel.getSessionParticipants(sessionId)
        withContext(Dispatchers.Main) {
            sessionsData.observe(this@ChatActivityFragment, Observer { sessions ->
                sessions?.let {
                    (rvChatMessages.adapter as? ChatListAdapter)?.setSessionParticipants(sessions)
                }
            })
        }
    }

    private fun observeMessages() = GlobalScope.launch {
        // Updates list when favorites change

//      val sessionId="5c99ce01b662766ce5e9a68e"
        val messagesData = chatMessagesViewModel.getMessages(sessionId)
        withContext(Dispatchers.Main) {
            messagesData.observe(this@ChatActivityFragment, Observer { messages ->
                messages?.let {
                    val chatListAdapter = rvChatMessages.adapter as? ChatListAdapter
                    chatListAdapter?.let {
                        println("item count before setting messages ${it.itemCount}")
                        it.setMessages(messages)
                        //(rvChatMessages.adapter as? ChatListAdapter)?.notifyItemInserted(it.size)
                        //rvChatMessages.scrollToPosition(it.size)
                        println("item count after setting messages ${it.itemCount}")
                        rvChatMessages.smoothScrollToPosition(it.itemCount - 1)
                    }
                }
            })
        }
    }

    // code for uploading attachment to AWS S3 Bucket

    private fun attachFile() {
        showPictureDialog()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action :")
//        pictureDialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.YELLOW))
        val pictureDialogItems =
            arrayOf(
                "Select Image from Gallery",
                "Select Video from Gallery",
                "Select Pdf File from Gallery"
            )
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> chooseImageFromGallery()
                1 -> chooseVideoFromGallery()
                2 -> choosePdfFromGallery()
            }
        }
        pictureDialog.show()
    }

    private fun chooseImageFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) run {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 10
            )
        }
        else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY)
        }
    }

    private fun chooseVideoFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) run {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 10
            )
        }
        else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY)
        }
    }

    private fun choosePdfFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) run {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 10
            )
        }
        else {
            val intent = Intent()
                .setType("application/pdf")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a File"), 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == GALLERY) {
            fileName = data.data!!.lastPathSegment!!
            val contentURI = data.data!!
            try {
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val filePathColumn1 = arrayOf(MediaStore.Video.Media.DATA)

                val cursor = activity!!.applicationContext.contentResolver.query(
                    contentURI,
                    filePathColumn, null, null, null
                )
                val cursor1 = activity!!.applicationContext.contentResolver.query(
                    contentURI,
                    filePathColumn1, null, null, null
                )
                cursor!!.moveToFirst()
                cursor1!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imagePath = cursor.getString(columnIndex)
                cursor.close()
                val columnIndex1 = cursor1.getColumnIndex(filePathColumn1[0])
                videoPath = cursor1.getString(columnIndex1)
                cursor1.close()
//                    etSendMessage.setText(picturePath)
//                    etSendMessage.setText(picturePath1)
//                     etSendMessage.setText(fileName)

                if (imagePath.contains(".png") || imagePath.contains(".jpg") || videoPath.contains(".mp4")) {
                    uploadToS3()
                }
//                else{
//                    Toast.makeText(context,"File format not supported.",Toast.LENGTH_LONG).show()
//                }

//                if (imagePath.contains(".png")) {
//                    uploadToS3()
////                      etSendMessage.setText(picturePath)
////                        val a=".png"
////                        etSendMessage.setText(fileName+a)
//
////                    etSendMessage.setText(fileName)
////                    etSendMessage.isEnabled = false
//
////                    println("PNG Image Path-$imagePath")
////                    println("PNG Image FileName-$fileName")
//                }
//                if (imagePath.contains(".jpg")) {
//                    uploadToS3()
////                      etSendMessage.setText(picturePath)
////                        val a=".jpg"
////                        etSendMessage.setText(fileName+a)
//
////                    etSendMessage.setText(fileName)
////                    etSendMessage.isEnabled = false
//
////                        println("JPG Image Path-$picturePath")
////                        println("JPG Image FileName-$fileName")
////                        img_video_upload_to_S3()
//                }
//                if (videoPath.contains(".mp4")) {
//                    uploadToS3()
////                      etSendMessage.setText(picturePath1)
////                        val a=".mp4"
////                        etSendMessage.setText(fileName+a)
//
////                    etSendMessage.setText(fileName)
////                    etSendMessage.isEnabled = false
//
////                    println("MP4 Video Path-$videoPath")
////                    println("MP4 Video FileName-$fileName")
////                        img_video_upload_to_S3()
//                }

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Error in Picking Image/Video from Gallery.",
                    Toast.LENGTH_LONG
                ).show()
            }
//            uploadToS3()

//            etSendMessage.text?.clear()
//            etSendMessage.isEnabled = true
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            val selectedFile = data.data!!.lastPathSegment!!
            pdfFullPath = data.data!!.path!!
            val regex = """(.+)/(.+)\.(.+)""".toRegex()
            val matchResult = regex.matchEntire(selectedFile)

            if (matchResult != null) {
                val (directory, fileName, extension) = matchResult.destructured
//                println("dir: $directory | fileName: $fileName | extension: $extension")
                pdfFileName = "$fileName.$extension"
//              etSendMessage.setText(selectedFile)
//              etSendMessage.setText(pdfFileName)

//                println("pdfFilePath: $pdfFullPath")
//                println("pdfFileName: $pdfFileName")
//                println("pdfSelectedFile: $selectedFile")

//                etSendMessage.setText(pdfFullPath)
////                etSendMessage.setText(selectedFile)
//                etSendMessage.isEnabled = false
            }

            if (isOnline()) {

//                etSendMessage.setText(selectedFile)
//                etSendMessage.isEnabled = false

                if (selectedFile.isEmpty()) {
                    Toast.makeText(context, "Please select a PDF File.", Toast.LENGTH_LONG).show()
                    return
                }
                if (selectedFile.trim().isNotEmpty()) {
                    if (selectedFile.contains(".pdf")) {

                        val uploadToS3 = File(selectedFile)
                        try {
                            val transferObserver = transferUtility.upload(
                                bucket,
                                pdfFileName,
                                uploadToS3
                            )
                            transferObserverListener(transferObserver)

                            val a = "https://s3-us-west-1.amazonaws.com/supportgeniemedia/"
                            val b = pdfFileName
//                          val msg = a.plus(b)

                            msgPdf = a.plus(b)


                            val uniqueID = UUID.randomUUID().toString()
                            println("uniqueID--->$uniqueID")

//                            Fuel.post(
//                                "/message/send", listOf(
//                                    "id" to uniqueID,
//                                    "message" to msgPdf,
//                                    "sender" to "5c99cdfd830f78365845f193",
//                                    "senderType" to "customer",
//                                    "sessionId" to "5c99ce01b662766ce5e9a68e",
//                                    "mimeType" to "application/pdf"
//                                )
//                            ).response { _, _, _ ->
//                                Toast.makeText(
//                                    context,
//                                    "Please open the App again to see changes.",
//                                    Toast.LENGTH_LONG
//                                )
//                                    .show()
//                            }

//                            if (msgPdf.isNotEmpty()) {
//                                println("send pdf file $msgPdf for session $sessionId company $companyId")
//                                this.context?.let {
//                                    sendPdfFile(msgPdf, sessionId, companyId, it)
//                                }
//                            }
                            sendPdfFile()
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error in Uploading PDF File.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

//                etSendMessage.text?.clear()
//                etSendMessage.isEnabled = true

            } else {
                Toast.makeText(context, "You are not connected to Internet.", Toast.LENGTH_LONG)
                    .show()
                AlertDialog.Builder(context).setTitle("No Internet Connection.")
                    .setMessage("Please check your internet connection and try again later!")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        //                        finish()
//                        startActivity(intent)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert).show()
            }
        }
    }

    private fun uploadToS3() {
        if (isOnline()) {
            val uploadToS3 = File(imagePath)
            val uploadToS31 = File(videoPath)
            if (imagePath.isEmpty() || videoPath.isEmpty()) {
                return@uploadToS3
            }
            if (imagePath.trim().isNotEmpty() || videoPath.trim().isNotEmpty()) {
                if (imagePath.contains(".png")) {
                    try {
                        val transferObserver = transferUtility.upload(
                            bucket,
                            fileName,
                            uploadToS3
                        )
                        transferObserverListener(transferObserver)
                        val a = "https://s3-us-west-1.amazonaws.com/supportgeniemedia/"
                        val b = fileName
//                        val msg = a.plus(b)

                        msgPng = a.plus(b)
                        val uniqueID = UUID.randomUUID().toString()
                        println("uniqueID--->$uniqueID")

//                        Fuel.post(
//                            "/message/send", listOf(
//                                "id" to uniqueID,
//                                "message" to msgPng,
//                                "sender" to "5c99cdfd830f78365845f193",
//                                "senderType" to "customer",
//                                "sessionId" to "5c99ce01b662766ce5e9a68e",
//                                "mimeType" to "image/png"
//                            )
//                        ).response { _, _, _ ->
//                            Toast.makeText(
//                                context,
//                                "Please open the App again to see changes.",
//                                Toast.LENGTH_LONG
//                            )
//                                .show()
//                        }

//                        if (msgPng.isNotEmpty()) {
//                            println("send png image $msgPng for session $sessionId company $companyId")
//                            this.context?.let {
//                                sendPngImage(msgPng, sessionId, companyId, it)
//                            }
//                        }
                        sendPngImage()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error in Uploading PNG Image.", Toast.LENGTH_LONG)
                            .show()
                    }
                } else if (imagePath.contains(".jpg")) {
                    try {
                        val transferObserver = transferUtility.upload(
                            bucket,
                            fileName,
                            uploadToS3
                        )
                        transferObserverListener(transferObserver)

                        val a = "https://s3-us-west-1.amazonaws.com/supportgeniemedia/"
                        val b = fileName
//                        val msg = a.plus(b)

                        msgJpg = a.plus(b)

                        val uniqueID = UUID.randomUUID().toString()
                        println("uniqueID--->$uniqueID")

//                        Fuel.post(
//                            "/message/send", listOf(
//                                "id" to uniqueID,
//                                "message" to msgJpg,
//                                "sender" to "5c99cdfd830f78365845f193",
//                                "senderType" to "customer",
//                                "sessionId" to "5c99ce01b662766ce5e9a68e",
//                                "mimeType" to "image/jpg"
//                            )
//                        ).response { _, _, _ ->
//                            Toast.makeText(
//                                context,
//                                "Please open the App again to see changes.",
//                                Toast.LENGTH_LONG
//                            )
//                                .show()
//                        }

//                        if (msgJpg.isNotEmpty()) {
//                            println("send jpg image $msgJpg for session $sessionId company $companyId")
//                            this.context?.let {
//                                sendJpgImage(msgJpg, sessionId, companyId, it)
//                            }
//                        }
                        sendJpgImage()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error in Uploading JPG Image.", Toast.LENGTH_LONG)
                            .show()
                    }
                } else if (videoPath.contains(".mp4")) {
                    try {
                        val transferObserver = transferUtility.upload(
                            bucket,
                            fileName,
                            uploadToS31
                        )
                        transferObserverListener(transferObserver)
                        val a = "https://s3-us-west-1.amazonaws.com/supportgeniemedia/"
                        val b = fileName
//                        val msg = a.plus(b)

                        msgMp4 = a.plus(b)

                        val uniqueID = UUID.randomUUID().toString()
                        println("uniqueID--->$uniqueID")

//                        Fuel.post(
//                            "/message/send", listOf(
//                                "id" to uniqueID,
//                                "message" to msgMp4,
//                                "sender" to "5c99cdfd830f78365845f193",
//                                "senderType" to "customer",
//                                "sessionId" to "5c99ce01b662766ce5e9a68e",
//                                "mimeType" to "video/mp4"
//                            )
//                        ).response { _, _, _ ->
//                            Toast.makeText(
//                                context,
//                                "Please open the App again to see changes.",
//                                Toast.LENGTH_LONG
//                            )
//                                .show()
//                        }

//                        if (msgMp4.isNotEmpty()) {
//                            println("send mp4 video $msgMp4 for session $sessionId company $companyId")
//                            this.context?.let {
//                                sendMp4Video(msgMp4, sessionId, companyId, it)
//                            }
//                        }
                        sendMp4Video()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error in Uploading MP4 Video.", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Please select : jpg/png/mp4 Files only.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@uploadToS3
                }
            }
//            etSendMessage.text?.clear()
//            etSendMessage.isEnabled = true
        } else {
            Toast.makeText(context, "You are not connected to Internet.", Toast.LENGTH_LONG).show()
            AlertDialog.Builder(context).setTitle("No Internet Connection.")
                .setMessage("Please check your internet connection and try again later!")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    //                    finish()
//                    startActivity(intent)
                }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }
    }

    private fun transferObserverListener(transferObserver: TransferObserver) {
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                Toast.makeText(context, "Upload Status: $state", Toast.LENGTH_LONG).show()
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentage = (bytesCurrent / bytesTotal * 100).toInt()
                Toast.makeText(context, "Upload Percentage : $percentage %", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onError(id: Int, ex: Exception) {
                Log.e("Error", "Error")
            }
        })
    }

    private fun s3credentialsProvider() {
        val cognitoCachingCredentialsProvider: CognitoCachingCredentialsProvider =
            CognitoCachingCredentialsProvider(
                context,
                "us-east-1:fdcd1af0-7e51-49fc-b23b-79139735c1f2",
                Regions.US_EAST_1
            )
        createAmazonS3Client(cognitoCachingCredentialsProvider)
    }

    private fun createAmazonS3Client(credentialsProvider: CognitoCachingCredentialsProvider) {
        s3Client = AmazonS3Client(credentialsProvider)
        s3Client.setRegion(Region.getRegion(Regions.US_EAST_1))
    }

    private fun setTransferUtility() {
        transferUtility = TransferUtility(s3Client, context)
    }

    @SuppressLint("MissingPermission")
    private fun isOnline(): Boolean {
        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}
