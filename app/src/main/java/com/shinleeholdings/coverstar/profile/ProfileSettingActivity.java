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
import com.shinleeholdings.coverstar.util.DebugLogger;
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
                selectProfileImage()
           );

        binding.nextButton.setOnClickListener(view -> {
            String nickName = binding.nickNameEditText.getText().toString();

            if (TextUtils.isEmpty(nickName)) {
                binding.nickNameHintTextView.setText(getString(R.string.nickname_input));
                binding.nickNameHintTextView.setEnabled(false);
                return;
            }

            // TODO 서버에 닉네임 체크

            if (selectedImageFile != null) {
                uploadImageFile();
            } else {
                updateUserProfileInfo();
            }
        });
    }

    private void updateUserProfileInfo() {
        startPasswordActivity();
    }

    private void uploadImageFile() {
        ProgressDialogHelper.show(this);
        RetroClient.getApiInterface().uploadUserProfile(Util.getImageBody("imgFile", selectedImageFile)).enqueue(new RetroCallback<PhotoUploadResult>() {
            @Override
            public void onSuccess(BaseResponse receivedData) {
                ProgressDialogHelper.dismiss();
                PhotoUploadResult result = (PhotoUploadResult)receivedData;
                updateUserProfileInfo();
            }

            @Override
            public void onFailure(BaseResponse response) {
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

    private void startPasswordActivity() {
        Intent phoneCertIntent = new Intent(this, UserPasswordActivity.class);
        phoneCertIntent.putExtra(AppConstants.EXTRA.PW_MODE, UserPasswordActivity.PW_MODE_JOIN);
        startActivity(phoneCertIntent);
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