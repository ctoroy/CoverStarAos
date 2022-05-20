package com.shinleeholdings.coverstar.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.databinding.ActivityPaymentWebviewBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.tosspayments.android.auth.interfaces.ConnectPayAuthWebManager;
import com.tosspayments.android.auth.utils.ConnectPayAuthManager;
import com.tosspayments.android.ocr.common.ConnectPayOcrManager;
import com.tosspayments.android.ocr.interfaces.ConnectPayOcrWebManager;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

// https://github.com/tosspayments/android-sdk-util-sample
public class PaymentWebViewActivity extends BaseActivity {
    // TODO 매니페스트의 data scheme 도 확인필요

    private ConnectPayAuthWebManager connectPayAuthWebManager = new ConnectPayAuthWebManager(this);
    private ConnectPayOcrWebManager connectPayOcrWebManager = new ConnectPayOcrWebManager(this);

    private ActivityPaymentWebviewBinding binding;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String targetUrl = getIntent().getStringExtra(AppConstants.EXTRA.WEBVIEW_URL);
        DebugLogger.i("tossPayments targetUrl : " + targetUrl);

        WebSettings webSettings = binding.nonLeakWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // javascript를 실행할 수 있도록 설정
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportMultipleWindows(true); // 여러개의 윈도우를 사용할 수 있도록 설정
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        binding.nonLeakWebView.setWebContentsDebuggingEnabled(true);
        binding.nonLeakWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        binding.nonLeakWebView.setFocusableInTouchMode(true);

        connectPayAuthWebManager.setCallback(new ConnectPayAuthWebManager.Callback() {
            @Override
            public void onPostScript(@NonNull String script) {
                DebugLogger.i("tossPayments connectPayAuthWebManager onPostScript : " + script);
                binding.nonLeakWebView.loadUrl(script);
            }
        });

        connectPayOcrWebManager.setCallback(new ConnectPayOcrWebManager.Callback() {
            @Override
            public void onPostScript(@NonNull String script) {
                DebugLogger.i("tossPayments connectPayOcrWebManager onPostScript : " + script);
                binding.nonLeakWebView.loadUrl(script);
            }
        });

        binding.nonLeakWebView.addJavascriptInterface(connectPayOcrWebManager.getJavaScriptInterface(), ConnectPayOcrWebManager.JAVASCRIPT_INTERFACE_NAME);
        binding.nonLeakWebView.addJavascriptInterface(connectPayAuthWebManager.getJavaScriptInterface(),ConnectPayAuthWebManager.JAVASCRIPT_INTERFACE_NAME);
        binding.nonLeakWebView.loadUrl(targetUrl);
    }

    @Override
    protected void onDestroy() {
        binding.nonLeakWebView.destroy();
        super.onDestroy();
    }

    private void requestBioMetricAuth() {
        ConnectPayAuthManager.requestBioMetricAuth(this, "MODULUSMODULUSSE", "EXPONENTEXPONENT", new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {
                // TODO 결과
                return null;
            }
        });
    }

    private void requestCardScan() {
        // TODO 카드스캔 호출해야하나?
        ConnectPayOcrManager.requestCardScan(this, "", ConnectPayOcrWebManager.REQUEST_CODE_CARD_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConnectPayOcrWebManager.REQUEST_CODE_CARD_SCAN) {
            connectPayOcrWebManager.handleActivityResult(requestCode, resultCode, data);
//            if (data != null) {
//                String cardScanResult = data.getStringExtra(ConnectPayOcrWebManager.EXTRA_CARD_SCAN_RESULT_SCRIPT);
//                // TODO
//            }
        }
    }
}
