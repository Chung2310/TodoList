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
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.todolist.R;

import com.example.todolist.adapter.TaskAdapter;
import com.example.todolist.model.Task;
import com.example.todolist.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            setupTaskListener();
        }
        else{
            Toast.makeText(getApplicationContext(),"Không có Internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }

    }

    private void setupTaskListener() {
        db.collection("tasks")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status", "unfinished")
                .whereGreaterThanOrEqualTo("dueDateTime", com.google.firebase.Timestamp.now())
                .whereLessThanOrEqualTo("dueDateTime",
                        new com.google.firebase.Timestamp(
                                new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000) // 24h sau
                        )
                )
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed", error);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED ||
                                dc.getType() == DocumentChange.Type.MODIFIED) {

                            Task task = dc.getDocument().toObject(Task.class);
                            task.setID(dc.getDocument().getId());

                            if ("unfinished".equals(task.getStatus())) {
                                scheduleNotification(task);
                            }
                        }
                    }
                });
    }

    private void scheduleNotification(Task task) {
        try {
            // Lấy thời gian từ Timestamp
            long dueTimeMillis = task.getDueDateTime().toDate().getTime();
            long delay = dueTimeMillis - System.currentTimeMillis();

            if (delay > 0) {
                Data inputData = new Data.Builder()
                        .putString("taskId", task.getID())
                        .putString("title", task.getTitle())
                        .putString("description", task.getDescription())
                        .build();

                OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(
                        TaskNotificationWorker.class
                )
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .addTag("TASK_" + task.getID())
                        .build();

                WorkManager.getInstance(this).enqueueUniqueWork(
                        "NOTIFY_" + task.getID(),
                        ExistingWorkPolicy.REPLACE,
                        notificationWork
                );

                Log.d("Notification", "Đã lên lịch thông báo cho task: " + task.getID() +
                        " vào lúc " + task.getDueDateTime().toDate());
            }
        } catch (Exception e) {
            Log.e("Notification", "Lỗi khi lên lịch thông báo", e);
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
                            taskItem.setID(doc.getId());
                            taskList.add(taskItem);
                        }

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

    private void anhXa(){
        addButton = findViewById(R.id.addButton);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(isConnected(this)) {
            getAllTasks();
        }
    }
}