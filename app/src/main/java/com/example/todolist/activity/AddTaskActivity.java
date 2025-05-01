package com.example.todolist.activity;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.R;
import com.example.todolist.model.Task;
import com.example.todolist.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {
    EditText edtTitle,edtDes;
    AppCompatButton btnPickDate,btnPickTime,btnAdd;
    TextView tvDate,tvTime;
    String time,date;
    private static final String STATUS_UNFINISHED = "unfinished";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        anhXa();
        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        tvDate.setText("Ngày đã chọn: " + date);

                    }, year, month, day);
            datePickerDialog.show();
        });

        btnPickTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        time = selectedHour + ":" + String.format("%02d", selectedMinute);
                        tvTime.setText("Giờ đã chọn: " + time);

                    }, hour, minute, true);
            timePickerDialog.show();
        });
        btnAdd.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            String description = edtDes.getText().toString().trim();

            if (title.isEmpty()) {
                edtTitle.setError(getString(R.string.title_required));
                return;
            }

            if (date == null || time == null) {
                Toast.makeText(this, "Vui lòng chọn ngày và giờ", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date dateTime = null;
            try {
                dateTime = sdf.parse(date+ " " +time);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Map<String, Object> task = new HashMap<>();
            String input = date+time;
            String result = input.replaceAll("[/:]", "");
            task.put("ID", result);
            task.put("title", title);
            task.put("description", description);
            task.put("status", STATUS_UNFINISHED);
            ;task.put("dueDateTime", new Timestamp(dateTime));
            task.put("userId", Utils.U_ID); // Thêm user ID

            db.collection("tasks")
                    .add(task)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Thêm công việc thành công", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng activity sau khi thêm thành công
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi thêm công việc: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Lỗi khi thêm document", e);
                    });
        });
    }
    private void anhXa() {
        edtTitle = findViewById(R.id.edtTitle);
        edtDes = findViewById(R.id.edtDes);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        btnAdd = findViewById(R.id.btnAdd);
    }
}