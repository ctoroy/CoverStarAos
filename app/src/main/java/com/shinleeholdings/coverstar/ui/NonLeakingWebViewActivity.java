package com.shinleeholdings.coverstar.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.databinding.ActivityWebviewBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;

public class NonLeakingWebViewActivity extends BaseActivity {

    private ActivityWebviewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String targetUrl = getIntent().getStringExtra(AppConstants.EXTRA.WEBVIEW_URL);
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

        binding.nonLeakWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        binding.nonLeakWebView.setFocusableInTouchMode(true);

        binding.nonLeakWebView.loadUrl(targetUrl);
    }

    @Override
    protected void onDestroy() {
        binding.nonLeakWebView.destroy();
        super.onDestroy();
    }
}
