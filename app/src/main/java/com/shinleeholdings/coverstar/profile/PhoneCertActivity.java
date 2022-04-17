package com.shinleeholdings.coverstar.profile;

import static com.shinleeholdings.coverstar.util.LoginHelper.PHONE_CERT_MODE_JOIN;
import static com.shinleeholdings.coverstar.util.LoginHelper.PHONE_CERT_MODE_LOGIN;
import static com.shinleeholdings.coverstar.util.LoginHelper.PHONE_CERT_MODE_RECERT;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CountryData;
import com.shinleeholdings.coverstar.databinding.ActivityPhoneCertBinding;
import com.shinleeholdings.coverstar.util.DebugLogger;
import com.shinleeholdings.coverstar.util.DialogHelper;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;

import java.util.concurrent.TimeUnit;

public class PhoneCertActivity extends AppCompatActivity {

    private static final long SMS_AUTH_TIME_SEC = 2 * 60;

    private String certMode = "";

    private ActivityPhoneCertBinding binding;

    private CountryData selectedCountry;

    CountDownTimer cTimer;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private PhoneAuthCredential authCredential = null;
    private String verificationId = "";
    private PhoneAuthProvider.ForceResendingToken token = null;

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
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> onBackPressed());

        binding.selectCountryLayout.setOnClickListener(view ->
            // TODO test
//                binding.ccp.performClick();
            DialogHelper.showCountrySelectPopup(PhoneCertActivity.this, country -> {
                selectedCountry = country;
                setCountrySelected();
            })
        );

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
        binding.resendLayout.setOnClickListener(view -> sendPhoneAuth());

        binding.nextButton.setText(getString(R.string.cert_complete));
        binding.nextButton.setOnClickListener(view -> {
            startFirebaseUserAuth();
        });
    }

    private void checkUserValid() {
        // TODO 사용자 유효성 API 체크

        // 유효성 체크 실패시 가입모드로 전환
        certMode = PHONE_CERT_MODE_JOIN;
        setModeUi();
    }

    private void initAuthInfo() {
        authCredential = null;
        verificationId = "";
        token = null;
    }

    private void sendPhoneAuth() {
        String phoneNum = binding.phoneNumEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, getString(R.string.phone_input_hint), Toast.LENGTH_SHORT).show();
            return;
        }

//        https://firebase.google.com/docs/auth/android/phone-auth?hl=ko&authuser=0
        initAuthInfo();
        // TODO 사용자의 국가 설정
//        mFirebaseAuth.setLanguageCode("kr");
        ProgressDialogHelper.show(this);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
                        .setPhoneNumber("+8201031240677")       // Phone number to verify
                        .setTimeout(0L, TimeUnit.SECONDS) // Timeout and unit
                        // disable "auto-retrieval" by setting the timeout of verifyPhoneNumber to 0
                        // (Reference: https://firebase.google.com/docs/reference/android/com/google/firebase/auth/PhoneAuthProvider.html)
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                즉시 인증(Instant verification) : 때에 따라서는 인증 코드를 보내거나 입력하지 않고 전화번호를 즉시 인증
//                                자동 검색(Auto-retrieval) : 일부 기기에서는 수신되는 인증 SMS를 Google Play 서비스가 자동으로 감지하여 사용자의 개입 없이 인증을 수행
//                                일부 이동통신사에서는 이 기능을 제공하지 않을 수 있습니다. SMS 메시지 끝에 11자리 해시를 포함하는 SMS Retriever API를 사용
                                ProgressDialogHelper.dismiss();
                                DebugLogger.i("PhoneAuth OnVerificationStateChangedCallbacks Complete : " + phoneAuthCredential);
                                PhoneCertActivity.this.authCredential = phoneAuthCredential;
                                // 이미 코드 설정까지 된거라 이걸로 로그인?
                                startFirebaseUserAuth();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                ProgressDialogHelper.dismiss();

                                DebugLogger.i("PhoneAuth OnVerificationStateChangedCallbacks fail : " + e.getMessage());
                                // TODO 에러 문구 노출 필요
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                }
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                ProgressDialogHelper.dismiss();

                                DebugLogger.i("PhoneAuth OnVerificationStateChangedCallbacks onCodeSent");
                                PhoneCertActivity.this.verificationId = verificationId;
                                PhoneCertActivity.this.token = token;
                                DialogHelper.showCertNumSendCompletePopup(PhoneCertActivity.this);
                                showCertNumInputLayout();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startFirebaseUserAuth() {
        if (authCredential == null) {
            String certNum = binding.certNumEditText.getText().toString();
            if (TextUtils.isEmpty(certNum) || TextUtils.isEmpty(verificationId)) {
                return;
            }
            authCredential = PhoneAuthProvider.getCredential(verificationId, certNum);
        }

        cancelCertificationTimer();

        ProgressDialogHelper.show(this);

        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            ProgressDialogHelper.dismiss();

            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                DebugLogger.i("PhoneAuth signInWithCredential:success");
                FirebaseUser user = task.getResult().getUser();

                // Update UI
                userAuthComplete();
            } else {
                // Sign in failed, display a message and update the UI
                DebugLogger.i("PhoneAuth signInWithCredential:failure");
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                }
            }
        });
    }

    private void userAuthComplete() {
        if (certMode == PHONE_CERT_MODE_LOGIN) {

        } else if (certMode == PHONE_CERT_MODE_RECERT) {

        } else {
            // 프로필 이미지와 닉네임 설정
        }

    }

    private void startCertificationTimer() {
        long remainTime = SMS_AUTH_TIME_SEC * 1000;
        cancelCertificationTimer();
        setRemainTimeText(remainTime);

        cTimer = new CountDownTimer(remainTime, 1000) {
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