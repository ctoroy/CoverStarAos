package com.shinleeholdings.coverstar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.shinleeholdings.coverstar.databinding.ActivitySplashBinding;
import com.shinleeholdings.coverstar.profile.LaunchActivity;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivitySplashBinding.inflate(getLayoutInflater()).getRoot());

        if (isTaskRoot() == false) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.PUSH_ID, task.getResult());
            }

            new Handler().postDelayed(this::startLogin, 1000);
        });
    }

    private void startLogin() {
        String loginId = SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LOGIN_ID);
        String loginPw = SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LOGIN_PW);
        if (TextUtils.isEmpty(loginId) || TextUtils.isEmpty(loginPw)) {
            // TODO test
            startMainActivity();
//            startOpeningActivity();
            return;
        }

        LoginHelper.getSingleInstance().login(this, loginId, loginPw, new LoginHelper.ILoginResultListener() {
            @Override
            public void onComplete(boolean success) {
                if (success) {
                    startMainActivity();
                } else {
                    startOpeningActivity();
                }
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fadein_anim, R.anim.fadeout_anim);
    }

    private void startOpeningActivity() {
        startActivity(new Intent(this, LaunchActivity.class));
        finish();
        overridePendingTransition(R.anim.fadein_anim, R.anim.fadeout_anim);
    }
}