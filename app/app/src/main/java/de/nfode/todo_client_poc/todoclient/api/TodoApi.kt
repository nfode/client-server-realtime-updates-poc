package de.nfode.todo_client_poc.todoclient.api

import de.nfode.todo_client_poc.todoclient.model.TodosResponse
import de.nfode.todo_client_poc.todoclient.model.Todo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface TodoApi {
    @GET("/todos/")
    fun getTodos(): Call<TodosResponse>

    @POST("/update/{id}")
    fun toogleChecked(@Path("id") id: Int): Call<Todo>
}