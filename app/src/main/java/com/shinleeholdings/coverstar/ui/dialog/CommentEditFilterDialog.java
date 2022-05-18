package com.shinleeholdings.coverstar.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.data.CommentBase;
import com.shinleeholdings.coverstar.data.CommentItem;
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

    public void init(CommentBase item, View.OnClickListener listener) {
        binding.closeLayout.setOnClickListener(view -> dismiss());

        binding.fixLayout.setVisibility(View.GONE);

        if (item.isMyContestComment()) {
//                영상 올린사람은 – 삭제, 신고
        } else if (item.isMyComment()) {
//                댓글 올린 사람은 - 삭제
            binding.reportLayout.setVisibility(View.GONE);
        } else {
//                일반 유저는 – 신고
            binding.deleteLayout.setVisibility(View.GONE);
        }

        binding.fixLayout.setOnClickListener(view -> {
            listener.onClick(view);
            dismiss();
        });

        binding.deleteLayout.setOnClickListener(view -> {
            listener.onClick(view);
            dismiss();
        });

        binding.reportLayout.setOnClickListener(view -> {
            listener.onClick(view);
            dismiss();
        });
    }
}
