package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.supportcenter_01.RoomDataBase.User;
import com.example.supportcenter_01.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    //viewmodel
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //viewmodel
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        myViewModel.getAllUsers().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                //update RecyclerView
                Toast.makeText(Login.this, "onchange", Toast.LENGTH_SHORT).show();
            }
        });
//
        auth = FirebaseAuth.getInstance();
        setOnClick();
        hide();
    }

    private void setOnClick() {
        binding.btLoginSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailaddress  = binding.etLoginEmailaddress.getText().toString().trim();
                String password  =  binding.etLoginPassword.getText().toString().trim();
//                signin_mail_passwd();
                myViewModel.checkUser(emailaddress,password);
            }
        });
    }

//    private void signin_mail_passwd() {
//        if (!binding.etLoginEmailaddress.getText().toString().isEmpty() && !binding.etLoginPassword.getText().toString().isEmpty()) {
//           String emailaddress  = binding.etLoginEmailaddress.getText().toString().trim();
//           String password  =  binding.etLoginPassword.getText().toString().trim();
//            auth.signInWithEmailAndPassword(emailaddress,password)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                FirebaseUser f_user = auth.getCurrentUser();
//                                binding.tvLoginInfo.setText(f_user.getEmail() + "登入成功");
//                                myViewModel.insert(emailaddress,password);
//                                startActivity(new Intent(Login.this, Lobby.class));
//                            } else {
//                                binding.tvLoginInfo.setText("登入失敗：帳號或密碼錯誤");
//                            }
//                        }
//                    });
//        } else {
//            binding.tvLoginInfo.setText("帳號或密碼不可空白");
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        if (myViewModel.checkUserOnline()) {
            startActivity(new Intent(Login.this, Lobby.class));
        }
    }

    public void onResume() {
        super.onResume();
        restorePrefs();
    }

    private void restorePrefs() {
        SharedPreferences settings = getSharedPreferences("PREF", 0);
        String pref_email = settings.getString("PREF_EMAIL", "");
        String pref_password = settings.getString("PREF_PASSWORD", "");
        if (!"".equals("PREF")) {
            binding.etLoginEmailaddress.setText(pref_email);
            binding.etLoginPassword.setText(pref_password);
            binding.etLoginEmailaddress.requestFocus();
        }
    }

    public void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences("PREF", 0);
        settings.edit().putString("PREF_EMAIL", binding.etLoginEmailaddress.getText().toString()).apply();
        settings.edit().putString("PREF_PASSWORD", binding.etLoginPassword.getText().toString()).apply();

    }

    private void hide() {
        ActionBar bar = getSupportActionBar();
        bar.hide();
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        // Hide both the status bar and the navigation bar
//        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }
}
