package com.shinleeholdings.coverstar.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.shinleeholdings.coverstar.databinding.DialogCommentEditFilterBinding;

public class CommentEditFilterDialog extends BottomSheetDialogView {

    public CommentEditFilterDialog(@NonNull Context context) {
        super(context);
    }

    DialogCommentEditFilterBinding binding;

    @Override
    View inflateView(LayoutInflater inflater) {
        binding = DialogCommentEditFilterBinding.inflate(inflater);
        return binding.getRoot();
    }

    public void init() {
        binding.closeLayout.setOnClickListener(view -> dismiss());

        binding.fixLayout.setOnClickListener(view -> {
            dismiss();
        });

        binding.deleteLayout.setOnClickListener(view -> {
            dismiss();
        });

        binding.reportLayout.setOnClickListener(view -> {
            dismiss();
        });
    }
}
