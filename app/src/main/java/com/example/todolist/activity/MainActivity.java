package com.example.todolist.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.example.todolist.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AppCompatButton addButton;
    RecyclerView taskRecyclerView;
    TaskAdapter adapter;
    List<Task> taskList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        anhXa();



        if(isConnected(this)){
            setClick();
            getAllTasks();

        }
        else{
            Toast.makeText(getApplicationContext(),"Không có Internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }

    }

    private void getAllTasks() {

        db.collection("tasks")
                .whereEqualTo("userId", Utils.U_ID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskList = new ArrayList<>(); // Tạo mới danh sách

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Task taskItem = doc.toObject(Task.class);
                            taskItem.setId(doc.getId());
                            taskList.add(taskItem);
                        }

                        // Sau khi có dữ liệu mới khởi tạo Adapter
                        initAdapter();
                    } else {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void initAdapter() {
        // Kiểm tra nếu danh sách không null
        if (taskList != null) {
            adapter = new TaskAdapter(taskList, this);
            taskRecyclerView.setAdapter(adapter);
        } else {
            Log.e("TaskListActivity", "Danh sách task là null");
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);


            if (networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
                return true;
            }
        }
        return false;
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
    private void setAdapter(List<Task> tasks) {
        adapter = new TaskAdapter(tasks,getApplicationContext());
        taskRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void anhXa(){
        addButton = findViewById(R.id.addButton);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}