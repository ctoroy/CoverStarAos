package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.shinleeholdings.coverstar.AppConstants;
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

    public static void showMessagePopup(Activity activity, String message, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setMessage(message);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                listener.onClick(dialogInterface, i);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showTwoButtonMessagePopup(Activity activity, String title, String message,
                                                 String positiveText, String negativeText,
                                                 View.OnClickListener positiveListener,
                                                 View.OnClickListener negativeListener) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                positiveListener.onClick(null);
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
                negativeListener.onClick(null);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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

    public static void showRegistConfirmPopup(Activity activity, int participateCount, View.OnClickListener confirmClickListener) {
        if (participateCount == 0) {
            confirmClickListener.onClick(null);
            return;
        }

        final Dialog dialog = new Dialog(activity, R.style.Theme_TransparentBackground);
        ViewGroup view = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.popup_participate_confirm, null);
        dialog.setContentView(view);

        FrameLayout closeLayout = view.findViewById(R.id.closeLayout);
        closeLayout.setOnClickListener(view1 -> dialog.dismiss());

        TextView participateConfirmTextView = view.findViewById(R.id.participateConfirmTextView);
        participateConfirmTextView.setText(String.format(activity.getString(R.string.participate_count_confirm), Util.getCoinDisplayCountString(participateCount)));

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view13 -> dialog.dismiss());
        Button okButton = view.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                confirmClickListener.onClick(view);
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static void showPointCheckPopup(Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_TransparentBackground);
        ViewGroup view = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.popup_participate_point_check, null);
        dialog.setContentView(view);

        FrameLayout closeLayout = view.findViewById(R.id.closeLayout);
        closeLayout.setOnClickListener(view1 -> dialog.dismiss());

        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(view13 -> dialog.dismiss());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
