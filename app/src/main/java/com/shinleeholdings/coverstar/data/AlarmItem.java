package com.shinleeholdings.coverstar.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AlarmItem extends RealmObject {
    @PrimaryKey
    public long alarmTime;

    public String alarmData;
}
