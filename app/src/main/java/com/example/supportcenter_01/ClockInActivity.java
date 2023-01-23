package com.example.supportcenter_01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.supportcenter_01.databinding.ActivityClockInBinding;

public class ClockInActivity extends AppCompatActivity {
    private ActivityClockInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClockInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}