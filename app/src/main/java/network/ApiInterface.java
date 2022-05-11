package network;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.HomeContentsDataList;
import network.model.LoginUserData;
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
    @POST("common/uploadImage")
    Call<BaseResponse<PhotoUploadResult>> uploadUserProfile(@Part MultipartBody.Part imgFile);

    @POST("getList")
    @FormUrlEncoded
    Call<BaseResponse<HomeContentsDataList>> getHomeList(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("join")
    @FormUrlEncoded
    Call<BaseResponse<defaultResult>> joinCoverStar(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("login")
    @FormUrlEncoded
    Call<BaseResponse<LoginUserData>> loginCoverStar(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("checkDupId")
    @FormUrlEncoded
    Call<BaseResponse<defaultResult>> checkExistUser(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("checkRecommend")
    @FormUrlEncoded
    Call<BaseResponse<defaultResult>> checkRecommend(@FieldMap(encoded = true) HashMap<String, String> body);
}
