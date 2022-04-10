package com.shinleeholdings.coverstar.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shinleeholdings.coverstar.databinding.ActivityOpeningBinding;

public class UserPasswordActivity extends AppCompatActivity {

    private ActivityOpeningBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpeningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}