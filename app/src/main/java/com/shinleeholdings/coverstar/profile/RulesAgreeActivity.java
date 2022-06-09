package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityRulesAgreeBinding;
import com.shinleeholdings.coverstar.ui.RuleDetailActivity;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.Util;

public class RulesAgreeActivity extends BaseActivity {

    private ActivityRulesAgreeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRulesAgreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> onBackPressed());

        binding.titleLayout.titleTextView.setText(R.string.rule_agree);
        binding.useRuleAgreeNeed.setText(Util.getSectionOfTextBold(getString(R.string.use_rule_agree_need), getString(R.string.use_rule)));

        binding.agreeAllRuleLayout.setOnClickListener(view -> {
            if (binding.agreeAllRuleLayout.isSelected()) {
                binding.agreeAllRuleLayout.setSelected(false);
                binding.userRuleCheckBoxLayout.setSelected(false);
                binding.privateRuleCheckBoxLayout.setSelected(false);
            } else {
                binding.agreeAllRuleLayout.setSelected(true);
                binding.userRuleCheckBoxLayout.setSelected(true);
                binding.privateRuleCheckBoxLayout.setSelected(true);
            }
        });

        binding.userRuleLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, RuleDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA.WEBVIEW_URL, AppConstants.USE_RULE_URL);
            startActivityForResult(intent, AppConstants.REQUEST_CODE.RULE_DETAIL_USE_RULE);
        });

        binding.privateRuleLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, RuleDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA.WEBVIEW_URL, AppConstants.PRIVATE_RULE_URL);
            startActivityForResult(intent, AppConstants.REQUEST_CODE.RULE_DETAIL_PRIVATE_RULE);
        });

        binding.userRuleCheckBoxLayout.setOnClickListener(view -> {
            binding.userRuleCheckBoxLayout.setSelected(!binding.userRuleCheckBoxLayout.isSelected());
            updateAllRuleAgree();
        });

        binding.privateRuleCheckBoxLayout.setOnClickListener(view -> {
            binding.privateRuleCheckBoxLayout.setSelected(!binding.privateRuleCheckBoxLayout.isSelected());
            updateAllRuleAgree();
        });

        binding.nextButton.setOnClickListener(view -> checkRuleAgree());
    }

    private void updateAllRuleAgree() {
        binding.agreeAllRuleLayout.setSelected(binding.privateRuleCheckBoxLayout.isSelected() && binding.userRuleCheckBoxLayout.isSelected());
    }

    private void checkRuleAgree() {
        if (!binding.userRuleCheckBoxLayout.isSelected() || !binding.privateRuleCheckBoxLayout.isSelected()) {
            Toast.makeText(this, getString(R.string.use_rule_agree_need), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent phoneCertIntent = new Intent(this, PhoneCertActivity.class);
        phoneCertIntent.putExtra(AppConstants.EXTRA.PHONE_CERT_MODE, LoginHelper.PHONE_CERT_MODE_JOIN);
        startActivity(phoneCertIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstants.REQUEST_CODE.RULE_DETAIL_USE_RULE) {
                binding.userRuleCheckBoxLayout.setSelected(true);
                updateAllRuleAgree();
            } else if (requestCode == AppConstants.REQUEST_CODE.RULE_DETAIL_PRIVATE_RULE) {
                binding.privateRuleCheckBoxLayout.setSelected(true);
                updateAllRuleAgree();
            }
        }
    }
}