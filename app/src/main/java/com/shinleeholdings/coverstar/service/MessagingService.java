package com.shinleeholdings.coverstar.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shinleeholdings.coverstar.CoverStarSchemeActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;

public class MessagingService extends FirebaseMessagingService {

    public static final int PUSH_GROUP_ID = 1983;
    public static String channelId = "CoverStar";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.PUSH_ID, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        DebugLogger.i("onMessageReceived : " + remoteMessage.toString());

        if (remoteMessage == null) {
            return;
        }
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        Context context = MyApplication.getContext();
        String pushLink = "";
        String message = "";

        // TODO 정리 필요 : 푸시 스펙
        NotificationCompat.Builder notiBuilder = getNotificationBuilder(context, pushLink).
                setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("test")
                .setContentText("message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("message"));
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(PUSH_GROUP_ID, notiBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context, String pushLink) {
        return new NotificationCompat.Builder(context, channelId)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(PendingIntent.getActivity(context, 0, getNotificationIntent(context, pushLink), PendingIntent.FLAG_UPDATE_CURRENT))
                .setLocalOnly(true)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    private Intent getNotificationIntent(Context context, String pushLink) {
        Intent intent = new Intent(context, CoverStarSchemeActivity.class);
        intent.setData(Uri.parse(pushLink));
        return intent;
    }
}
