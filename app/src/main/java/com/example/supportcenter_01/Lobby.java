package com.example.supportcenter_01;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

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

        setSupportActionBar(binding.toolbar);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getAllUsers().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Toast.makeText(Lobby.this, "onChange", Toast.LENGTH_SHORT).show();
            }
        });
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
        LinearLayoutManager llm =  new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.lobbyRvbt.setLayoutManager(llm);
//        binding.lobbyRvbt.setLayoutManager(new GridLayoutManager(this,3));
        MyAdapter adapterF = new MyAdapter(icon, strings);
        binding.lobbyRvbt.setAdapter(adapterF);
    }

}