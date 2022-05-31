package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

import network.model.BaseResponse;
import network.model.CoverStarUser;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class LoginHelper {
	public static final String PHONE_CERT_MODE_JOIN = "JOIN";
	public static final String PHONE_CERT_MODE_LOGIN = "LOGIN";
	public static final String PHONE_CERT_MODE_RECERT = "RECERT";

	public interface ILoginResultListener {
		void onComplete(boolean success);
	}

	public interface ILoginUserInfoChangeEventListener {
		void onMyCoinUpdated(int currentCoinCount);
		void onUserInfoUpdated();
	}
	private final ArrayList<ILoginUserInfoChangeEventListener> userInfoChangeListenerList = new ArrayList<>();

	public void addUserInfoChangeListener(ILoginUserInfoChangeEventListener listener) {
		synchronized(userInfoChangeListenerList) {
			if (userInfoChangeListenerList.contains(listener) == false) {
				userInfoChangeListenerList.add(listener);
			}
		}
	}

	public void removeUserInfoChangeListener(ILoginUserInfoChangeEventListener listener) {
		synchronized(userInfoChangeListenerList) {
			userInfoChangeListenerList.remove(listener);
		}
	}

	public void sendCoinChangeEvent(int count) {
		synchronized(userInfoChangeListenerList) {
			if (userInfoChangeListenerList.size() == 0) {
				return;
			}

			for (int i = 0; i< userInfoChangeListenerList.size(); i++) {
				userInfoChangeListenerList.get(i).onMyCoinUpdated(count);
			}
		}
	}

	public void sendUserInfoChangeEvent() {
		synchronized(userInfoChangeListenerList) {
			if (userInfoChangeListenerList.size() == 0) {
				return;
			}

			for (int i = 0; i< userInfoChangeListenerList.size(); i++) {
				userInfoChangeListenerList.get(i).onUserInfoUpdated();
			}
		}

	}

	private static volatile LoginHelper instance;
	private final static Object lockObject = new Object();

	private CoverStarUser mLoginUserData;

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
		CoverStarUser userData = getSavedLoginUserData();
		if (userData == null) {
			return;
		}

		int updateCoinCount = userData.curCoin + updateValue;
		userData.curCoin = updateCoinCount;
		saveLoginUserData(userData);
		sendCoinChangeEvent(updateCoinCount);
	}

	public void updateUserInfo(String imagePath, String nickName) {
		CoverStarUser userData = getSavedLoginUserData();
		if (userData == null) {
			return;
		}

		userData.userProfileImage = imagePath;
		userData.nickName = nickName;

		saveLoginUserData(userData);
		sendUserInfoChangeEvent();
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

	public String getLoginUserId() {
		if (getSavedLoginUserData() == null) {
			return "";
		}
		return getSavedLoginUserData().userId;
	}

	public CoverStarUser getSavedLoginUserData() {
		if (mLoginUserData == null) {
			mLoginUserData = SharedPreferenceHelper.getInstance().getObject(SharedPreferenceHelper.LOGIN_USER_DATA, CoverStarUser.class);
		}
		return mLoginUserData;
	}

	public void saveLoginUserData(CoverStarUser data) {
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

		RetroClient.getApiInterface().loginCoverStar(param).enqueue(new RetroCallback<CoverStarUser>() {
			@Override
			public void onSuccess(BaseResponse<CoverStarUser> receivedData) {
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
			public void onFailure(BaseResponse<CoverStarUser> response) {
				ProgressDialogHelper.dismiss();
				listener.onComplete(false);
			}
		});
	}
}