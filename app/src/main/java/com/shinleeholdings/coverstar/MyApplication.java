package com.shinleeholdings.coverstar;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.shinleeholdings.coverstar.service.MessagingService;

public class MyApplication extends Application {

    private static MyApplication appContext = null;

    private static Thread mUiThread;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        appContext = this;

        mUiThread = Thread.currentThread();
        mHandler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MessagingService.channelId,
                    appContext.getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            ((NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }

    public static Context getContext() {
        return appContext;
    }

    public static void runOnUiThread(Runnable runnable) {
        if (Thread.currentThread() == mUiThread) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }

    public static Handler getUiHandler() {
        if (Thread.currentThread() == mUiThread) {
            return new Handler();
        } else {
            return new Handler(mHandler.getLooper());
        }
    }

    private static Activity mActivity;

    public static void setActivity(Activity activity) {
        mActivity = activity;
    }

    public static Activity getActivity() {
        return mActivity;
    }
}
