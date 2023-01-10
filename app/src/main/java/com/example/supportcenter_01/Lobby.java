package com.example.supportcenter_01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supportcenter_01.RoomDataBase.User;
import com.example.supportcenter_01.databinding.ActivityLobbyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Lobby extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLobbyBinding binding;
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLobbyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.lobbyClockInOut.setOnClickListener(v -> {
            if (haveInternet()) {

            } else {
                Toast.makeText(this, "網路未連線", Toast.LENGTH_SHORT).show();
            }
        });

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewModel.signOutUserOnline();
                finish();
            }
        });
        Resources res = getResources();
        int[] icon = {R.drawable.baseline_work_outline_24,
                R.drawable.baseline_flight_takeoff_24,
                R.drawable.baseline_calendar_month_24,
                R.drawable.baseline_date_range_24,
                R.drawable.baseline_work_24,
                R.drawable.baseline_fact_check_24};
        String[] strings = res.getStringArray(R.array.option_string);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.lobbyRvbt.setLayoutManager(llm);
//        binding.lobbyRvbt.setLayoutManager(new GridLayoutManager(this,3));
        MyAdapter adapterF = new MyAdapter(icon, strings);
        binding.lobbyRvbt.setAdapter(adapterF);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getAllUsers().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Toast.makeText(Lobby.this, "onChange", Toast.LENGTH_SHORT).show();
            }
        });
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mConnReceiver);
    }

    //廣播監聽是否有網路
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 當使用者開啟或關閉網路時會進入這邊

            // 判斷目前有無網路
            if (haveInternet()) {
                // 以連線至網路，做更新資料等事情
                binding.lobbyClockInOut.setEnabled(true);
                Toast.makeText(Lobby.this, "有網路!", Toast.LENGTH_SHORT).show();
            } else {
                // 沒有網路
                binding.lobbyClockInOut.setEnabled(false);
                Toast.makeText(Lobby.this, "沒網路!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //網路連線檢查
    private boolean haveInternet() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return info != null &&
                info.isConnected();
    }
}