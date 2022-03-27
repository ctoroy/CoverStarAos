package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.shinleeholdings.coverstar.R;

public class Util {

    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity, EditText editText) {
        if (activity == null || editText == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        imm.showSoftInput(editText, 0);
    }

    public static String appVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo packInfo = manager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void goStore(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static boolean isActivityAvailable(Activity activity) {
        boolean isAvailable = false;
        if (activity != null && activity.isFinishing() == false) {
            if (activity.isDestroyed() == false) {
                isAvailable = true;
            }
        }

        return isAvailable;
    }

    /**
     * 색상 변경 함수
     *
     * @param context
     * @param fulltext    풀 텍스트
     * @param textToColor 변경할 텍스트
     * @return
     */
    public static SpannableStringBuilder getSectionOfTextColor(Context context, String fulltext, String... textToColor) {
        SpannableStringBuilder builder = new SpannableStringBuilder(fulltext);

        for (String textItem : textToColor) {
            if (textItem.length() > 0 && !textItem.trim().equals("")) {
                //for counting start/end indexes
                int startingIndex = fulltext.indexOf(textItem);
                int endingIndex = startingIndex + textItem.length();

                if (startingIndex >= 0 && endingIndex >= 0) {
                    builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)), startingIndex, endingIndex, 0);
                    builder.setSpan(new BackgroundColorSpan(ContextCompat.getColor(context, R.color.black)), startingIndex, endingIndex, 0);
                }
            }
        }

        return builder;
    }

    public static SpannableStringBuilder changeAllIncludedTextColor(Context context, String fullText, String searchText) {
        SpannableStringBuilder builder = new SpannableStringBuilder(fullText);

        if (searchText.length() > 0 && !searchText.trim().equals("")) {
            int index = fullText.indexOf(searchText);
            int matchLength = searchText.length();

            while (index >= 0) {  // indexOf returns -1 if no match found
                int endPosition = index + matchLength;
                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.white)), index, endPosition, 0);
                builder.setSpan(new BackgroundColorSpan(ContextCompat.getColor(context, R.color.black)), index, endPosition, 0);
                index = fullText.indexOf(searchText, endPosition);
            }
        }

        return builder;
    }
}
