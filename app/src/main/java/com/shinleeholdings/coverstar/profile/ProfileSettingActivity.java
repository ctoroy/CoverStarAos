package com.shinleeholdings.coverstar.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.ActivityProfileSettingBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;
import com.shinleeholdings.coverstar.util.ProgressDialogHelper;
import com.shinleeholdings.coverstar.util.Util;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnSelectedListener;
import gun0912.tedimagepicker.builder.type.MediaType;
import network.model.BaseResponse;
import network.model.PhotoUploadResult;
import network.retrofit.RetroCallback;
import network.retrofit.RetroClient;

public class ProfileSettingActivity extends BaseActivity {
    private ActivityProfileSettingBinding binding;
    private File selectedImageFile;

    public static final String MODE_JOIN = "MODE_JOIN";

    private String userId = "";
    private boolean isJoin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra(AppConstants.EXTRA.USER_ID);
        String mode = getIntent().getStringExtra(AppConstants.EXTRA.MODE);
        if (MODE_JOIN.equals(mode)) {
            isJoin = true;
        }

        initUi();
    }

    private void initUi() {
        binding.titleLayout.titleBackLayout.setOnClickListener(view -> onBackPressed());

        binding.userPhotoLayout.setOnClickListener(view ->
                selectProfileImage()
           );

        if (isJoin) {
            binding.nextButton.setText(getString(R.string.next));
        } else {
            binding.nextButton.setText(getString(R.string.change));
        }

        binding.nextButton.setOnClickListener(view -> {
            String nickName = binding.nickNameEditText.getText().toString();

            if (TextUtils.isEmpty(nickName)) {
                binding.nickNameHintTextView.setText(getString(R.string.nickname_input));
                binding.nickNameHintTextView.setEnabled(false);
                return;
            }

            if (selectedImageFile != null) {
                uploadImageFile();
            } else {
                updateUserProfileInfo("", nickName);
            }
        });
    }

    private void updateUserProfileInfo(String imagePath, String nickName) {

        if (isJoin) {
            Intent intent = new Intent(this, UserPasswordActivity.class);
            // TODO 추천코드 입력화면으로 이동시킨다.
            intent.putExtra(AppConstants.EXTRA.USER_ID, userId);
            intent.putExtra(AppConstants.EXTRA.IMAGE_PATH, imagePath);
            intent.putExtra(AppConstants.EXTRA.NICKNAME, nickName);
            intent.putExtra(AppConstants.EXTRA.MODE, UserPasswordActivity.MODE_JOIN);
            startActivity(intent);
        } else {
            // TODO 서버에 사진 및 닉네임 업데이트 API 호출, 프로필 정보 변경 및 업데이트 브로드캐스트 날리기, 받는쪽에서는 업데이트 처리
            finish();
        }
    }

    private void uploadImageFile() {
        ProgressDialogHelper.show(this);
        RetroClient.getApiInterface().uploadUserProfile(Util.getImageBody("imgFile", selectedImageFile)).enqueue(new RetroCallback<PhotoUploadResult>() {
            @Override
            public void onSuccess(BaseResponse<PhotoUploadResult> receivedData) {
                ProgressDialogHelper.dismiss();
                PhotoUploadResult result = receivedData.data;
                updateUserProfileInfo(result.imageUrl, binding.nickNameEditText.getText().toString());
            }

            @Override
            public void onFailure(BaseResponse<PhotoUploadResult> response) {
                ProgressDialogHelper.dismiss();
            }
        });
    }

    private void selectProfileImage() {
        TedImagePicker.with(this).mediaType(MediaType.IMAGE).start(new OnSelectedListener() {
            @Override
            public void onSelected(@NonNull Uri uri) {
                imageSelected(uri);
            }
        });
    }

    private void imageSelected(Uri imageUri) {
        CropImage.activity(imageUri)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setRequestedSize(1024, 1024)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                handleCroppedImageResult(data);
            }
        }
    }

    private void handleCroppedImageResult(Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        Uri croppedImageUri = result.getUri();
        selectedImageFile = new File(croppedImageUri.getPath());
        Bitmap bm = null;
        try {
            bm = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.userImageView.setImageBitmap(bm);
    }
}