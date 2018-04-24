package de.nfode.todo_client_poc.todoclient


class Todo(
        val id: Int,
        val text: String,
        var checked: Boolean = false
)
