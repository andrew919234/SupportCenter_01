package com.example.supportcenter_01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.supportcenter_01.databinding.ActivityOptionBinding;

public class OptionActivity extends AppCompatActivity {
    private ActivityOptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}