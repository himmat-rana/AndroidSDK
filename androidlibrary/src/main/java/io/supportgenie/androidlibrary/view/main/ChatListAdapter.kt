package io.supportgenie.androidlibrary.view.main

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import io.supportgenie.androidlibrary.R
import io.supportgenie.androidlibrary.model.*
//import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.rv_chat_text_item.*
import com.bumptech.glide.*
import io.supportgenie.androidlibrary.utils.*
import io.supportgenie.androidlibrary.utils.formatDateTime
import kotlinx.android.synthetic.main.rv_chat_text_item.ivSenderPhoto
import kotlinx.android.synthetic.main.rv_chat_text_item.tvMessageTime
import kotlinx.android.synthetic.main.rv_chat_text_item.tvSenderName
import io.adaptivecards.renderer.RenderedAdaptiveCard;
import io.adaptivecards.renderer.actionhandler.ICardActionHandler;
import androidx.fragment.app.FragmentManager
import io.supportgenie.androidlibrary.view.adaptivecard.*
import io.adaptivecards.objectmodel.BaseActionElement
import io.adaptivecards.objectmodel.BaseCardElement
import io.adaptivecards.objectmodel.HostConfig
import kotlinx.android.synthetic.main.rv_chat_adaptive_card_item.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import kotlinx.android.synthetic.main.rv_chat_doc_item.*
import kotlinx.android.synthetic.main.rv_chat_image_item.*
import kotlinx.android.synthetic.main.rv_chat_video_item.*
import io.supportgenie.androidlibrary.view.main.LayoutContainer1
import kotlinx.android.synthetic.main.rv_chat_adaptive_card_item.view.*
import kotlinx.android.synthetic.main.rv_chat_doc_item.view.*
import kotlinx.android.synthetic.main.rv_chat_image_item.view.*
import kotlinx.android.synthetic.main.rv_chat_text_item.view.*
import kotlinx.android.synthetic.main.rv_chat_text_item.view.ivSenderPhoto
import kotlinx.android.synthetic.main.rv_chat_text_item.view.tvMessageTime
import kotlinx.android.synthetic.main.rv_chat_text_item.view.tvSenderName
import kotlinx.android.synthetic.main.rv_chat_video_item.view.*

const val s3Prefix: String = "https://s3-us-west-1.amazonaws.com/supportgeniemedia/"

fun getSGStoreUrl(url: String): String {
    return if (url != "") {
        if (url.startsWith(("http"))) {
            url
        } else {
            "${s3Prefix}${url}"
        }
    } else {
        url
    }
}

private val strHostConfig = """
{
	"preExpandSingleShowCardAction": true,
	"actions": {
		"actionsOrientation": "vertical",
		"actionAlignment": "center"
	},
	"fontTypes": {
		"default": {
			"fontFamily": "'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', 'sans-serif'",
			"fontSizes": {
				"small": 10,
				"default": 12,
				"medium": 12,
				"large": 14,
				"extraLarge": 16
			},
			"fontWeights": {
				"lighter": 200,
				"default": 400,
				"bolder": 600
			}
		}
	}
}
""".trimIndent()


