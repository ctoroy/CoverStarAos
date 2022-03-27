package com.shinleeholdings.coverstar.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;

public class MessagingService extends FirebaseMessagingService {

    private static final String NOTIFICATION_ID = "HubTalk";
    public static final int NOTIFICATION_ID_PUSH_MESSAGE = 1111;
    private static final String NOTICIFATION_NAME = "HUBTALK";

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

        if (remoteMessage == null) {
            return;
        }
        // TODO 푸시 처리
    }
}
