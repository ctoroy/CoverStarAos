package network.retrofit;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.shinleeholdings.coverstar.util.DebugLogger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import network.ApiInterface;
import network.ServerAPIConstants;
import network.cookie.CookieManager;
import network.interceptor.ConnectivityInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    private static Object retrofitService;

    public static ApiInterface getApiInterface(){
        return (ApiInterface) getClient();
    }

    private static Object getClient() {
        if (retrofitService == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

            httpClientBuilder.readTimeout(60, TimeUnit.SECONDS);
            httpClientBuilder.connectTimeout(60, TimeUnit.SECONDS);
            httpClientBuilder.cookieJar(CookieManager.getInstance());

            if (DebugLogger.IS_DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClientBuilder.addInterceptor(logging);
            }

            httpClientBuilder.addInterceptor(new ConnectivityInterceptor());

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory());
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();

           Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ServerAPIConstants.SERVER_DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClientBuilder.build())
                    .build();

            retrofitService = retrofit.create(ApiInterface.class);
        }
        return retrofitService;
    }

    private static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }

    private static class StringAdapter extends TypeAdapter<String> {

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (TextUtils.isEmpty(value)) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }

        @Override
        public String read(com.google.gson.stream.JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return "";
            }
            return in.nextString();
        }
    }
}


