package com.example.todolist.activity;

import static com.example.todolist.utils.Utils.BASE_URL;

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
import com.example.todolist.retrofit.Api;
import com.example.todolist.retrofit.RetrofitClient;
import com.example.todolist.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    AppCompatButton addButton;
    RecyclerView taskRecyclerView;
    TaskAdapter adapter;
    List<Task> tasks = new ArrayList<>();

    Retrofit retrofit = RetrofitClient.getInstance(BASE_URL);
    Api api = retrofit.create(Api.class);;

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
        api = RetrofitClient.getInstance(BASE_URL).create(Api.class);


        getAllTasks();
        anhXa();
        setClick();
    }

    private void getAllTasks() {
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = "Bearer " + task.getResult().getToken();

                        Call<List<Task>> call = api.getTasks(token);
                        call.enqueue(new Callback<List<Task>>() {
                            @Override
                            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    tasks.clear(); // Xóa dữ liệu cũ
                                    tasks.addAll(response.body()); // Thêm dữ liệu mới
                                    setAdapter(); // Cập nhật RecyclerView
                                } else {
                                    Toast.makeText(MainActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Task>> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("MainActivity", "Lỗi: ", t);
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Lỗi lấy token", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setClick() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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