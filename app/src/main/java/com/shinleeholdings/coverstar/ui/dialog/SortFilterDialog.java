package com.shinleeholdings.coverstar.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.shinleeholdings.coverstar.databinding.DialogSortFilterBinding;

public class SortFilterDialog extends BottomSheetDialogView {

    public enum SortType {
        POPULAR, SEARCH, LATEST;
    }

    public interface ISortTypeSelectListener {
        public void onSortTypeSelected(SortType type);
    }

    public SortFilterDialog(@NonNull Context context) {
        super(context);
    }

    DialogSortFilterBinding binding;

    @Override
    View inflateView(LayoutInflater inflater) {
        binding = DialogSortFilterBinding.inflate(inflater);
        return binding.getRoot();
    }

    public void init(ISortTypeSelectListener listener) {
        binding.closeLayout.setOnClickListener(view -> dismiss());

        // TODO 아이콘들 Pressed 이미지 확인
        binding.orderPopularLayout.setOnClickListener(view -> {
            listener.onSortTypeSelected(SortType.POPULAR);
            dismiss();
        });

        binding.orderSearchLayout.setOnClickListener(view -> {
            listener.onSortTypeSelected(SortType.SEARCH);
            dismiss();
        });

        binding.orderRecentlyLayout.setOnClickListener(view -> {
            listener.onSortTypeSelected(SortType.LATEST);
            dismiss();
        });
    }
}
