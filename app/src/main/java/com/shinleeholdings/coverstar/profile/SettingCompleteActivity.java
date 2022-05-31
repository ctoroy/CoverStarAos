package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.databinding.ActivitySettingCompleteBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.LoginHelper;

import network.model.CoverStarUser;

public class SettingCompleteActivity extends BaseActivity {
    private ActivitySettingCompleteBinding binding;

    private CoverStarUser loginUserData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginUserData = getIntent().getParcelableExtra(AppConstants.EXTRA.USER_DATA);

        initUi();
    }

    private void initUi() {
        binding.nextButton.setOnClickListener(view -> {
            login();
        });
    }

    private void login() {
        LoginHelper.getSingleInstance().login(this, loginUserData.userId, loginUserData.userPwd, false, new LoginHelper.ILoginResultListener() {
            @Override
            public void onComplete(boolean success) {
                if (success) {
                    Intent mainIntent = new Intent(SettingCompleteActivity.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        BackClickEventHandler.onBackPressed(this);
    }
}