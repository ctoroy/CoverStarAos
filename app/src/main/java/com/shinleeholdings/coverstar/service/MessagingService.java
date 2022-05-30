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
import com.shinleeholdings.coverstar.data.AlarmItem;
import com.shinleeholdings.coverstar.util.AlarmHelper;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;

import org.json.JSONObject;

import java.util.Map;

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
        if (SharedPreferenceHelper.getInstance().getBooleanPreference(SharedPreferenceHelper.ALARM_IS_OFF)) {
            return;
        }
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        Context context = MyApplication.getContext();

        Map<String, String> pushDataMap = remoteMessage.getData();
        // TODO 푸시 : 타이틀과 메세지 처리
        String title = pushDataMap.get("TITLE");
        String message = pushDataMap.get("MESSAGE");

        JSONObject json =  new JSONObject(pushDataMap);
        String pushDataString = json.toString();

        // TODO 푸시 : 알림 데이터 세팅
        AlarmItem alarmItem = new AlarmItem();
        alarmItem.alarmTime = System.currentTimeMillis();
        alarmItem.alarmData = pushDataString;
        AlarmHelper.getSingleInstance().addAlarmItem(alarmItem);

        showNotificationGroup(context, pushDataString);

        NotificationCompat.Builder notiBuilder = getNotificationBuilder(context, pushDataString)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(message)
                .setGroup(PUSH_GROUP_ID + "")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setTicker(message);

        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(PUSH_GROUP_ID + "",  (int)(System.currentTimeMillis() / 1000), notiBuilder.build());
    }

    private void showNotificationGroup(Context context, String pushDataString) {
        NotificationCompat.Builder notificationBuilder = getNotificationBuilder(context, pushDataString)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(this.getString(R.string.app_name))
                .setGroupSummary(true)
                .setGroup(PUSH_GROUP_ID + "")
                .setContentText("");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(PUSH_GROUP_ID, notificationBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context, String pushDataString) {
        return new NotificationCompat.Builder(context, channelId)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentIntent(PendingIntent.getActivity(context, 0, getNotificationIntent(context, pushDataString), PendingIntent.FLAG_UPDATE_CURRENT))
                .setLocalOnly(true)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
    }

    private Intent getNotificationIntent(Context context, String pushDataString) {
        Intent intent = new Intent(context, CoverStarSchemeActivity.class);
        intent.setData(Uri.parse(pushDataString));
        return intent;
    }
}
