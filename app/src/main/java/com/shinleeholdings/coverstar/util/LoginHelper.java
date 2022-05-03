package com.shinleeholdings.coverstar.util;

import android.app.Activity;

import com.shinleeholdings.coverstar.data.LoginUserData;

import java.util.HashMap;

import network.model.BaseResponse;
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

	public LoginUserData getSavedLoginUserData() {
		return SharedPreferenceHelper.getInstance().getObject(SharedPreferenceHelper.LOGIN_USER_DATA, LoginUserData.class);
	}

	public void saveLoginUserData(LoginUserData data) {
		SharedPreferenceHelper.getInstance().putObject(SharedPreferenceHelper.LOGIN_USER_DATA, data);
	}

	public void login(Activity activity, String id, String pw, ILoginResultListener listener) {
		ProgressDialogHelper.show(activity);
		HashMap param = new HashMap<String, String>();
		param.put("userId", id);
		param.put("userPwd", pw);
		param.put("pushId", SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.PUSH_ID));

		RetroClient.getApiInterface().login(param).enqueue(new RetroCallback<BaseResponse>() {
			@Override
			public void onSuccess(BaseResponse receivedData) {
				ProgressDialogHelper.dismiss();
				listener.onComplete(true);
			}

			@Override
			public void onFailure(BaseResponse response) {
				ProgressDialogHelper.dismiss();
				listener.onComplete(false);
			}
		});
	}
}