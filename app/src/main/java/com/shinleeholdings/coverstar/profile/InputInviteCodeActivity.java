package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityInputInviteCodeBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.Util;

public class InputInviteCodeActivity extends BaseActivity {
    private ActivityInputInviteCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputInviteCodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> onBackPressed());

        binding.inviteCodeRewardTextView.setText(Util.getSectionOfTextBold(getString(R.string.input_invite_code_reward), getString(R.string.input_invite_code_reward_3star)));

        binding.nextButton.setOnClickListener(view -> {
            // 추천인 코드
            String inviteCode = binding.inviteCodeEditText.getText().toString();

            // TODO 다음처리 필요
            startActivity(new Intent(InputInviteCodeActivity.this, SettingCompleteActivity.class));

        });
    }

    @Override
    public void onBackPressed() {
        BackClickEventHandler.onBackPressed(this);
    }
}