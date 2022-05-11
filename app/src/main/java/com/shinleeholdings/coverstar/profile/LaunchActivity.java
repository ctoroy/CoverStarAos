package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.databinding.ActivityOpeningBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.LoginHelper;

public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.shinleeholdings.coverstar.databinding.ActivityOpeningBinding binding = ActivityOpeningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(view -> {
            Intent phoneCertIntent = new Intent(this, PhoneCertActivity.class);
            if (LoginHelper.getSingleInstance().getSavedLoginUserData() != null) {
                phoneCertIntent.putExtra(AppConstants.EXTRA.PHONE_CERT_MODE, LoginHelper.PHONE_CERT_MODE_LOGIN);
            } else {
                phoneCertIntent.putExtra(AppConstants.EXTRA.PHONE_CERT_MODE, LoginHelper.PHONE_CERT_MODE_RECERT);
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