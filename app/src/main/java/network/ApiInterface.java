package network;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestGroupDataList;
import network.model.ContestDataList;
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

    @POST("setVote")
    @FormUrlEncoded
    Call<BaseResponse<defaultResult>> setVote(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("updateUserProfile")
    @FormUrlEncoded
    Call<BaseResponse<defaultResult>> updateUserProfile(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("getList")
    @FormUrlEncoded
    Call<BaseResponse<ContestDataList>> getHomeList(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("getUserVodList")
    @FormUrlEncoded
    Call<BaseResponse<ContestDataList>> getUserVodList(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("getLiveListName")
    @FormUrlEncoded
    Call<BaseResponse<ContestDataList>> getLiveListName(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("getContestList")
    @FormUrlEncoded
    Call<BaseResponse<ContestGroupDataList>> getContestList(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("getLastList")
    @FormUrlEncoded
    Call<BaseResponse<ContestDataList>> getLastList(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("startBroadCast")
    @FormUrlEncoded
    Call<BaseResponse<defaultResult>> startBroadCast(@FieldMap(encoded = true) HashMap<String, String> body);

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
