package network.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class LoginResult {
//	        "userId": "8201051391123",
//					"userPwd": "*6BB4837EB74329105EE4568DDA7DC67ED2CA2AD9",
//					"userType": 0, //NA
//					"nickName": "roypark",
//					"phoneNumber": "8201051391123",
//					"userEmail": "cto@coverstar.com", //NA
//					"curCoin": "0",
//					"userProfileImage": "https://d3fq0wmt9zn6f5.cloudfront.net/roy.jpg",
//					"pcPush": null,  //NA
//					"androidPush": null,
//					"iosPush": "i3j3do12idi3pnp123jdkpo23kdopko23pdk2",
//					"userDialCode": "82",
//					"userNation": "KR",
//					"recommend": "4178496412" //추천코드
	@SerializedName("userId") public String userId;
	@SerializedName("userPwd") public String userPwd;
	@SerializedName("userType") public int userType;
	@SerializedName("nickName") public String nickName;
	@SerializedName("phoneNumber") public String phoneNumber;
	@SerializedName("userEmail") public String userEmail;
	@SerializedName("curCoin") public long curCoin;
	@SerializedName("userProfileImage") public String userProfileImage;
	@SerializedName("userDialCode") public String userDialCode;
	@SerializedName("userNation") public String userNation;
	@SerializedName("recommend") public String recommend;


	public String getPhoneNumWithoutNationCode() {
		if (TextUtils.isEmpty(phoneNumber)) {
			return "";
		}

		if (phoneNumber.startsWith(userDialCode)) {
			return phoneNumber.replaceFirst(userDialCode, "");
		} else {
			return phoneNumber;
		}
	}
}

