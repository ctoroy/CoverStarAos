package com.shinleeholdings.coverstar.data;

import io.realm.RealmObject;

public class AlarmItem extends RealmObject {
    //        @PrimaryKey
//        @SerializedName("PDNO")
//        open var scode: String = "" // 종목 코드
//
//        @SerializedName("STOCK_ID")
//        open var stockId: Int = 0
//
//        @SerializedName("KEYWORDS")
//        open var keywordList: RealmList<String> = RealmList()

    public boolean isSelected = false;
}
