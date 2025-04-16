package com.example.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.R;
import com.example.todolist.retrofit.Api;
import com.example.todolist.retrofit.RetrofitClient;
import com.example.todolist.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {

    TextInputEditText txtemailDK,txtpassDK,txtpassxacthuc,txtusername;
    AppCompatButton btnDangKi;
    Api api;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ki);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();
        initControl();
    }
    private void initControl() {
        btnDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKi();
            }
        });
    }


    private void dangKi() {
        String str_email = txtemailDK.getText().toString().trim();
        String str_pass = txtpassDK.getText().toString().trim();
        String str_repass = txtpassxacthuc.getText().toString().trim();
        String str_user = txtusername.getText().toString().trim();

        if (TextUtils.isEmpty(str_email)) {
            Toast.makeText(getApplicationContext(), "Hãy nhập email của bạn", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_pass)) {
            Toast.makeText(getApplicationContext(), "Hãy nhập mật khẩu của bạn", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_repass)) {
            Toast.makeText(getApplicationContext(), "Hãy xác thực lại mật khẩu của bạn", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_user)) {
            Toast.makeText(getApplicationContext(), "Hãy nhập họ và tên người dùng", Toast.LENGTH_LONG).show();
        } else {
            if (str_pass.equals(str_repass)) {

                compositeDisposable.add(api.dangKi(str_email, str_pass, str_user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                messageModel -> {
                                    if (messageModel.isSuccess()) {
                                        Utils.user_current.setEmail(str_email);
                                        Utils.user_current.setPass(str_pass);

                                        Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                },
                                throwable -> {
                                    Log.d("loidongki",throwable.getMessage());
                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        ));

            } else {
                Toast.makeText(getApplicationContext(), "Mật khẩu của bạn chưa trùng khớp", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void anhXa() {
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        txtemailDK = findViewById(R.id.txtemailDK);
        txtpassDK = findViewById(R.id.txtpassDK);
        txtpassxacthuc = findViewById(R.id.txtpassxacnhan);
        btnDangKi =findViewById(R.id.btnDangKy);
        txtusername = findViewById(R.id.txtusername);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}