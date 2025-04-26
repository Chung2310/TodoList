package com.example.todolist.retrofit;

import com.example.todolist.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Api {
    @POST("/api/tasks")
    Call<Task> createTask(@Header("Authorization") String token, @Body Task task);

    @GET("/api/tasks")
    Call<List<Task>> getTasks(@Header("Authorization") String token);

    @PUT("/api/tasks/{id}")
    Call<Task> updateTask(@Header("Authorization") String token, @Path("id") long id, @Body Task task);

    @DELETE("/api/tasks/{id}")
    Call<Void> deleteTask(@Header("Authorization") String token, @Path("id") long id);
}
