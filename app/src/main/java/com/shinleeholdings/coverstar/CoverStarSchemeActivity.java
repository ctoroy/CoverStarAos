package com.shinleeholdings.coverstar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CoverStarSchemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            handleUri(uri);
        }

        finish();
    }

    private void handleUri(Uri data) {
        if (isTaskRoot()) {
            // 외부에서 들어옴
            // TODO 스플래시로 시작한다. data 프리퍼런스에 저장해서 로그인 이후에 사용하자
            Intent intent = new Intent(this, CoverStarSchemeActivity.class);
//            intent.setData(data);
            startActivity(intent);
        } else {
            // 내부에서 들어옴
            // TODO 데이터 핸들하기
        }
    }
}