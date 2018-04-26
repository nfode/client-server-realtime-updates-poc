package de.nfode.todo_client_poc.todoclient.api

import com.centrifugal.centrifuge.android.Centrifugo
import com.centrifugal.centrifuge.android.credentials.Token
import com.centrifugal.centrifuge.android.credentials.User
import com.centrifugal.centrifuge.android.listener.ConnectionListener
import com.centrifugal.centrifuge.android.listener.SubscriptionListener
import com.centrifugal.centrifuge.android.subscription.SubscriptionRequest
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import de.nfode.todo_client_poc.todoclient.MainActivity
import de.nfode.todo_client_poc.todoclient.model.Todo
import de.nfode.todo_client_poc.todoclient.util.Signing
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CentrifugoClient(address: String) {
    private var centrifugo: Centrifugo

    init {
        val centrifugoAddress = "ws://192.168.178.88:8000/connection/websocket"
        val userId = UUID.randomUUID().toString()
        val userToken = null //nullable
        val tokenTimestamp = System.currentTimeMillis().toString()
        println(tokenTimestamp)
        val token = Signing().generateConnectionToken(userId, tokenTimestamp, "")
        centrifugo = Centrifugo.Builder(centrifugoAddress)
                .setUser(User(userId, userToken))
                .setToken(Token(token, tokenTimestamp))
                .build()
        val channel = "public:update"
        centrifugo.subscribe(SubscriptionRequest(channel))
        setUpConnectionListener()
        setUpSubscriptionListener()
    }

    private fun setUpConnectionListener() {
        centrifugo.setConnectionListener(object : ConnectionListener {
            override fun onWebSocketOpen() {
            }

            override fun onConnected() {
                println("Connected to Centrifugo!")
            }

            override fun onDisconnected(code: Int, reason: String, remote: Boolean) {
                println("Disconnected from Centrifugo.")
            }
        })
    }

    public fun connect(): CentrifugoClient {
        centrifugo.connect()
        return this
    }

    private fun setUpSubscriptionListener() {
        centrifugo.setSubscriptionListener(object : SubscriptionListener {
            override fun onSubscribed(channelName: String) {
                println("Just subscribed to $channelName")
            }

            override fun onUnsubscribed(channelName: String) {
                println("Unsubscribed from $channelName")
            }

            override fun onSubscriptionError(channelName: String, error: String) {
                error("Failed to subscribe to $channelName, cause: $error")
            }

        })
    }

    fun setUpOnMessageEvent(todos: ArrayList<Todo>, mainActivity: MainActivity) : CentrifugoClient{
        centrifugo.setDataMessageListener {
            val data: JSONObject = it.body.getJSONObject("data")
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<Todo> = moshi.adapter<Todo>(Todo::class.java)
            val res: Todo = jsonAdapter.fromJson(data.toString())
            val match: Todo = todos.filter { todo -> todo.id == res.id }[0]
            synchronized(todos) {
                todos[todos.indexOf(match)].checked = res.checked
            }
            mainActivity.updateList()
        }
        return this
    }

}