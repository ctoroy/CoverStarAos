package com.shinleeholdings.coverstar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.FragmentSettingBinding;
import com.shinleeholdings.coverstar.util.ImageLoader;

public class SettingFragment extends BaseFragment {

    private FragmentSettingBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initView() {
        binding.titleLayout.titleTextView.setText(getString(R.string.setting));
        binding.titleLayout.titleBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // TODO 사용자 이미지 설정
        ImageLoader.loadImage(binding.userImageView, "");

        // TODO 사용자 닉네임 설정
        binding.userNicknameTextView.setText("닉네임");

        binding.editProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 회원정보 수정
            }
        });

        binding.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 로그아웃
            }
        });

    }
}
