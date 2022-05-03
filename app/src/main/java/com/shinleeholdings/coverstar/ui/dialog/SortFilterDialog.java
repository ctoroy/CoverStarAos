package com.shinleeholdings.coverstar.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.shinleeholdings.coverstar.R;
import com.shinleeholdings.coverstar.databinding.DialogSortFilterBinding;

public class SortFilterDialog extends BottomSheetDialogView {

    public SortFilterDialog(@NonNull Context context) {
        super(context);
    }

    DialogSortFilterBinding binding;

    @Override
    View inflateView(LayoutInflater inflater) {
        binding = DialogSortFilterBinding.inflate(inflater);
        return binding.getRoot();
    }
}
