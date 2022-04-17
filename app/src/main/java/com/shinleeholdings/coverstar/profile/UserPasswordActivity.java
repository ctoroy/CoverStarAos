package com.shinleeholdings.coverstar.profile;

import static com.shinleeholdings.coverstar.util.LoginHelper.PHONE_CERT_MODE_JOIN;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.databinding.ActivityOpeningBinding;
import com.shinleeholdings.coverstar.databinding.ActivityUserPasswordBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;

public class UserPasswordActivity extends AppCompatActivity {

    private ActivityUserPasswordBinding binding;
    private String certMode = "";

    private boolean isPasswordModeFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String mode = getIntent().getStringExtra(AppConstants.EXTRA.PHONE_CERT_MODE);
        if (TextUtils.isEmpty(mode)) {
            certMode = PHONE_CERT_MODE_JOIN;
        } else {
            certMode = mode;
        }

        initUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> onBackPressed());

        if (certMode == PHONE_CERT_MODE_JOIN) {
            binding.starChainIntroLayout.setVisibility(View.VISIBLE);
            binding.passwordInputLayout.setVisibility(View.GONE);
        } else {
            binding.starChainIntroLayout.setVisibility(View.GONE);
            binding.passwordInputLayout.setVisibility(View.VISIBLE);
        }

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (certMode == PHONE_CERT_MODE_JOIN) {
                    // TODO 비밀번호 입력 화면 세팅
                } else {

                }
            }
        });
    }

    private void setPasswordFirstMode() {

    }

    private void setPasswordSecondMode() {

    }

    @Override
    public void onBackPressed() {
        if (isPasswordModeFirst == false) {
            setPasswordFirstMode();
            return;
        }
        BackClickEventHandler.onBackPressed(this);
    }
}