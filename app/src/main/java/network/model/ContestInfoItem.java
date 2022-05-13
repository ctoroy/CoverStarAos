package network.model;

import com.google.gson.annotations.SerializedName;

public class ContestInfoItem {
//"contestId": 1,
//"contestTitle": "커버스타 5월 이벤트 ",
//contestMaker": "coverstar",
//contestType": "0",
//contestStartDate": "20220501000000",
//contestEndDate": "20220520000000",
//contestShotDate": "202206100000" //발표일
//contestPayAmt": 3, 
//contestCount": 0, //참여자수
//contestAward": 1000000, //총상금
    @SerializedName("contestId") public int contestId;
    @SerializedName("contestTitle") public String contestTitle;
    @SerializedName("contestMaker") public String contestMaker;
    @SerializedName("contestType") public int contestType;

    @SerializedName("contestStartDate") public String contestStartDate;
    @SerializedName("contestEndDate") public String contestEndDate;
    @SerializedName("contestShotDate") public String contestShotDate;

    @SerializedName("contestPayAmt") public int contestPayAmt;
    @SerializedName("contestCount") public int contestCount;
    @SerializedName("contestAward") public int contestAward;
}
