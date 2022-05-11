package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.text.TextUtils;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.LoginUserData;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class LoginHelper {

	public static final String PHONE_CERT_MODE_JOIN = "JOIN";
	public static final String PHONE_CERT_MODE_LOGIN = "LOGIN";
	public static final String PHONE_CERT_MODE_RECERT = "RECERT";

	public interface ILoginResultListener {
		public void onComplete(boolean success);
	}

	private static volatile LoginHelper instance;
	private final static Object lockObject = new Object();

	private LoginUserData mLoginUserData;

	public static LoginHelper getSingleInstance() {
		if (instance == null) {
			synchronized (lockObject) {
				if (instance == null) {
					instance = new LoginHelper();
				}
			}
		}

		return instance;
	}

	public int getMyCoinCount() {
		// TODO 내 코인개수 할당 및 업데이트 로직 정리
		return 10;
	}

	public String getLoginUserImagePath() {
		// TODO 사용자 이미지 설정
		return "";
	}

	public String getLoginUserNickName() {
		// TODO 사용자 닉네임 설정
		return "테스트입니다.";
	}

	public LoginUserData getSavedLoginUserData() {
		if (mLoginUserData == null) {
			mLoginUserData = SharedPreferenceHelper.getInstance().getObject(SharedPreferenceHelper.LOGIN_USER_DATA, LoginUserData.class);
		}
		return mLoginUserData;
	}

	public void saveLoginUserData(LoginUserData data) {
		SharedPreferenceHelper.getInstance().putObject(SharedPreferenceHelper.LOGIN_USER_DATA, data);
	}

	public boolean hasLoginInfo() {
		String loginId = SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LOGIN_ID);
		String loginPw = SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LOGIN_PW);

		if (TextUtils.isEmpty(loginId) || TextUtils.isEmpty(loginPw)) {
			return false;
		}
		return true;
	}

	public void startAutoLogin(Activity activity, ILoginResultListener listener) {
		String loginId = SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LOGIN_ID);
		String loginPw = SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.LOGIN_PW);

		if (TextUtils.isEmpty(loginId) || TextUtils.isEmpty(loginPw)) {
			listener.onComplete(false);
			return;
		}
		login(activity, loginId, loginPw, true, listener);
	}

	public void login(Activity activity, String id, String pw, boolean isAutoLogin, ILoginResultListener listener) {
		ProgressDialogHelper.show(activity);
		HashMap<String, String> param = new HashMap<>();
		param.put("userId", id);
		param.put("userPwd", pw);
		param.put("device", "1");
		param.put("pushId", SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.PUSH_ID));

		RetroClient.getApiInterface().loginCoverStar(param).enqueue(new RetroCallback<LoginUserData>() {
			@Override
			public void onSuccess(BaseResponse<LoginUserData> receivedData) {
				ProgressDialogHelper.dismiss();
				mLoginUserData = receivedData.data;
				if (isAutoLogin == false) {
					SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.LOGIN_ID, id);
					SharedPreferenceHelper.getInstance().setSharedPreference(SharedPreferenceHelper.LOGIN_PW, pw);
				}
				saveLoginUserData(mLoginUserData);
				listener.onComplete(true);
			}

			@Override
			public void onFailure(BaseResponse<LoginUserData> response) {
				ProgressDialogHelper.dismiss();
				listener.onComplete(false);
			}
		});
	}
}