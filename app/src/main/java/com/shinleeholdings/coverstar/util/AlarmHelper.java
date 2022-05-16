package com.shinleeholdings.coverstar.util;

import com.shinleeholdings.coverstar.data.AlarmItem;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AlarmHelper {
    private static final String TAG = "AlarmHelper";
    private static volatile AlarmHelper instance;
    private final static Object lockObject = new Object();
    public static AlarmHelper getSingleInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new AlarmHelper();
                }
            }
        }

        return instance;
    }

    public ArrayList<AlarmItem> getAlarmList() {
        DebugLogger.i(TAG, "alarmHelper getAlarmList");

        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).allowQueriesOnUiThread(true).build();
        Realm defaultRealm = Realm.getInstance(config);

        ArrayList<AlarmItem> itemList = new ArrayList<>(defaultRealm.copyFromRealm(defaultRealm.where(AlarmItem.class).findAll()));
        DebugLogger.i(TAG, "alarmHelper getAlarmList result : " + itemList.size());
        return itemList;
    }

    public void addAlarmItem(AlarmItem item) {
        DebugLogger.i(TAG, "alarmHelper addAlarmItem start");
        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).allowQueriesOnUiThread(true).build();
        Realm defaultRealm = Realm.getInstance(config);
        defaultRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                DebugLogger.i(TAG, "alarmHelper addAlarmItem insertOrUpdate start");
                realm.insertOrUpdate(item);
                DebugLogger.i(TAG, "alarmHelper addAlarmItem insertOrUpdate finished");
            }
        });
    }
}
