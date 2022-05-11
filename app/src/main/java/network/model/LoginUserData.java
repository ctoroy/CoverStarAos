package network.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class LoginUserData implements Parcelable {
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

	public LoginUserData() {

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userId);
		dest.writeString(userPwd);
		dest.writeInt(userType);
		dest.writeString(nickName);
		dest.writeString(phoneNumber);
		dest.writeString(userEmail);
		dest.writeLong(curCoin);
		dest.writeString(userProfileImage);
		dest.writeString(userDialCode);
		dest.writeString(userNation);
		dest.writeString(recommend);
	}

	protected LoginUserData(Parcel in) {
		userId = in.readString();
		userPwd = in.readString();
		userType = in.readInt();
		nickName = in.readString();
		phoneNumber = in.readString();
		userEmail = in.readString();
		curCoin = in.readLong();
		userProfileImage = in.readString();
		userDialCode = in.readString();
		userNation = in.readString();
		recommend = in.readString();
	}

	public static final Creator<LoginUserData> CREATOR = new Creator<LoginUserData>() {
		@Override
		public LoginUserData createFromParcel(Parcel in) {
			return new LoginUserData(in);
		}

		@Override
		public LoginUserData[] newArray(int size) {
			return new LoginUserData[size];
		}
	};
}

