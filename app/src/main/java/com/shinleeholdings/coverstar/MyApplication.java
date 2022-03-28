package com.shinleeholdings.coverstar;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

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
