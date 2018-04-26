package de.nfode.todo_client_poc.todoclient

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import de.nfode.todo_client_poc.todoclient.api.CentrifugoClient
import de.nfode.todo_client_poc.todoclient.api.TodoApi
import de.nfode.todo_client_poc.todoclient.model.Todo
import de.nfode.todo_client_poc.todoclient.model.TodosResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


class MainActivity : AppCompatActivity() {

    var todoApi: TodoApi? = null
    var listView: ListView? = null
    var todos = ArrayList<Todo>()
    var adapter: TodoListViewAdapter? = null
    val properties = Properties()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getProperties()
        setUpRestApi()
        listView = findViewById(R.id.listView)
        adapter = TodoListViewAdapter(this, todos, todoApi)
        (listView as ListView).adapter = adapter

        val centrifugoAddress = properties.getProperty("centrifugoWs")
        CentrifugoClient(centrifugoAddress)
                .setUpOnMessageEvent(todos, this).connect()
    }

    private fun setUpRestApi() {
        val apiUrl = properties.getProperty("apiUrl")
        val retrofit = Retrofit.Builder().baseUrl(apiUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        todoApi = retrofit.create(TodoApi::class.java)
        Thread {
            getTodos()
        }.start()
    }

    fun getProperties() {
        val assetManager = applicationContext.assets
        val inputStream = assetManager.open("application.properties")
        properties.load(inputStream)
    }

    private fun getTodos() {
        val call: Call<TodosResponse>? = todoApi?.getTodos()
        var res: Response<TodosResponse>?
        res = call?.execute()
        val body = res?.body()
        for (todo in body?.data!!) {
            println(todo.text)
            todos.add(todo)
        }
        updateList()
    }

    fun updateList() {
        runOnUiThread { adapter?.notifyDataSetChanged() }
    }

}
