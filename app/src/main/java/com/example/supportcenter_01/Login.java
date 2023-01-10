package com.example.supportcenter_01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
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
    //viewmodel
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setOnClick();
//        hide();
    }

    private void setOnClick() {
        binding.btLoginSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailaddress = binding.etLoginEmailaddress.getText().toString().trim();
                String password = binding.etLoginPassword.getText().toString().trim();
//                signin_mail_passwd();
                if (TextUtils.isEmpty(emailaddress) && TextUtils.isEmpty(password)) {
                    binding.emailLayout.setError("請輸入電子信箱");
                    binding.passwordLayout.setError("請輸入密碼");
                } else if (TextUtils.isEmpty(emailaddress)) {
                    binding.emailLayout.setError("請輸入電子信箱");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    binding.passwordLayout.setError("請輸入密碼");
                    return;
                } else {
                    switch (myViewModel.checkUser(emailaddress, password)) {
                        case -1:
                            binding.tvLoginInfo.setText("登入失敗：帳號或密碼不可空白");
                            break;
                        case -2:
                            binding.tvLoginInfo.setText("登入失敗：帳號或密碼錯誤");
                            break;
                        case 1:
                            startActivity(new Intent(Login.this, Lobby.class));
                            break;
                    }

                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        //viewmodel
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getAllUsers().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                //update RecyclerView
//                Toast.makeText(Login.this, "onchange", Toast.LENGTH_SHORT).show();
            }
        });
        return super.onCreateView(name, context, attrs);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (myViewModel.checkUserOnline()) {
            startActivity(new Intent(Login.this, Lobby.class));
        }
    }

    public void onResume() {
        super.onResume();
        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);

            }
        };
        if(haveInternet()){
            binding.btLoginSign.setEnabled(true);
            Toast.makeText(this, "網路連線中", Toast.LENGTH_SHORT).show();

        }else {
            binding.btLoginSign.setEnabled(false);
            binding.tvLoginInfo.setText("網路未連線");
        }
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

    //網路連線檢查
    private boolean haveInternet() {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            result = false;
        } else {
            if (!info.isAvailable()) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
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
