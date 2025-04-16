package com.example.todolist.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.retrofit.Api;
import com.example.todolist.retrofit.RetrofitClient;
import com.example.todolist.utils.Utils;

import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddTaskActivity extends AppCompatActivity {

    TextView tvDay,tvHours;
    EditText taskInput;
    AppCompatButton btnPickDate, btnPickHours,btnAddToList;

    private String selectedDate = "";
    private String selectedTime = "";
    Api api;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        anhXa();
        setEvents();
    }

    private void setEvents() {
        btnPickDate.setOnClickListener(v -> {
            // Lấy ngày hiện tại
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Hiển thị hộp thoại chọn ngày
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddTaskActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        tvDay.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        btnPickHours.setOnClickListener(v -> {
            // Lấy thời gian hiện tại
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Tạo TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AddTaskActivity.this,
                    (view, hourOfDay, minuteOfHour) -> {
                        selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                        tvHours.setText(selectedTime);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task taskModel = new Task();
                taskModel.setDay(selectedDate);
                taskModel.setHours(selectedTime);
                taskModel.setTask(taskInput.getText().toString());
                String id = selectedDate.replace("/", "") + selectedTime.replace(":", "");
                taskModel.setId(id);
                addTask(taskModel);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void anhXa() {
        taskInput = findViewById(R.id.taskInput);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickHours = findViewById(R.id.btnPickHours);
        tvDay = findViewById(R.id.tvDay);
        tvHours = findViewById(R.id.tvHours);
        btnAddToList = findViewById(R.id.btnAddToList);
    }
    private void addTask(Task task)
    {
        compositeDisposable.add(api.addTask(task.getId(),task.getTask(),task.getDay(),task.getHours())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()){
                                Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }, throwable -> {
                            Log.d("loi_add",throwable.getMessage());
                            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }
}