package com.shinleeholdings.coverstar.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shinleeholdings.coverstar.databinding.ActivitySettingCompleteBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;

public class SettingCompleteActivity extends AppCompatActivity {
    private ActivitySettingCompleteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();
    }

    private void initUi() {
        binding.nextButton.setOnClickListener(view -> {
            // TODO 다음처리 필요
        });
    }

    @Override
    public void onBackPressed() {
        BackClickEventHandler.onBackPressed(this);
    }
}