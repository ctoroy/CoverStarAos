package network.retrofit;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.DialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import network.ServerAPIConstants;
import network.interceptor.ConnectivityInterceptor;
import network.model.BaseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetroCallback<T> implements Callback<T> {

    public abstract void onSuccess(BaseResponse receivedData);
    public abstract void onFailure(BaseResponse response);

    private Activity getCurrentActivity() {
        return MyApplication.getActivity();
    }

    @Override
    public void onResponse(final Call<T> call, Response<T> response) {
        try {
            if (!response.isSuccessful()) {
                sendCustomResultFail("API RequestFail", response.code() + "");
                try {
                    response.errorBody().close();
                } catch (Exception e) {
                    DebugLogger.d(e);
                }

            } else {
                final BaseResponse requestResult = (BaseResponse) response.body();

                String requestUrl = call.request().url().toString();
                DebugLogger.e("returnCode : " + requestResult.getResultCode() + ", requestUrl : " + requestUrl);

                String resultMessage = requestResult.getMessage();
                if (requestResult.isSuccess() == false && TextUtils.isEmpty(resultMessage) == false && needPopupPassRequestMethod(call) == false) {
                    if (Util.isActivityAvailable(getCurrentActivity())) {
                        DialogHelper.showMessagePopup(getCurrentActivity(), resultMessage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sendResult(requestResult);
                            }
                        });
                        return;
                    }
                }
                sendResult(requestResult);
            }
        } catch (Exception e) {
            DebugLogger.d(e);
            sendCustomResultFail(e.getMessage(), ServerAPIConstants.SERVER_RETURN_CODE.API_RESULT_ERROR);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof ConnectivityInterceptor.NoConnectivityException) {
            Activity activity = getCurrentActivity();
            if (Util.isActivityAvailable(activity)) {
                DialogHelper.showToast(activity, activity.getString(R.string.network_not_connected), false);
            }
        }

        String message = "";
        if (t != null && TextUtils.isEmpty(t.getMessage()) == false) {
            message = t.getMessage();
        } else {
            message = "API Callback unKnown";
        }
        sendCustomResultFail(message, ServerAPIConstants.SERVER_RETURN_CODE.API_CALLBACK_FAIL);
    }

    private boolean needPopupPassRequestMethod(Call<T> method) {
        // 서버 결과 팝업이 필요 없는 Method의 경우 여기에 추가
//        String url = method.request().url().toString();
//        if (url.contains("/popUp/getPopUp")) {
//            return true;
//        }
        return false;
    }

    private void sendResult(BaseResponse requestResult) {
        try {
            if (requestResult.isSuccess()) {
                onSuccess(requestResult);
            } else {
                onFailure(requestResult);
            }
        }catch (Exception e){
            DebugLogger.d(e);
        }
    }

    private void sendCustomResultFail(String message, String errorCode) {
        DebugLogger.e("onResponse sendCustomResultFail errorCode : " + errorCode + ", message : " + message);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(message + " \n errorCode : " + errorCode);
        baseResponse.setResultCode(errorCode);
        sendResult(baseResponse);
    }
}