// multiple items types code inspired from
// https://medium.com/@ivancse.58/android-and-kotlin-recyclerview-with-multiple-view-types-65285a254393
class ChatListAdapter(
    private var agents: List<Agent>,
    private var sessions: List<Session>,
    private var sessionParticipant: List<SessionParticipant>,
    private var companies: List<Company>,
    private var messages: List<ChatMessage>,
    private val fragmentManager: FragmentManager?
) : RecyclerView.Adapter<ChatListAdapter.BaseViewHolder>() {

    companion object {
        private const val TEXT_ITEM = 0
        private const val IMAGE_ITEM = 1
        private const val VIDEO_ITEM = 2
        private const val DOC_ITEM = 3
        private const val ADAPTIVE_CARD_ITEM = 4
//        lateinit var progressBar: ProgressBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TEXT_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_chat_text_item, parent, false)
                ChatTextViewHolder(view)
            }
            IMAGE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_chat_image_item, parent, false)
                ChatImageViewHolder(view)
            }
            VIDEO_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_chat_video_item, parent, false)
                ChatVideoViewHolder(view)
            }
            DOC_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_chat_doc_item, parent, false)
                ChatDocViewHolder(view)
            }
            ADAPTIVE_CARD_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_chat_adaptive_card_item, parent, false)
                ChatAdaptiveCardViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type") as Throwable
        }
    }

    override fun getItemCount(): Int {
        println("getItemCount returning ${messages.size}")
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val mimeType = message.mimeType
        return if (isMimeTypeText(mimeType))
            TEXT_ITEM
        else if (isMimeTypeImage(mimeType))
            IMAGE_ITEM
        else if (isMimeTypeVideo(mimeType))
            VIDEO_ITEM
        else if (isMimeTypeAdaptiveCard(mimeType))
            ADAPTIVE_CARD_ITEM
        else if (isMimeTypeDocument(mimeType))
            DOC_ITEM
        else
            throw IllegalArgumentException("Invalid type of data " + position)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val message = messages[position]
        holder.bindTo(message)  // Binds data to a list item
        println("Check Error Not Displaying All Data = $message")
    }

    abstract inner class BaseViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer1 {
        open fun bindTo(chatMessage: ChatMessage) {
            var photoUrl: String? = null
            val isMe = chatMessage.senderType == "customer"
            if (isMe) {
                itemView.tvSenderName.text = "Me"
            } else {
                var agentFound = false
                agents.forEach {
                    if (it.userId == chatMessage.sender) {
                        agentFound = true
                        println("found agent name ${it.name}, id ${it.userId} for message ${chatMessage.message} photoUrl ${it.photoUrl}")
                        itemView.tvSenderName.text = it.name
                        if ((it.photoUrl != null) && (it.photoUrl != "")) {
                            photoUrl = getSGStoreUrl(it.photoUrl)
                        }
                    }
                }
                if (!agentFound) {
                    println("not found agent for sender ${chatMessage.sender} senderType ${chatMessage.senderType} message ${chatMessage.message} ")
                    itemView.tvSenderName.text = "Agent"
                }
            }
            println("displaying message ${chatMessage.message}")
            itemView.tvMessageTime.text =
                formatDateTime(chatMessage.localMessageTime, containerView.context)

            if (photoUrl != null) {
                println("photoUrl is ${photoUrl}")
                Glide.with(containerView)
                    .asBitmap()
                    .load(photoUrl)
                    .into(itemView.ivSenderPhoto)
            }
        }
    }

    inner class ChatImageViewHolder(override val containerView: View) : BaseViewHolder(containerView) {
        override fun bindTo(chatMessage: ChatMessage) {
            super.bindTo(chatMessage)
            val photoUrl = getSGStoreUrl(chatMessage.message)
            println("photoUrl inside ChatImageViewHolder : $photoUrl")

            val circularProgressDrawable = CircularProgressDrawable(containerView.context)
            circularProgressDrawable.strokeWidth = 7f
            circularProgressDrawable.centerRadius = 50f
            circularProgressDrawable.setColorFilter((Color.rgb(255, 0, 0)), PorterDuff.Mode.SRC_OVER)

//            circularProgressDrawable.progressRotation=0.5f
            circularProgressDrawable.start()

//            progressBar = containerView.findViewById<View>(R.id.progressBarImageView) as ProgressBar

            if (photoUrl != "") {
                Glide.with(containerView)
                    .asBitmap()
                    .load(photoUrl)
                    .placeholder(circularProgressDrawable)
                    .error(R.mipmap.image_not_found)

//                    .thumbnail(0.1f)
                    .into(itemView.ivMessageImage)

            } else {
                Glide.with(containerView)
                    .asBitmap()
                    .load(R.mipmap.image_not_found)
                    .placeholder(circularProgressDrawable)
                    .into(itemView.ivMessageImage)
            }

            itemView.ivMessageImage.setOnClickListener {
                val intent = Intent(containerView.context, ImageZoomActivity::class.java)
                intent.putExtra("message", photoUrl)
                containerView.context.startActivity(intent)
            }
//                Glide.with(containerView)
//                    .load(photoUrl)
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            progressBar.visibility = View.GONE
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Drawable?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            dataSource: DataSource?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            progressBar.visibility = View.GONE
//                            return false
//                        }
//
//                    })
//                    .into(ivMessageImage)

        }
    }

    inner class ChatVideoViewHolder(override val containerView: View) : BaseViewHolder(containerView) {
        override fun bindTo(chatMessage: ChatMessage) {
            super.bindTo(chatMessage)
            val videoUrl = getSGStoreUrl(chatMessage.message)
            println("videoUrl inside ChatVideoViewHolder : $videoUrl")

            val circularProgressDrawable = CircularProgressDrawable(containerView.context)
            circularProgressDrawable.strokeWidth = 7f
            circularProgressDrawable.centerRadius = 50f
            circularProgressDrawable.setColorFilter((Color.rgb(255, 0, 0)), PorterDuff.Mode.SRC_OVER)
            circularProgressDrawable.start()

//            val thumb = ThumbnailUtils.createVideoThumbnail(
//                videoUrl,
//                MediaStore.Images.Thumbnails.MINI_KIND
//            )
//            val bitmapDrawable = BitmapDrawable(containerView.context.resources,thumb)
//            imgThumbnail.background = bitmapDrawable

            if (videoUrl != "") {
                itemView.imgMiniThumbnail.visibility=View.VISIBLE
                Glide.with(containerView)
                    .asBitmap()
                    .load(videoUrl)
//                    .load(listOf(videoUrl, R.drawable.play_icon))
                    .placeholder(circularProgressDrawable)
                    .error(R.mipmap.video_not_found)
//                    .apply(RequestOptions.circleCropTransform())

//                   .thumbnail(0.1f)
                    .into(itemView.imgThumbnail)

//                Glide.with(containerView)
//                    .asBitmap()
//                    .load(R.drawable.play_icon)
//
//                    .placeholder(circularProgressDrawable)
//                    .error(R.mipmap.video_not_found)
//
////                   .thumbnail(0.1f)
//                    .into(imgThumbnail)
            } else {
                Glide.with(containerView)
                    .asBitmap()
                    .load(R.mipmap.video_not_found)
                    .placeholder(circularProgressDrawable)
                    .into(itemView.imgThumbnail)
            }

            itemView.imgMiniThumbnail.setOnClickListener {
                //                println("videoUrl inside Click Listener")
                val intent = Intent(containerView.context, VideoActivity::class.java)
//                val intent = Intent(containerView.context, VideoViewActivity::class.java)
                intent.putExtra("message", videoUrl)
                containerView.context.startActivity(intent)
            }
        }
    }

    inner class ChatDocViewHolder(override val containerView: View) : BaseViewHolder(containerView) {
        override fun bindTo(chatMessage: ChatMessage) {
            super.bindTo(chatMessage)
            val pdfUrl = getSGStoreUrl(chatMessage.message)
            println("pdfUrl inside ChatDocViewHolder : $pdfUrl")
            if (pdfUrl != "") {
                itemView.tvPdfMessage.text = pdfUrl
            }
            itemView.tvPdfMessage.setOnClickListener {
                val intent = Intent(containerView.context, PdfViewerActivity::class.java)
                intent.putExtra("message", pdfUrl)
                containerView.context.startActivity(intent)
            }
        }
    }

    inner class ChatAdaptiveCardViewHolder(override val containerView: View) : BaseViewHolder(containerView) {
        inner class CardActionHandler(private val sessionId: String) : ICardActionHandler {
            override fun onAction(var1: BaseActionElement, var2: RenderedAdaptiveCard) {

            }

            override fun onMediaPlay(var1: BaseCardElement, var2: RenderedAdaptiveCard) {

            }

            override fun onMediaStop(var1: BaseCardElement, var2: RenderedAdaptiveCard) {

            }
        }

        //private val hostConfig: HostConfig = HostConfig.DeserializeFromString("{\"preExpandSingleShowCardAction\": true, \"actions\": {\"actionsOrientation\": \"vertical\",\"actionAlignment\": \"center\"}}")
        private val hostConfig: HostConfig = HostConfig.DeserializeFromString(strHostConfig)
        private val adaptiveCards: MutableMap<String, RecyclerViewContent?> =
            mutableMapOf<String, RecyclerViewContent?>()

        private fun getRecyclerViewContent(sessionId: String, cardData: String): RecyclerViewContent? {
            val adaptiveCard: Card = Card(cardData)
            adaptiveCard.parsedCard?.let { parsedResult ->
                val cardActionHandler = CardActionHandler(sessionId)
                fragmentManager?.let {
                    val recyclerViewContent: RecyclerViewContent = RecyclerViewContent(
                        parsedResult, containerView.context, it,
                        cardActionHandler, hostConfig
                    ) {
                        // on finished rendering
                        renderCard(it)
                    }
                    return recyclerViewContent
                }
            }
            return null
        }

        private fun renderCard(recyclerViewContent: RecyclerViewContent) {
            recyclerViewContent.renderedCard?.let {
                //llAdaptiveCard.removeAllViewsInLayout()
                //llAdaptiveCard.addView(it)
                val oldView = containerView.findViewWithTag<View>("adaptiveCard")
                oldView?.let {
                    itemView.clAdaptiveCard.removeView(it)
                }
                val set = ConstraintSet()
                it.setTag("adaptiveCard")
                it.setId(View.generateViewId())
                itemView.clAdaptiveCard.addView(it, 0)
                set.clone(itemView.clAdaptiveCard)
                set.connect(it.getId(), ConstraintSet.BOTTOM, itemView.clAdaptiveCard.id, ConstraintSet.BOTTOM, 0)
                set.applyTo(itemView.clAdaptiveCard)
            }
        }

        override fun bindTo(chatMessage: ChatMessage) {
            super.bindTo(chatMessage)
            val sessionId = chatMessage.sessionId
            if (!adaptiveCards.contains(sessionId)) {
                adaptiveCards.put(sessionId, getRecyclerViewContent(sessionId, chatMessage.message))
            } else {
                adaptiveCards.get(sessionId)?.let {
                    renderCard(it)
                }
            }
            /*
            val renderedCard = AdaptiveCardRenderer.getInstance()
                .render(m_context, m_fragmentManager, m_showCardAction.GetCard(), m_cardActionHandler, m_hostConfig)
                */
            //Do your view assignment here from the data model
        }
    }

    inner class ChatTextViewHolder(override val containerView: View) : BaseViewHolder(containerView) {
        override fun bindTo(chatMessage: ChatMessage) {  // Populates text views and star image to show a food
            super.bindTo(chatMessage)
            itemView.tvMessage.text = chatMessage.message
        }
    }

    // In this app, we'll usually replace all items so DiffUtil has little use
    fun setSessions(newSessions: Session) {
//        this.sessions = newSessions   // Replaces whole list
        notifyDataSetChanged()  // Notifies recycler view of data changes to re-render
    }

    // In this app, we'll usually replace all items so DiffUtil has little use
    fun setAgentsForCompany(newAgents: List<Agent>) {
        this.agents = newAgents   // Replaces whole list
        notifyDataSetChanged()  // Notifies recycler view of data changes to re-render
    }

    // In this app, we'll usually replace all items so DiffUtil has little use
    fun setSessionParticipants(newSessionParticipant: List<SessionParticipant>) {
        this.sessionParticipant = newSessionParticipant   // Replaces whole list
        notifyDataSetChanged()  // Notifies recycler view of data changes to re-render
    }

    // In this app, we'll usually replace all items so DiffUtil has little use
    fun setMessages(newMessages: List<ChatMessage>) {
        this.messages = newMessages   // Replaces whole list
        notifyDataSetChanged()  // Notifies recycler view of data changes to re-render
    }
}