package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.MainActivity;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityUserPasswordBinding;
import com.shinleeholdings.coverstar.util.BackClickEventHandler;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.LoginHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.SharedPreferenceHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.HashMap;

import network.model.BaseResponse;
import network.model.CoverStarUser;
import network.model.DefaultResult;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class UserPasswordActivity extends BaseActivity {

    public static final String MODE_JOIN = "MODE_JOIN";
    public static final String MODE_LOGIN = "MODE_LOGIN";

    private ActivityUserPasswordBinding binding;

    private String firstPassword = "";
    private String secondPassword = "";

    private boolean isPasswordModeFirst = true;

    private String pwMode = "";

    private CoverStarUser loginUserData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String mode = getIntent().getStringExtra(AppConstants.EXTRA.MODE);
        if (TextUtils.isEmpty(mode)) {
            pwMode = MODE_JOIN;
        } else {
            pwMode = mode;
        }
        loginUserData = getIntent().getParcelableExtra(AppConstants.EXTRA.USER_DATA);

        initUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> {
            Util.hideKeyboard(UserPasswordActivity.this);
            onBackPressed();
        });

        binding.passwordLayout.setOnClickListener(view -> {
            if (isPasswordModeFirst) {
                Util.showKeyboard(UserPasswordActivity.this, binding.pw1EditText);
            } else {
                Util.showKeyboard(UserPasswordActivity.this, binding.pw2EditText);
            }
        });

        binding.pw1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                firstPassword = editable.toString();
                updatePasswordLayout();
            }
        });

        binding.pw2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                secondPassword = editable.toString();
                updatePasswordLayout();
            }
        });

        if (pwMode.equals(MODE_JOIN)) {
            binding.starChainIntroLayout.setVisibility(View.VISIBLE);
            binding.passwordInputLayout.setVisibility(View.GONE);
            binding.agreeLayout.setVisibility(View.VISIBLE);
            binding.rememberTextView.setText(getString(R.string.password_need_remember));
            binding.pwDescriptionTextView.setText(getString(R.string.password_warning));
            binding.nextButton.setText(getString(R.string.next));
        } else {
            binding.titleLayout.titleTextView.setText(getString(R.string.login));

            binding.starChainIntroLayout.setVisibility(View.GONE);
            binding.passwordInputLayout.setVisibility(View.VISIBLE);

            binding.rememberTextView.setText(getString(R.string.password_forgot));
            binding.pwDescriptionTextView.setText(getString(R.string.password_forgot_description));
            binding.nextButton.setText(getString(R.string.login));

            binding.passwordSettingTopLayout.setVisibility(View.GONE);
            binding.passwordLoginTopTextView.setVisibility(View.VISIBLE);
            binding.passwordSubTextView.setText(getString(R.string.password_login_first));
            binding.agreeLayout.setVisibility(View.INVISIBLE);
        }

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pwMode.equals(MODE_LOGIN)) {
                    startLogin();
                    return;
                }

                if (binding.starChainIntroLayout.getVisibility() == View.VISIBLE) {
                    binding.agreeLayout.setSelected(false);
                    firstPassword = "";
                    binding.pw1EditText.setText("");
                    setPasswordFirstMode();
                } else {
                    if (isPasswordModeFirst) {
                        if (TextUtils.isEmpty(firstPassword) || firstPassword.trim().length() < 6) {
                            Toast.makeText(UserPasswordActivity.this, getString(R.string.password_input), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        isPasswordModeFirst = false;
                        setPasswordSecondMode();
                    } else {
                        if (TextUtils.isEmpty(secondPassword) || secondPassword.trim().length() < 6) {
                            Toast.makeText(UserPasswordActivity.this, getString(R.string.password_input), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (firstPassword.equals(secondPassword) == false) {
                            Toast.makeText(UserPasswordActivity.this, getString(R.string.password_not_same), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (binding.agreeLayout.isSelected() == false) {
                            Toast.makeText(UserPasswordActivity.this, getString(R.string.agree_password_use), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startJoin();
                    }
                }
            }
        });

        binding.agreeLayout.setOnClickListener(view -> view.setSelected(!view.isSelected()));
    }

    private void startJoin() {
        loginUserData.userPwd = firstPassword;

        HashMap<String, String> param = new HashMap<>();
        param.put("userId", loginUserData.userId);
        param.put("nickName", loginUserData.nickName);
        param.put("userPwd", loginUserData.userPwd);
        param.put("coinPwd", loginUserData.userPwd);
        param.put("userProfileImage", loginUserData.userProfileImage);
        param.put("pushId", SharedPreferenceHelper.getInstance().getStringPreference(SharedPreferenceHelper.PUSH_ID));
        param.put("device", "1");
        param.put("userDialCode", loginUserData.userDialCode);
        param.put("userNation", loginUserData.userNation);

        //가입시 추천해서 성공한 사람은 3으로 보냄 아니면 0으로 보냄
        if (TextUtils.isEmpty(loginUserData.recommend)) {
            param.put("curCoin", "0");
        } else {
            param.put("curCoin", "3");
        }

        param.put("recommendKey", loginUserData.recommend);

        ProgressDialogHelper.show(this);
        RetroClient.getApiInterface().joinCoverStar(param).enqueue(new RetroCallback<DefaultResult>() {
            @Override
            public void onSuccess(BaseResponse<DefaultResult> receivedData) {
                ProgressDialogHelper.dismiss();
                Intent intent = new Intent(UserPasswordActivity.this, SettingCompleteActivity.class);
                intent.putExtra(AppConstants.EXTRA.USER_DATA, loginUserData);
                startActivity(intent);
            }

            @Override
            public void onFailure(BaseResponse<DefaultResult> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }

    private void startLogin() {
        if (TextUtils.isEmpty(firstPassword) || firstPassword.trim().length() < 6) {
            Toast.makeText(UserPasswordActivity.this, getString(R.string.password_input), Toast.LENGTH_SHORT).show();
            return;
        }

        LoginHelper.getSingleInstance().login(this, loginUserData.userId, firstPassword, false, new LoginHelper.ILoginResultListener() {
            @Override
            public void onComplete(boolean success) {
                if (success) {
                    Intent intent = new Intent(UserPasswordActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void updatePasswordLayout() {
        String pw = "";
        if (isPasswordModeFirst) {
            pw = firstPassword;
        } else {
            pw = secondPassword;
        }

        int length = pw.length();
        for (int i=0; i < length; i++) {
            String item = String.valueOf(pw.charAt(i));
            switch (i) {
                case 0: binding.pw1ValueTextView.setText(item); break;
                case 1: binding.pw2ValueTextView.setText(item); break;
                case 2: binding.pw3ValueTextView.setText(item); break;
                case 3: binding.pw4ValueTextView.setText(item); break;
                case 4: binding.pw5ValueTextView.setText(item); break;
                case 5: binding.pw6ValueTextView.setText(item); break;
            }
        }

        if (length == 0) {
            binding.pw1ValueTextView.setText("");
            binding.pw2ValueTextView.setText("");
            binding.pw3ValueTextView.setText("");
            binding.pw4ValueTextView.setText("");
            binding.pw5ValueTextView.setText("");
            binding.pw6ValueTextView.setText("");
        } else if(length == 1) {
            binding.pw2ValueTextView.setText("");
            binding.pw3ValueTextView.setText("");
            binding.pw4ValueTextView.setText("");
            binding.pw5ValueTextView.setText("");
            binding.pw6ValueTextView.setText("");
        } else if(length == 2) {
            binding.pw3ValueTextView.setText("");
            binding.pw4ValueTextView.setText("");
            binding.pw5ValueTextView.setText("");
            binding.pw6ValueTextView.setText("");
        } else if(length == 3) {
            binding.pw4ValueTextView.setText("");
            binding.pw5ValueTextView.setText("");
            binding.pw6ValueTextView.setText("");
        } else if(length == 4) {
            binding.pw5ValueTextView.setText("");
            binding.pw6ValueTextView.setText("");
        } else if(length == 5) {
            binding.pw6ValueTextView.setText("");
        }
    }

    private void setPasswordFirstMode() {
        binding.starChainIntroLayout.setVisibility(View.GONE);
        binding.passwordInputLayout.setVisibility(View.VISIBLE);
        binding.passwordSettingTopLayout.setVisibility(View.VISIBLE);
        binding.passwordLoginTopTextView.setVisibility(View.GONE);
        binding.passwordTitleTextView.setText(getString(R.string.password_setting));
        binding.passwordSubTextView.setText(getString(R.string.password_use));
        updatePasswordLayout();
    }

    private void setPasswordSecondMode() {
        binding.passwordTitleTextView.setText(getString(R.string.password_confirm));
        binding.passwordSubTextView.setText(getString(R.string.password_reinput));
        updatePasswordLayout();
    }

    @Override
    public void onBackPressed() {
        if (pwMode.equals(MODE_JOIN)) {
            if (isPasswordModeFirst == false) {
                secondPassword = "";
                binding.pw2EditText.setText("");
                isPasswordModeFirst = true;
                setPasswordFirstMode();
            } else {
                if (binding.starChainIntroLayout.getVisibility() != View.VISIBLE) {
                    binding.starChainIntroLayout.setVisibility(View.VISIBLE);
                    binding.passwordInputLayout.setVisibility(View.GONE);
                } else {
                    BackClickEventHandler.onBackPressed(this);
                }
            }
        } else {
            super.onBackPressed();
        }
    }
}