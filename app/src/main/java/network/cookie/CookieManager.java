package network.cookie;

import android.content.Context;

import com.shinleeholdings.coverstar.MyApplication;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieManager implements CookieJar {

    private static CookieManager cookieManager;

    private final PersistentCookieStore persistentCookieStore;

    public static CookieManager getInstance() {
        if (cookieManager == null) {
            synchronized (CookieManager.class) {
                if (cookieManager == null) {
                    cookieManager = new CookieManager(MyApplication.getContext());
                }
            }
        }
        return cookieManager;
    }

    private CookieManager(Context context) {
        persistentCookieStore = new PersistentCookieStore(context);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        persistentCookieStore.add(url, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return persistentCookieStore.get(url);
    }
}