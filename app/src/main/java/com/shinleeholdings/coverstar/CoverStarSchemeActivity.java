package com.shinleeholdings.coverstar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CoverStarSchemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            handleUri(uri);
        }

        finish();
    }

    private void handleUri(Uri data) {
        if (isTaskRoot()) {
            // 외부에서 들어옴
            startActivity(new Intent(this, SplashActivity.class));
        } else {
            // 내부에서 들어옴
        }
    }
}