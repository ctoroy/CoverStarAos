package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.text.TextUtils;

import java.util.ArrayList;
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
		void onComplete(boolean success);
	}

	public interface IMyCoinCountChangeListener {
		void onMyCoinUpdated(int currentCoinCount);
	}
	private final ArrayList<IMyCoinCountChangeListener> coinCountChangeListenerList = new ArrayList<>();

	public void addCoinCountChangeListener(IMyCoinCountChangeListener listener) {
		synchronized(coinCountChangeListenerList) {
			if (coinCountChangeListenerList.contains(listener) == false) {
				coinCountChangeListenerList.add(listener);
			}
		}
	}

	public void removeCoinCountChangeListener(IMyCoinCountChangeListener listener) {
		synchronized(coinCountChangeListenerList) {
			coinCountChangeListenerList.remove(listener);
		}
	}

	public void sendCoinChangeEvent(int count) {
		synchronized(coinCountChangeListenerList) {
			if (coinCountChangeListenerList.size() == 0) {
				return;
			}

			for (int i=0; i<coinCountChangeListenerList.size(); i++) {
				coinCountChangeListenerList.get(i).onMyCoinUpdated(count);
			}
		}
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
		if (getSavedLoginUserData() == null) {
			return 0;
		}
		return getSavedLoginUserData().curCoin;
	}

	public void updateMyCoin(int updateValue) {
		LoginUserData userData = getSavedLoginUserData();
		if (userData == null) {
			return;
		}

		int updateCoinCount = userData.curCoin + updateValue;
		userData.curCoin = updateCoinCount;
		saveLoginUserData(userData);
		sendCoinChangeEvent(updateCoinCount);
	}

	public String getLoginUserImagePath() {
		if (getSavedLoginUserData() == null) {
			return "";
		}
		return getSavedLoginUserData().userProfileImage;
	}

	public String getLoginUserNickName() {
		if (getSavedLoginUserData() == null) {
			return "";
		}
		return getSavedLoginUserData().nickName;
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