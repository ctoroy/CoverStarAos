package network;

import java.util.HashMap;

import network.model.BaseResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {

    @POST("/HubTalk_API/user/deleteContact")
    @FormUrlEncoded
    Call<BaseResponse> deleteContact(@FieldMap(encoded = true) HashMap<String, String> body);

    @GET
    Call<ResponseBody> getInputStreamFromUrl(@Url String url);
}
