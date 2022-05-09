package network;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.PhotoUploadResult;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface ApiInterface {
    @Multipart
    @POST("/coverstarAPI/common/uploadImage")
    Call<PhotoUploadResult> uploadUserProfile(@Part MultipartBody.Part imgFile);

    @POST("/coverstarAPI/common/login")
    @FormUrlEncoded
    Call<BaseResponse> login(@FieldMap(encoded = true) HashMap<String, String> body);

}
