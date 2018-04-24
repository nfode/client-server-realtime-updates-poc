package de.nfode.todo_client_poc.todoclient

import org.json.JSONObject
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