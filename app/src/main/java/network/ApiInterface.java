package network;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.ContestGroupDataList;
import network.model.ContestDataList;
import network.model.LoginUserData;
import network.model.NoticeDataList;
import network.model.PhotoUploadResult;
import network.model.DefaultResult;
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

    @POST("setPlay")
    @FormUrlEncoded
    Call<BaseResponse<DefaultResult>> setPlay(@FieldMap(encoded = true) HashMap<String, String> body);

    // TODO 공지사항 API적용
    @POST("getNoticeList")
    @FormUrlEncoded
    Call<BaseResponse<NoticeDataList>> getNoticeList(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("getPlayList")
    @FormUrlEncoded
    Call<BaseResponse<ContestDataList>> getPlayList(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("getMyList")
    @FormUrlEncoded
    Call<BaseResponse<ContestDataList>> getMyList(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("getRoom")
    @FormUrlEncoded
    Call<BaseResponse<ContestDataList>> getContestDetail(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("setVote")
    @FormUrlEncoded
    Call<BaseResponse<DefaultResult>> setVote(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("updateUserProfile")
    @FormUrlEncoded
    Call<BaseResponse<DefaultResult>> updateUserProfile(@FieldMap(encoded = true) HashMap<String, String> body);

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
    Call<BaseResponse<DefaultResult>> startBroadCast(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("join")
    @FormUrlEncoded
    Call<BaseResponse<DefaultResult>> joinCoverStar(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("login")
    @FormUrlEncoded
    Call<BaseResponse<LoginUserData>> loginCoverStar(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("checkDupId")
    @FormUrlEncoded
    Call<BaseResponse<DefaultResult>> checkExistUser(@FieldMap(encoded = true) HashMap<String, String> body);

    @POST("checkRecommend")
    @FormUrlEncoded
    Call<BaseResponse<DefaultResult>> checkRecommend(@FieldMap(encoded = true) HashMap<String, String> body);
}
