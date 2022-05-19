package com.shinleeholdings.coverstar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shinleeholdings.coverstar.AppConstants;
import com.shinleeholdings.coverstar.databinding.ActivityInputMessageBinding;
import com.shinleeholdings.coverstar.util.BaseActivity;

public class InputMessageActivity extends BaseActivity {

    private ActivityInputMessageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setResult(RESULT_CANCELED);

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.messageEditTextView.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(AppConstants.EXTRA.MESSAGE, message);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
