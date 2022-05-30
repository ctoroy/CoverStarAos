package com.shinleeholdings.coverstar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shinleeholdings.coverstar.databinding.ActivitySplashBinding;
import com.shinleeholdings.coverstar.profile.LaunchActivity;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;
import com.shinleeholdings.coverstar.util.Util;

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

            checkAppUpdate();
        });
    }

    private void checkAppUpdate() {
        DebugLogger.i("appUpdateInfo checkMarketStoreVersion");
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnCompleteListener(new OnCompleteListener<AppUpdateInfo>() {
            @Override
            public void onComplete(@NonNull Task<AppUpdateInfo> task) {
                DebugLogger.i("appUpdateInfo onCompleted isComplete : " + task.isComplete() + ", isSuccessful : " + task.isSuccessful());
                try {
                    if (task.isComplete() && task.isSuccessful()) {
                        PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                        int currentVersionCode = pInfo.versionCode;

                        AppUpdateInfo info = task.getResult();
                        DebugLogger.i("appUpdateInfo availableVersionCode : " + info.availableVersionCode() + ", updateAvailability : " + info.updateAvailability());
                        if (info.availableVersionCode() > currentVersionCode || info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                            Util.goStore(SplashActivity.this);
                            return;
                        }
                    }
                } catch (Exception e) {
                }
                new Handler().postDelayed(() -> startAutoLogin(), 1000);
            }
        });
    }

    private void startAutoLogin() {
        LoginHelper.getSingleInstance().startAutoLogin(this, success -> {
            if (success) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, LaunchActivity.class));
            }
            finish();
            overridePendingTransition(R.anim.fadein_anim, R.anim.fadeout_anim);
        });
    }
}