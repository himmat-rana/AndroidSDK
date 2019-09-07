package io.supportgenie.androidlibrary.data.network.pubsub

import android.util.Log
import com.laurencegarmstrong.kwamp.client.core.Client
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.wss
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText

import com.laurencegarmstrong.kwamp.client.core.ClientImpl
import com.laurencegarmstrong.kwamp.core.InvalidMessageException
import com.laurencegarmstrong.kwamp.core.Uri
import com.laurencegarmstrong.kwamp.core.WAMP_JSON
import com.laurencegarmstrong.kwamp.core.WAMP_MSG_PACK
import com.laurencegarmstrong.kwamp.core.messages.Dict

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import io.ktor.client.HttpClient
import kotlinx.coroutines.*


class PubSub() {
    private var wampClient: Client? = null
    private var isConnected = false
    private lateinit var host: String
    private val listeners = mutableMapOf<String, MutableList<((kwArgs: Dict?) -> Unit)>>()

    companion object {
        private var INSTANCE: PubSub? = null

        fun getPubsub(): PubSub {      // Builds and caches DB object
            if (INSTANCE == null) {
                INSTANCE = PubSub()
            }

            return INSTANCE!!
        }
    }

    public fun registerListener(topic: String, onEvent: (eventData: Dict?) -> Unit) {
        if (listeners.containsKey(topic)) {
            println("registerListener found topic $topic check isConnected $isConnected")
            val onEventList = listeners.get(topic)
            onEventList?.contains(onEvent)?.let { found ->
                if (!found) {
                    println("registerListener found topic $topic not found on onEventList")
                    onEventList.add(onEvent)
                    if (isConnected) {
                        println("registerListener subscribe to topic $topic")
                        subscribe(topic) {
                            println("onEvent  for topic $topic")
                            onEvent(it)
                        }
                    }
                }
            }
        } else {
            val onEventList = mutableListOf<((eventData: Dict?) -> Unit)>()
            onEventList.add(onEvent)
            listeners.put(topic, onEventList)
            println("registerListener subscribe to topic $topic check isConnected $isConnected")
            if (isConnected) {
                println("registerListener subscribe to topic $topic")
                subscribe(topic) {
                    println("onEvent  for topic $topic")
                    onEvent(it)
                }
            }
        }
    }

    public fun connect(hostVal: String) {
        println("pubsub inside connect to host $hostVal")
        host = hostVal
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    println("pubsub inside connect createWebsocketWampClient")
                    wampClient = createWebsocketWampClient()
                    println("pubsub inside connect createWebsocketWampClient returned")
                    startSubscribers()
                    Log.d("test", "pubsub startSubscribers done")
                } catch (t: Throwable) {
                    println("pubsub exception ${t.message}")
                }
            }
        }
    }

    private fun subscribe(topic: String, onEvent: (eventData: Dict?) -> Unit) {
        //println("pubsub subscribe before for topic $topic")
        wampClient?.let {
            println("pubsub subscribe for topic $topic")
            it.subscribe(Uri(topic)) { arguments, argumentsKw ->
                println("pubsub subscribe onEvent for topic $topic")
                onEvent(argumentsKw)
            }
        }
    }

    private suspend fun createWebsocketWampClient(): Client {
        val wampIncoming = Channel<ByteArray>()
        val wampOutgoing = Channel<ByteArray>()
        println("pubsub establishWebsocketConnection")
        establishWebsocketConnection(wampIncoming, wampOutgoing)
        println("pubsub establishWebsocketConnection done")
        return ClientImpl(wampIncoming, wampOutgoing, Uri("realm1"), protocol = WAMP_JSON)
    }

    private fun startSubscribers() {
        println("inside startSubscribers")
        for ((topic, onEvents) in listeners) {
            println("inside startSubscribers got topic $topic")
            for (onEvent in onEvents) {
                println("subscribing to topic $topic")
                subscribe(topic) {
                    println("got event for topic $topic")
                    onEvent(it)
                }
            }
        }
    }

    private fun establishWebsocketConnection(
        wampIncoming: Channel<ByteArray>,
        wampOutgoing: Channel<ByteArray>,
        protocol: String = WAMP_JSON
    ) {
        runBlocking {
            GlobalScope.launch {
                val client = websocketClient()
                Log.d("test", "pubsub created websocketClient")
                client.wss(host = host, port = 443, path = "/ws") {
                    Log.d("test", "pubsub created client.wss")
                    isConnected = true
                    //ClientImpl(wampIncoming, wampOutgoing, Uri("realm1"), protocol = WAMP_JSON)
                    GlobalScope.launch {
                        wampOutgoing.consumeEach { message ->
                            //Log.d("test", "send back outgoing")
                            send(Frame.Text(message.toString(Charsets.UTF_8)))
                        }
                    }

                    incoming.consumeEach { frame ->
                        Log.d("test", "got incoming")
                        if (frame is Frame.Text && protocol == WAMP_JSON) {
                            Log.d("test", "got json")
                            val byteArray = frame.readText().toByteArray()
                            try {
                                if (byteArray != null) {
                                    wampIncoming.send(byteArray)
                                }
                            } catch (e: InvalidMessageException) {
                                Log.d("test", "got exception")
                            }
                            Log.d("test", "got json done")
                        } else if (frame is Frame.Binary && protocol == WAMP_MSG_PACK) {
                            Log.d("test", "got binary")
                            wampIncoming.send(frame.buffer.array())
                        }
                    }
                }
                println("pubsub created websocketClient done")
            }
        }
    }

    private fun websocketClient() = HttpClient(CIO) {
        install(WebSockets) {
        }
    }
}
