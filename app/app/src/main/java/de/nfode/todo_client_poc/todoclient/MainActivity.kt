package de.nfode.todo_client_poc.todoclient

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.centrifugal.centrifuge.android.Centrifugo
import com.centrifugal.centrifuge.android.credentials.Token
import com.centrifugal.centrifuge.android.credentials.User
import com.centrifugal.centrifuge.android.listener.ConnectionListener
import com.centrifugal.centrifuge.android.listener.SubscriptionListener
import com.centrifugal.centrifuge.android.subscription.SubscriptionRequest
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var todoApi: TodoApi
    var listView: ListView? = null
    var todos = ArrayList<Todo>()
    var adapter: TodoListViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpRestApi()
        val centrifugoAddress = "ws://192.168.178.88:8000/connection/websocket"
        val userId = UUID.randomUUID().toString()
        val userToken = null //nullable
        val tokenTimestamp = System.currentTimeMillis().toString()
        println(tokenTimestamp)
        val token = Signing().generateConnectionToken(userId, tokenTimestamp, "")
        val centrifugo = Centrifugo.Builder(centrifugoAddress)
                .setUser(User(userId, userToken))
                .setToken(Token(token, tokenTimestamp))
                .build()
        val channel = "public:update"
        centrifugo.subscribe(SubscriptionRequest(channel))
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
        centrifugo.setDataMessageListener {
            val data: JSONObject = it.body.getJSONObject("data")
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<Todo> = moshi.adapter<Todo>(Todo::class.java)
            val res: Todo = jsonAdapter.fromJson(data.toString())
            val match: Todo = todos.filter { todo -> todo.id == res.id }[0]
            synchronized(todos) {
                todos[todos.indexOf(match)].checked = res.checked
            }
            runOnUiThread {
                adapter?.notifyDataSetChanged()
            }

        }
        centrifugo.connect()
    }

    fun setUpRestApi() {
        val retrofit = Retrofit.Builder().baseUrl("http://192.168.178.88:8080")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        todoApi = retrofit.create(TodoApi::class.java)
        listView = findViewById(R.id.listView) as ListView

        adapter = TodoListViewAdapter(this, todos, todoApi)
        (listView as ListView).adapter = adapter

        AsyncCall().execute()

    }


    inner class AsyncCall : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            val call: Call<TodosResponse> = todoApi.getTodos()
            var res: Response<TodosResponse>? = null
            res = call.execute()
            val body = res.body()
            for (todo in body?.data!!) {
                println(todo.text)
                todos.add(todo)
            }
            runOnUiThread {
                adapter?.notifyDataSetChanged()
            }
            return res?.body().toString()
        }

    }

}
