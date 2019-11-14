// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package io.supportgenie.androidlibrary.view.adaptivecard

import android.content.Context
import android.os.AsyncTask
import android.view.View
import androidx.fragment.app.FragmentManager

import io.adaptivecards.objectmodel.HostConfig
import io.adaptivecards.objectmodel.ParseResult
import io.adaptivecards.renderer.AdaptiveCardRenderer
import io.adaptivecards.renderer.actionhandler.ICardActionHandler

class RecyclerViewContent {
    var renderedCard: View? = null
        private set
    private var m_cardHasRendered: Boolean = false
    private var m_cardRendererTask: CardRendererTask? = null
    private var m_onFinishedRendering: ((recyclerViewContent: RecyclerViewContent) -> Unit)? = null

    private inner class CardRendererTask(
            private val m_context: Context,
            private val m_fragmentManager: FragmentManager,
            private val m_cardActionHandler: ICardActionHandler,
            private val m_hostConfig: HostConfig
    ) : AsyncTask<ParseResult, Void, View>() {
        override fun doInBackground(vararg objects: ParseResult): View? {
            try {
                val renderedCard = AdaptiveCardRenderer.getInstance().render(
                        m_context,
                        m_fragmentManager,
                        objects[0].GetAdaptiveCard(),
                        m_cardActionHandler,
                        m_hostConfig
                )
                return renderedCard.view
            } catch (e: Exception) {
                println(e.message)
                e.printStackTrace()
                return null
            }

        }

        override fun onPostExecute(view: View) {
            renderedCard = view
            m_cardHasRendered = true

            m_onFinishedRendering?.let {
                it(this@RecyclerViewContent)
            }
        }
    }


    constructor(
            parseResult: ParseResult,
            context: Context,
            fragmentManager: FragmentManager,
            cardActionHandler: ICardActionHandler,
            hostConfig: HostConfig,
            onFinishedRendering: ((recyclerViewContent: RecyclerViewContent) -> Unit)?
    ) {
        m_onFinishedRendering = onFinishedRendering
        m_cardHasRendered = false
        renderedCard = null

        m_cardRendererTask = CardRendererTask(context, fragmentManager, cardActionHandler, hostConfig)
        m_cardRendererTask!!.execute(parseResult)
    }

    constructor(view: View) {
        renderedCard = view
        m_cardHasRendered = true
    }

    fun hasRenderedCard(): Boolean {
        return m_cardHasRendered
    }

    /*
    fun registerCardRenderedListener(cardRenderedListener: RecyclerViewAdapter.ICardRenderedListener) {
        m_cardRendererTask?.registerCardRenderedListener(cardRenderedListener)
    }

    fun unregisterCardRenderedListener() {
        m_cardRendererTask?.unregisterCardRenderedListener()
    }
    */
}
