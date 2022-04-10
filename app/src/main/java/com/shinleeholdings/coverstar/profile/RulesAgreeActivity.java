package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityRulesAgreeBinding;
import com.shinleeholdings.coverstar.util.Util;

public class RulesAgreeActivity extends AppCompatActivity {

    private ActivityRulesAgreeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRulesAgreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> finish());

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
            // TODO 이용자 약관 상세 보여주기
        });

        binding.privateRuleLayout.setOnClickListener(view -> {
            // TODO 개인정보 처리방침 약관 상세 보여주기
        });

        binding.userRuleCheckBoxLayout.setOnClickListener(view -> {
            if (binding.userRuleCheckBoxLayout.isSelected()) {
                binding.userRuleCheckBoxLayout.setSelected(false);
            } else {
                binding.userRuleCheckBoxLayout.setSelected(true);
            }
            updateAllRuleAgree();
        });

        binding.privateRuleCheckBoxLayout.setOnClickListener(view -> {
            if (binding.privateRuleCheckBoxLayout.isSelected()) {
                binding.privateRuleCheckBoxLayout.setSelected(false);
            } else {
                binding.privateRuleCheckBoxLayout.setSelected(true);
            }
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
        phoneCertIntent.putExtra(AppConstants.EXTRA.PHONE_CERT_MODE, PhoneCertActivity.PHONE_CERT_MODE_JOIN);
        startActivity(phoneCertIntent);
    }
}