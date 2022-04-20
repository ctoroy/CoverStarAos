package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shinleeholdings.coverstar.R;

public class DialogHelper {
    private static final boolean DIALOG_CANCELABLE = false;

    public static void showToast(Context context, String message, boolean center) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        if (center) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }

        toast.show();
    }

    public static void showCertNumSendCompletePopup(Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_TransparentBackground);
        ViewGroup view = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.popup_phone_cert_send_complete, null);
        dialog.setContentView(view);

        FrameLayout closeLayout = view.findViewById(R.id.closeLayout);
        closeLayout.setOnClickListener(view1 -> dialog.dismiss());

        TextView countryListView = view.findViewById(R.id.certNumSendCompleteTextView);

        countryListView.setText(Util.getSectionOfTextColor(activity, R.color.color_f45786, activity.getString(R.string.cert_num_send_complete), activity.getString(R.string.send)));
        Button okButton = view.findViewById(R.id.okButton);
        okButton.setOnClickListener(view12 -> dialog.dismiss());
        dialog.show();
    }
}
