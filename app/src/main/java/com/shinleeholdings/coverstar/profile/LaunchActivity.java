package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityOpeningBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;

public class LaunchActivity extends AppCompatActivity {

    private ActivityOpeningBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpeningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(view -> {
            boolean hasLoginInfo = false; // TODO 로그인 정보 보유 유무 체크
            Intent phoneCertIntent = new Intent(this, PhoneCertActivity.class);
            if (hasLoginInfo) {
                phoneCertIntent.putExtra(AppConstants.EXTRA.PHONE_CERT_MODE, PhoneCertActivity.PHONE_CERT_MODE_LOGIN);
            } else {
                phoneCertIntent.putExtra(AppConstants.EXTRA.PHONE_CERT_MODE, PhoneCertActivity.PHONE_CERT_MODE_RECERT);
            }
            startActivity(phoneCertIntent);
        });

        binding.joinButton.setOnClickListener(view -> startActivity(new Intent(LaunchActivity.this, RulesAgreeActivity.class)));
    }

    @Override
    public void onBackPressed() {
        BackClickEventHandler.onBackPressed(this);
    }
}