package com.shinleeholdings.coverstar.ui.custom;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;


public class NonLeakingWebView extends WebView {
    private static Field sConfigCallback;

    static {
        try {
            sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            sConfigCallback.setAccessible(true);

            // https://developer.android.com/reference/android/webkit/WebView.html?hl=ko#setDataDirectorySuffix(java.lang.String)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WebView.setDataDirectorySuffix("coverStar");
            }
        } catch (Exception e) {
            // ignored
        }
    }

    public NonLeakingWebView(Activity activity) {
        super(activity.getApplicationContext());

        setWebView(activity);
    }

    public NonLeakingWebView(Context context, AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);

        setWebView(context);
    }

    public NonLeakingWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context.getApplicationContext(), attrs, defStyle);

        setWebView(context);
    }

    private void setWebView(Context context) {
        setWebViewClient(new MyWebViewClient((Activity) context));
        setWebChromeClient(new CustomWebViewClient(context));
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            if (sConfigCallback != null) {
                sConfigCallback.set(null, null);
            }
        } catch (Exception e) {
        }

        try {
            clearCache(true);
        } catch (Exception e) {
        }
    }

    protected static class MyWebViewClient extends WebViewClient {

        protected WeakReference<Activity> activityRef;
        String INTENT_PROTOCOL_START = "intent:";
        String INTENT_PROTOCOL_INTENT = "#Intent;";
        String INTENT_PROTOCOL_PACKAGE = ";package=";
        String INTENT_PROTOCOL_SCHEME = ";scheme=";
        String INTENT_PROTOCOL_END = ";";
        String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

        public MyWebViewClient(Activity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {

                final Activity activity = activityRef.get();

                if (activity != null)
                    if (url.startsWith(INTENT_PROTOCOL_START)) {
                        int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                        int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                        if (customUrlEndIndex < 0) {
                            view.loadUrl(url);
                            return true;
                        } else {
                            int schemeStartIndex = url.indexOf(INTENT_PROTOCOL_SCHEME);

                            String customUrl;
                            customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                            if (schemeStartIndex >= 0) {
                                schemeStartIndex = schemeStartIndex + INTENT_PROTOCOL_SCHEME.length();
                                String schemeName = url.substring(schemeStartIndex);
                                int schemeEndIndex = schemeName.indexOf(INTENT_PROTOCOL_END);
                                schemeName = schemeName.substring(0, schemeEndIndex < 0 ? schemeName.length() : schemeEndIndex);
                                String cu2 = customUrl.substring(0, 2);
                                if (cu2.equals("//"))
                                    customUrl = schemeName + ":" + customUrl;
                            }

                            try {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                            } catch (ActivityNotFoundException e) {
                                int packageStartIndex = url.indexOf(INTENT_PROTOCOL_PACKAGE) + INTENT_PROTOCOL_PACKAGE.length();
                                String packageName = url.substring(packageStartIndex);
                                int packageEndIndex = packageName.indexOf(INTENT_PROTOCOL_END);
                                packageName = packageName.substring(0, packageEndIndex < 0 ? packageName.length() : packageEndIndex);
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                            }
                            return true;
                        }
                    } else if (Uri.parse(url).getScheme().equals("market")) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            activity.startActivity(intent);
                            return true;
                        } catch (ActivityNotFoundException e) {
                            // Google Play app is not installed, you may want to open the app store link
                            Uri uri = Uri.parse(url);
                            view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                            return false;
                        }
                    } else {
                        view.loadUrl(url);
                        return true;
                    }

            } catch (RuntimeException ignored) {
                // ignore any url parsing exceptions
            }
            return true;
        }


        //  웹페이지 로딩이 시작할 때 처리
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        //      웹페이지 로딩이 끝났을 때 처리
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    public class CustomWebViewClient extends WebChromeClient {

        private Context context;

        public CustomWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
            if (context != null) {
                WebView newWebView = new WebView(context);
                WebViewTransport transport = (WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
            }
            return true;
        }
    }
}
