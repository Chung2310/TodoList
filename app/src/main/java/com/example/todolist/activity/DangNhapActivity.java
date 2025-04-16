package com.example.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todolist.R;
import com.example.todolist.model.User;
import com.example.todolist.model.UserModel;
import com.example.todolist.retrofit.Api;
import com.example.todolist.retrofit.RetrofitClient;
import com.example.todolist.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {

    TextView txtdangki,txtResetPass;
    TextInputEditText txtemail,txtpassword;
    AppCompatButton btnDangNhap;
    Api api;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhXa();
        initcontrol();
        txtemail.setText("");
        txtpassword.setText("");
    }
    private void initcontrol() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DangKiActivity.class);
                startActivity(intent);
            }
        });
        txtResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(),ResetPassActivity.class);
                //startActivity(intent);
            }
        });
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = txtemail.getText().toString().trim();
                String str_pass = txtpassword.getText().toString().trim();
                if(TextUtils.isEmpty(str_email)){
                    Toast.makeText(getApplicationContext(),"Hãy nhập email của bạn",Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(str_pass)) {
                    Toast.makeText(getApplicationContext(),"Hãy nhập mật khẩu của bạn",Toast.LENGTH_LONG).show();
                } else {

                    Paper.book().write("email",str_email);
                    Paper.book().write("pass",str_pass);
                    dangNhap(str_email,str_pass);

                }
            }
        });
    }

    private void anhXa() {
        Paper.init(this);
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        txtdangki =findViewById(R.id.txtdangki);
        txtemail = findViewById(R.id.txtemail);
        txtpassword = findViewById(R.id.password);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        txtResetPass = findViewById(R.id.resetPass);

        if(Paper.book().read("email") != null && Paper.book().read("pass") != null){
            txtemail.setText(Paper.book().read("email"));
            txtpassword.setText(Paper.book().read("pass"));
            if(Paper.book().read("islogin")!= null){
                boolean flag = Paper.book().read("islogin");

                if(flag){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dangNhap(Paper.book().read("email"), Paper.book().read("pass"));
                        }
                    },1000);
                }
            }
        }
    }

    private void dangNhap(String str_email, String str_pass) {
        compositeDisposable.add(api.dangNhap(str_email,str_pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                isLogin = true;
                                Paper.book().write("islogin",isLogin);
                                Paper.book().write("user",userModel.getResult());
                                Utils.user_current = (User) userModel.getResult();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),userModel.getMessage(),Toast.LENGTH_LONG).show();
                            }

                        },  throwable -> {
                            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.user_current.getEmail() != null && Utils.user_current.getPass() !=null){
            txtemail.setText(Utils.user_current.getEmail());
            txtpassword.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}