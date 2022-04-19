package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityProfileSettingBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;

public class ProfileSettingActivity extends BaseActivity {
    private ActivityProfileSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> onBackPressed());

        binding.userPhotoLayout.setOnClickListener(view ->
                // TODO 프로필 이미지 세팅
                selectProfileImage()
           );

        binding.nextButton.setOnClickListener(view -> {
            // TODO 프로필 이미지 세팅 체크

            String nickName = binding.nickNameEditText.getText().toString();

            if (TextUtils.isEmpty(nickName)) {
                binding.nickNameHintTextView.setText(getString(R.string.nickname_input));
                binding.nickNameHintTextView.setEnabled(false);
                return;
            }

            // TODO 서버에 닉네임 체크

            startPasswordActivity();
        });
    }

    private void selectProfileImage() {
        // TODO
    }

    private void startPasswordActivity() {
        Intent phoneCertIntent = new Intent(this, UserPasswordActivity.class);
        phoneCertIntent.putExtra(AppConstants.EXTRA.PW_MODE, UserPasswordActivity.PW_MODE_JOIN);
        startActivity(phoneCertIntent);
    }
}