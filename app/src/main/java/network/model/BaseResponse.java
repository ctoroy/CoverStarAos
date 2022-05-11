package network.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import network.ServerAPIConstants;

public class BaseResponse<T> {
    @SerializedName("message")
    private String message;

    @SerializedName("result")
    private String result;

    @SerializedName("data")
    public T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        if (TextUtils.isEmpty(result)) {
            result = ServerAPIConstants.SERVER_RETURN_CODE.UNKNOWN_ERROR;
        }
        return result;
    }

    public void setResultCode(String errorCode) {
        this.result = errorCode;
    }

    public boolean isSuccess() {
        return result.equals(ServerAPIConstants.SERVER_RETURN_CODE.SUCCESS);
    }
}
