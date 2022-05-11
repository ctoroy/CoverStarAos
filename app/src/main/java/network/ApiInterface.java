package network;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.LoginResult;
import network.model.PhotoUploadResult;
import network.model.defaultResult;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("/coverstarAPI/common/uploadImage")
    Call<BaseResponse<PhotoUploadResult>> uploadUserProfile(@Part MultipartBody.Part imgFile);

    @POST("/coverstarAPI/common/login")
    @FormUrlEncoded
    Call<BaseResponse<LoginResult>> loginCoverStar(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("/coverstarAPI/checkDupId")
    @FormUrlEncoded
    Call<BaseResponse<defaultResult>> checkExistUser(@FieldMap(encoded = true) HashMap<String, String> body);
}
