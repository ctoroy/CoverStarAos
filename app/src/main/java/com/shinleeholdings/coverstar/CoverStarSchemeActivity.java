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
            if (isTaskRoot()) {
                // 외부에서 들어옴
                startActivity(new Intent(this, SplashActivity.class));
            } else {
                // 내부에서 들어옴
            }
        } else {
            String pushType = getIntent().getStringExtra(AppConstants.EXTRA.PUSH_TYPE);
            String pushKey = getIntent().getStringExtra(AppConstants.EXTRA.PUSH_KEY);

            if (isTaskRoot()) {
                // 외부에서 들어옴
                Intent intent = new Intent(this, SplashActivity.class);
                intent.putExtra(AppConstants.EXTRA.PUSH_KEY, pushKey);
                intent.putExtra(AppConstants.EXTRA.PUSH_TYPE, pushType);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                // 내부에서 들어옴
                if (MessagingService.PUSHTYPE_CHAT_TEXT.equals(pushType) || MessagingService.PUSHTYPE_CHAT_FILE.equals(pushType)) {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(AppConstants.EXTRA.CHAT_ID, pushKey);
                    startActivity(intent);
                }
            }
        }

        finish();
    }
}