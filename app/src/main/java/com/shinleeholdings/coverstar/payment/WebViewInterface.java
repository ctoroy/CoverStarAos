package com.shinleeholdings.coverstar.payment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.webkit.JavascriptInterface;

import com.shinleeholdings.coverstar.util.DebugLogger;

/**
 * Created by Administrator on 2016-08-30.
 */
public class WebViewInterface {

    public static final String WEBVIEW_JS_INTERFACE_NAME = "coverstar";
    private Context mContext;
    private Handler mHandler = new Handler();

    public WebViewInterface(Context context) {
        mContext = context;
    }

    /**
     * Activity를 종료함.
     * sendBackKey
     */
    @JavascriptInterface
        public void sendFinishKey(){
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                DebugLogger.i("webViewInterface", "sendFinishKey");
                // TODO 이걸로 처리하는지 확인
                Activity act = (Activity)mContext;
                act.setResult(Activity.RESULT_OK);
                act.finish();
            }
        });
    }

    @JavascriptInterface
    public void onShopClose(){
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                DebugLogger.i("webViewInterface", "onShopClose");
            }
        });
    }

    /**
     * WebViewActivity를 호출함
     * @param url
     * @param title
     */
    @JavascriptInterface
    public void internalUrl(final String url, final String title){
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                DebugLogger.i("webViewInterface", "internalUrl : " + url);
                Intent intent = new Intent(mContext, PaymentWebViewActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", title);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 외부 브라우저를 호출함.
     * @param url
     */
    @JavascriptInterface
    public void externalUrl(final String url){
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                DebugLogger.i("webViewInterface", "externalUrl : " + url);
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }
}
