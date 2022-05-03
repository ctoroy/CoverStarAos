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

public abstract class BottomSheetDialogView extends BottomSheetDialog {
    public BottomSheetDialogView(@NonNull Context context) {
        super(context, R.style.AppTheme_BottomSheetDialog);
        init();
    }

    public BottomSheetDialogView(@NonNull Context context, int theme) {
        super(context, R.style.AppTheme_BottomSheetDialog);
        init();
    }

    protected BottomSheetDialogView(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, R.style.AppTheme_BottomSheetDialog);
        init();
    }

    abstract View inflateView(LayoutInflater inflater);

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.AppTheme);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentsView = inflateView(inflater);
        setContentView(contentsView);

        contentsView.requestLayout();
        contentsView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentsView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                FrameLayout bottomSheet = findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    int viewHeight = contentsView.getHeight();
                    if (viewHeight != -1) {
                        bottomSheet.getLayoutParams().height = viewHeight;
                        behavior.setPeekHeight(viewHeight);
                        bottomSheet.getParent().getParent().requestLayout();
                    }
                }
            }
        });

        setDismissWithAnimation(true);
    }
}
