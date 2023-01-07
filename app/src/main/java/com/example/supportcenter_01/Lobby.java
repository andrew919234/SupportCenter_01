package com.example.supportcenter_01;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.supportcenter_01.databinding.ActivityLobbyBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Lobby extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLobbyBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLobbyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getInstance().signOut();
                startActivity(new Intent(Lobby.this,Login.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }
}