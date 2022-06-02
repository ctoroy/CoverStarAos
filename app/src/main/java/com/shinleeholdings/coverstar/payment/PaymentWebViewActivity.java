package com.shinleeholdings.coverstar.payment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.databinding.ActivityPaymentWebviewBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.Util;

public class PaymentWebViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPaymentWebviewBinding binding = ActivityPaymentWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String targetUrl = getIntent().getStringExtra(AppConstants.EXTRA.WEBVIEW_URL);

        DebugLogger.i("PaymentWebViewActivity targetUrl : " + targetUrl);

        binding.webviewLayout.setWebViewClient(new MyWebViewClient());

        WebSettings webSettings = binding.webviewLayout.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setJavaScriptEnabled(true); // javascript를 실행할 수 있도록 설정
        webSettings.setLoadWithOverviewMode(true);
        binding.webviewLayout.setWebChromeClient(new CustomWebViewClient(this));
        /**
         * 화면 포트에 맞도록 웹 설정을 변경합니다.
         * v 1.1.1 버전 웹 퍼블리셔 이슈로 인하여 해당 오류 사항을 수정하지않습니다.
         *
         * mWebView.getSettings().setUseWideViewPort(true);
         * mWebView.getSettings().setLoadWithOverviewMode(true);
         * mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
         *
         */
        binding.webviewLayout.addJavascriptInterface(new WebViewInterface(this),WebViewInterface.WEBVIEW_JS_INTERFACE_NAME);

        WebView.setWebContentsDebuggingEnabled(true);

        binding.webviewLayout.loadUrl(targetUrl);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                DebugLogger.i("PaymentWebViewActivity shouldOverrideUrlLoading : " + url);
                if (URLUtil.isNetworkUrl(url) == false && URLUtil.isJavaScriptUrl(url) == false) {
                    Uri uri = Uri.parse(url);
                    if (uri.getScheme().equals("intent")) {
                        return startSchemeIntent(url);
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        return true;
                    }
                }
            } catch (Exception ignored) {
                // ignore any url parsing exceptions
            }
            return false;
        }

        private boolean startSchemeIntent(String url) throws Exception {
            Intent schemeIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            try {
                startActivity(schemeIntent);
                return true;
            } catch (Exception e) {
                String packageName = schemeIntent.getPackage();
                if (TextUtils.isEmpty(packageName) == false) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    return true;
                }
            }

            return false;
        }
    }

    public class CustomWebViewClient extends WebChromeClient {

        private Context context;

        public CustomWebViewClient(Context context) {
            this.context = context;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
            return super.onCreateWindow(view, dialog, userGesture, resultMsg);
        }
    }
}
