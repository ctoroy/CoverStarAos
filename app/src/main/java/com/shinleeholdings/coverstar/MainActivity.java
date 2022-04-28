package com.shinleeholdings.coverstar;

import android.os.Bundle;

import com.shinleeholdings.coverstar.databinding.ActivityMainBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;
import com.shinleeholdings.coverstar.util.BaseActivity;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 알림 리스트 작업
        // 바텀시트 메뉴 작업

        // 탭 구조로 메인 액티비티 만들기 , 프래그먼트도
            // 홈 / 지난 영상 / 참가 신청 / 마이페이지
    }

    @Override
    public void onBackPressed() {
        BackClickEventHandler.onBackPressed(this);
    }
}