package com.shinleeholdings.coverstar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.shinleeholdings.coverstar.databinding.ActivityMainBinding;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;

public class SplashActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get new FCM registration token
                String token = task.getResult();
                DebugLogger.i("token : " + token);

                // TODO 토큰 저장
                SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.PUSH_ID, token);
            }

            // 다음 프로세스 진행
        });
    }
}