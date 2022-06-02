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

    private static final String PAY_URL_REAL = "https://coverstar.tv/livepay/index.php";
    private static final String PAY_URL_DEV = "https://coverstar.tv/pay/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPaymentWebviewBinding binding = ActivityPaymentWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String amount = getIntent().getStringExtra(AppConstants.EXTRA.AMOUNT);
        String order = getIntent().getStringExtra(AppConstants.EXTRA.ORDER);
        String targetUrl = getRequestUrl(amount, order);

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

    private String getRequestUrl(String amount, String order) {
//        AMOUNT : 3000 (가격)
//        ORDERID : ORDER + NAME + YYYYMMDDHHMMSS (나중에 주문 확인용 맘대로 유니크)
//        ORDER : 3000P (제품명)
//        NAME : 8201031240677 (구매자명)
        String name = LoginHelper.getSingleInstance().getLoginUserId();
        String orderId = order + name + Util.getCurrentTimeToFormat("yyyyMMddHHmmss");
        String payUrl = "";
        if (DebugLogger.IS_DEBUG) {
            payUrl = PAY_URL_DEV;
        } else {
            payUrl = PAY_URL_REAL;
        }

       return payUrl + "?p="+amount+"&o="+orderId+"&n="+order+"&c="+name;
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                DebugLogger.i("PaymentWebViewActivity shouldOverrideUrlLoading : " + url);
                if (URLUtil.isAboutUrl(url) == false && URLUtil.isJavaScriptUrl(url) == false) {
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
            if (context != null) {
                WebView newWebView = new WebView(context);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
            }
            return true;
        }
    }
}
