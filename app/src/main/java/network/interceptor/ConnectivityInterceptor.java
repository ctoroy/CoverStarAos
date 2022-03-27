package network.interceptor;

import com.shinleeholdings.coverstar.util.NetworkHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectivityInterceptor implements Interceptor {

    public class NoConnectivityException extends IOException {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (NetworkHelper.isNetworkConnected() == false) {
            throw new NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}
