package com.example.todolist.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.model.Task;
import com.example.todolist.model.User;
import com.example.todolist.retrofit.Api;
import com.example.todolist.retrofit.RetrofitClient;
import com.example.todolist.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    AppCompatButton addButton;
    RecyclerView taskRecyclerView;
    TaskAdapter adapter;
    List<Task> tasks = new ArrayList<>();
    Api api;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        anhXa();
        getListJob();
        setClick();
    }

    private void getListJob() {
        compositeDisposable.add(api.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        JobModel -> {
                            if (JobModel.isSuccess()) {
                                if (JobModel.getResult() != null) {
                                    tasks = JobModel.getResult();
                                    setAdapter();
                                } else {
                                    tasks = new ArrayList<>();
                                    setAdapter();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), JobModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Failed to load tasks", Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void setClick() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        adapter = new TaskAdapter(tasks,getApplicationContext());
        taskRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void anhXa(){
        addButton = findViewById(R.id.addButton);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();

    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    setAdapter();
                }

    }
}