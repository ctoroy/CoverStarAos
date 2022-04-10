package com.shinleeholdings.coverstar.profile;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CountryData;
import com.shinleeholdings.coverstar.databinding.ActivityPhoneCertBinding;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.DialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.concurrent.TimeUnit;

public class PhoneCertActivity extends AppCompatActivity {

    public static final String PHONE_CERT_MODE_JOIN = "JOIN";
    public static final String PHONE_CERT_MODE_LOGIN = "LOGIN";
    public static final String PHONE_CERT_MODE_RECERT = "RECERT";

    private static final long SMS_AUTH_TIME = 3 * 60 * 1000;

    private String certMode = "";

    private ActivityPhoneCertBinding binding;

    private CountryData selectedCountry;

    CountDownTimer cTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneCertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String mode = getIntent().getStringExtra(AppConstants.EXTRA.PHONE_CERT_MODE);
        if (TextUtils.isEmpty(mode)) {
            certMode = PHONE_CERT_MODE_JOIN;
        } else {
            certMode = mode;
        }

        initUi();
        setModeUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> finish());

        binding.selectCountryLayout.setOnClickListener(view -> DialogHelper.showCountrySelectPopup(PhoneCertActivity.this, country -> {
            selectedCountry = country;
            setCountrySelected();
        }));

        binding.nextButton.setOnClickListener(view -> {
            if (certMode.equals(PHONE_CERT_MODE_JOIN)) {
                sendPhoneAuth();
            } else {
                checkUserValid();
            }
        });
    }

    private void setModeUi() {
        binding.phoneNumInputLayout.setVisibility(View.VISIBLE);
        binding.certNumInputLayout.setVisibility(View.GONE);
        if (certMode.equals(PHONE_CERT_MODE_RECERT)){
            binding.titleLayout.titleTextView.setText(R.string.login);
            binding.phoneCertNeed.setText(Util.getSectionOfTextBold(getString(R.string.insert_registered_num), getString(R.string.registered_num)));
            // TODO 기본 국가정보 세팅
            selectedCountry = new CountryData("한국", "+82", "");
            binding.loginHintTextView.setVisibility(View.GONE);
            binding.userPhotoLayout.setVisibility(View.GONE);
            binding.nextButton.setText(getString(R.string.next));
        } else if (certMode.equals(PHONE_CERT_MODE_LOGIN)) {
            binding.titleLayout.titleTextView.setText(R.string.login);
            // TODO 저장된 사용자 정보 세팅(닉네임, 휴대폰, 사진경로, 국가번호)
            String nickName = "테스트";
            String phoneNum = "01031240677";
            selectedCountry = new CountryData("한국", "+82", "");

            String message = String.format(getString(R.string.welcome_user), nickName);
            binding.phoneCertNeed.setText(Util.getSectionOfTextBold(message, nickName));
            binding.phoneNumEditText.setText(phoneNum);
            binding.loginHintTextView.setVisibility(View.VISIBLE);
            binding.userPhotoLayout.setVisibility(View.VISIBLE);
            binding.nextButton.setText(getString(R.string.next));
        } else {
            binding.titleLayout.titleTextView.setText(R.string.phone_cert);
            binding.phoneCertNeed.setText(Util.getSectionOfTextBold(getString(R.string.phone_cert_agree_need), getString(R.string.phone_cert)));

            // TODO 기본 국가정보 세팅
            selectedCountry = new CountryData("한국", "+82", "");
            binding.loginHintTextView.setVisibility(View.GONE);
            binding.userPhotoLayout.setVisibility(View.GONE);
            binding.nextButton.setText(getString(R.string.send_phone_auth));
        }

        setCountrySelected();
    }

    private void setCountrySelected() {
        binding.selectedCountryTextView.setText(selectedCountry.getName() + selectedCountry.getCode());
    }

    private void showCertNumInputLayout() {
        binding.phoneNumInputLayout.setVisibility(View.GONE);
        binding.certNumInputLayout.setVisibility(View.VISIBLE);

        binding.sendedCertNumNeed.setText(Util.getSectionOfTextBold(getString(R.string.insert_sended_number), getString(R.string.sended_number)));

        binding.certNumEditText.setText("");
        startCertificationTimer();

        binding.resendLayout.setVisibility(View.GONE);
        binding.resendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPhoneAuth();
            }
        });

        binding.nextButton.setText(getString(R.string.cert_complete));
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 파베 인증 완료 API 처리 작업
                cancelCertificationTimer();
            }
        });
    }

    private void checkUserValid() {
        // TODO 사용자 유효성 API 체크

        // 유효성 체크 실패시 가입모드로 전환
        certMode = PHONE_CERT_MODE_JOIN;
        setModeUi();
    }

    private void sendPhoneAuth() {
        String phoneNum = binding.phoneNumEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, getString(R.string.phone_input_hint), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        // TODO 사용자의 국가 설정
//        auth.setLanguageCode("kr");
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber("+8201031240677")       // Phone number to verify
                        .setTimeout(SMS_AUTH_TIME, TimeUnit.MILLISECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // This callback will be invoked in two situations:
                                // 1 - Instant verification. In some cases the phone number can be instantly
                                //     verified without needing to send or enter a verification code.
                                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                //     detect the incoming verification SMS and perform verification without
                                //     user action.
                                DebugLogger.i("PhoneAuth Complete : " + phoneAuthCredential);
                                DialogHelper.showCertNumSendCompletePopup(PhoneCertActivity.this);
                                showCertNumInputLayout();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // This callback is invoked in an invalid request for verification is made,
                                // for instance if the the phone number format is not valid.
                                DebugLogger.i("PhoneAuth fail : " + e.getMessage());

                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid request
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                }

                                // Show a message and update the UI
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startCertificationTimer() {
        cancelCertificationTimer();
        setRemainTimeText(SMS_AUTH_TIME);

        cTimer = new CountDownTimer(SMS_AUTH_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setRemainTimeText(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                cancelCertificationTimer();
                binding.resendLayout.setVisibility(View.VISIBLE);
            }
        };
        cTimer.start();
    }

    private void setRemainTimeText(long millisUntilFinished) {
        String hms = String.format(getString(R.string.remain_time_format),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));
        binding.remainTimeTextView.setText(hms);
    }

    private void cancelCertificationTimer() {
        if (cTimer != null) {
            cTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCertificationTimer();
    }
}