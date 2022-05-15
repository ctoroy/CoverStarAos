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

    public void init(SortType selectedSortType, ISortTypeSelectListener listener) {
        binding.closeLayout.setOnClickListener(view -> dismiss());

         switch (selectedSortType) {
             case SEARCH:
                 binding.orderSearchLayout.setSelected(true);
                 break;
             case LATEST:
                 binding.orderRecentlyLayout.setSelected(true);
                 break;
             default:
                 binding.orderPopularLayout.setSelected(true);
                 break;
         }

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
