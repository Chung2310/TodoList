package com.example.todolist.retrofit;

import com.example.todolist.model.JobModel;
import com.example.todolist.model.MessageModel;
import com.example.todolist.model.UserModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("dangnhap.php")
    Observable<UserModel> dangNhap(
            @Query("email") String email,
            @Query("pass") String pass
    );

    @GET("dangki.php")
    Observable<MessageModel> dangKi(
            @Query("email") String email,
            @Query("pass") String pass,
            @Query("name") String name
    );
    @GET("getall.php")
    Observable<JobModel> getAll();
    @GET("addtask.php")
    Observable<MessageModel> addTask(
            @Query("id_job") String id_job,
            @Query("task") String task,
            @Query("day") String day,
            @Query("hours") String hours
    );
    @GET("xoatask.php")
    Observable<MessageModel> deleteTask(
            @Query("id_job") String id_job
    );
}
