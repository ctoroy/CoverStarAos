package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityInputInviteCodeBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.CoverStarUser;
import network.model.DefaultResult;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class InputInviteCodeActivity extends BaseActivity {
    private ActivityInputInviteCodeBinding binding;

    private CoverStarUser loginUserData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputInviteCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginUserData = getIntent().getParcelableExtra(AppConstants.EXTRA.USER_DATA);

        initUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> onBackPressed());

        binding.inviteCodeRewardTextView.setText(Util.getSectionOfTextBold(getString(R.string.input_invite_code_reward), getString(R.string.input_invite_code_reward_3star)));

        binding.nextButton.setOnClickListener(view -> {
            // 추천인 코드
            String inviteCode = binding.inviteCodeEditText.getText().toString();
            if (TextUtils.isEmpty(inviteCode)) {
                startPasswordActivity(inviteCode);
            } else {
                checkInviteCode(inviteCode);
            }
        });
    }

    private void checkInviteCode(String inviteCode) {
        ProgressDialogHelper.show(this);
        HashMap<String, String> param = new HashMap<>();
        param.put("recommend", inviteCode);
        RetroClient.getApiInterface().checkRecommend(param).enqueue(new RetroCallback<DefaultResult>() {
            @Override
            public void onSuccess(BaseResponse<DefaultResult> receivedData) {
                ProgressDialogHelper.dismiss();
                startPasswordActivity(inviteCode);
            }

            @Override
            public void onFailure(BaseResponse<DefaultResult> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }

    private void startPasswordActivity(String inviteCode) {
        Intent intent = new Intent(this, UserPasswordActivity.class);
        loginUserData.recommend = inviteCode;
        intent.putExtra(AppConstants.EXTRA.USER_DATA, loginUserData);
        intent.putExtra(AppConstants.EXTRA.MODE, UserPasswordActivity.MODE_JOIN);
        startActivity(intent);
    }
}