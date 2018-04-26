package de.nfode.todo_client_poc.todoclient.model


class Todo(
        val id: Int,
        val text: String,
        var checked: Boolean = false
)
