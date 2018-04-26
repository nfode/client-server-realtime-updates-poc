package de.nfode.todo_client_poc.todoclient

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import de.nfode.todo_client_poc.todoclient.api.TodoApi
import de.nfode.todo_client_poc.todoclient.model.Todo
import retrofit2.Call

class TodoListViewAdapter(private val activity: Activity, todos: List<Todo>, restApi: TodoApi?) : BaseAdapter() {

    private var todos = ArrayList<Todo>()
    private var restApi: TodoApi?

    init {
        this.todos = todos as ArrayList
        this.restApi = restApi

    }

    override fun getCount(): Int {
        return todos.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var view: View
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.list_view, null)
        val text = view.findViewById<TextView>(R.id.title)
        val checkbox = view.findViewById<CheckBox>(R.id.checkBox)
        val todo: Todo = todos[i]
        text.text = todo.text
        checkbox.isChecked = todo.checked
        checkbox.setOnClickListener {
            var call: Call<Todo>? = restApi?.toogleChecked(todo.id)
            Thread { call?.execute() }.start()
        }
        return view
    }
}