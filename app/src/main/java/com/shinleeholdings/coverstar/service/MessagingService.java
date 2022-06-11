package com.shinleeholdings.coverstar.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.CoverStarSchemeActivity;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.chatting.ChatMessageListHelper;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {

    public static final int PUSH_GROUP_ID = 1983;
    public static String channelId = "CoverStar";

    public static final String PUSHTYPE_DEFAULT = "0";
    public static final String PUSHTYPE_CHAT_TEXT = "1";
    public static final String PUSHTYPE_CHAT_FILE = "2";
    public static final String PUSHTYPE_NOTICE = "3";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.PUSH_ID, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        DebugLogger.i("onMessageReceived : " + remoteMessage.toString());
        if (SharedPreferenceHelper.getInstance().getBooleanPreference(SharedPreferenceHelper.ALARM_IS_OFF)) {
            return;
        }
        try {
            showNotification(remoteMessage);
        } catch (Exception e) {
        }
    }

    private void showNotification(RemoteMessage remoteMessage) {
        Map<String, String> messageData = remoteMessage.getData();
        DebugLogger.i("onMessageReceived messageData : " + messageData);

        String type = messageData.get("type");
        String key = messageData.get("key");
        int notiId = 0;

        if (type.equals(PUSHTYPE_CHAT_TEXT) || type.equals(PUSHTYPE_CHAT_FILE)) {
            String currentChattingRoomId = ChatMessageListHelper.getSingleInstance().getCurrentChattingId();
            if (TextUtils.isEmpty(currentChattingRoomId) == false && currentChattingRoomId.equals(key)) {
                // 현재 채팅방 내부에 있으면 푸시띄우지 않는다.
                return;
            }

            String loginId = SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LOGIN_ID);
            String loginPw = SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LOGIN_PW);
            if (TextUtils.isEmpty(loginId) || TextUtils.isEmpty(loginPw)) {
                return;
            }

            notiId = key.hashCode();
        } else {
            notiId = (int) (System.currentTimeMillis() / 1000);
        }

        Context context = MyApplication.getContext();
        String message = messageData.get("value");
        String title = messageData.get("title");
        if (TextUtils.isEmpty(title)) {
            title = getApplicationContext().getString(R.string.app_name);
        }

        Intent notificationIntent = getNotificationIntent(context, key, type);
        showNotificationGroup(context, notificationIntent);

        NotificationCompat.Builder notiBuilder = getNotificationBuilder(context, notificationIntent)
                .setSmallIcon(R.mipmap.app_icon)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(message)
                .setGroup(PUSH_GROUP_ID + "")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setTicker(message);

        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(PUSH_GROUP_ID + "",  notiId, notiBuilder.build());
    }

    private void showNotificationGroup(Context context, Intent notificationIntent) {
        NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context, notificationIntent)
                .setSmallIcon(R.mipmap.app_icon)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(this.getString(R.string.app_name))
                .setGroupSummary(true)
                .setGroup(PUSH_GROUP_ID + "")
                .setContentText("");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PUSH_GROUP_ID, notificationBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context, Intent notificationIntent) {
        return new NotificationCompat.Builder(context, channelId)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setLocalOnly(true)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    private Intent getNotificationIntent(Context context, String pushKey, String pushType) {
        Intent intent = new Intent(context, CoverStarSchemeActivity.class);
        intent.putExtra(AppConstants.EXTRA.PUSH_KEY, pushKey);
        intent.putExtra(AppConstants.EXTRA.PUSH_TYPE, pushType);
        return intent;
    }
}
