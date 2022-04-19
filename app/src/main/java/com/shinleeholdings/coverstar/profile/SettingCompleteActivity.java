package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;

import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.databinding.ActivitySettingCompleteBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;
import com.shinleeholdings.coverstar.util.BaseActivity;

public class SettingCompleteActivity extends BaseActivity {
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
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        });
    }

    @Override
    public void onBackPressed() {
        BackClickEventHandler.onBackPressed(this);
    }
}