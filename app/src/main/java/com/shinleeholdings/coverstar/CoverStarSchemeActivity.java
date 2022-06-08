package com.shinleeholdings.coverstar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shinleeholdings.coverstar.chatting.ChatActivity;
import com.shinleeholdings.coverstar.service.MessagingService;

public class CoverStarSchemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            handleUri(uri);
        } else {
            Intent intent;
            String pushType = getIntent().getStringExtra(AppConstants.EXTRA.PUSH_TYPE);
            String pushKey = getIntent().getStringExtra(AppConstants.EXTRA.PUSH_KEY);

            if (isTaskRoot()) {
                // 외부에서 들어옴
                // TODO 푸시 test
                intent = new Intent(this, SplashActivity.class);
                intent.putExtra(AppConstants.EXTRA.PUSH_KEY, pushKey);
                intent.putExtra(AppConstants.EXTRA.PUSH_TYPE, pushType);
            } else {
                // 내부에서 들어옴
                if (MessagingService.PUSHTYPE_CHAT_TEXT.equals(pushType) || MessagingService.PUSHTYPE_CHAT_FILE.equals(pushType)) {
                    // TODO 푸시 test
                    intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(AppConstants.EXTRA.CHAT_ID, pushKey);
                } else {
                    intent = new Intent(this, SplashActivity.class);
                }
            }
            startActivity(intent);
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