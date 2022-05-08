package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Process;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;

public class Util {
    public static String getFormattedCountString(int value) {
        // TODO Format 적용(12,000)
        String displayFormat = value + "";
        return displayFormat;
    }
    public static String getCoinDisplayCountString(int value) {
        return String.format(MyApplication.getContext().getString(R.string.coin_count_format), getFormattedCountString(value));
    }

    public static int dpToPixel(Context context, float dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
        return px;
    }

    public static String getDisplayCountString(int value) {
//		999 -> 999
//		1000 -> 1K
//		1,000K -> 1M
        String unit = "";

        if (value >= 1000000) {
            value = value / 1000000;
            unit = "M";
        } else {
            if (value >= 1000) {
                value = value / 1000;
                unit = "K";
            }
        }

        return value + unit;
    }

    public static void setTextViewBold(TextView textview, boolean bold) {
        if (textview != null) {
            if (bold) {
                textview.setPaintFlags(textview.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
            } else {
                textview.setPaintFlags(textview.getPaintFlags() & ~Paint.FAKE_BOLD_TEXT_FLAG);
            }
        }
    }

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
     * bolder 로 변경해주는 함수
     *
     * @param fulltext   전체 텍스트
     * @param textToBold 변경할 텍스트
     * @return
     */
    public static SpannableStringBuilder getSectionOfTextBold(String fulltext, String... textToBold) {
        SpannableStringBuilder builder = new SpannableStringBuilder(fulltext);

        for (String textItem : textToBold) {
            if (textItem.length() > 0 && !textItem.trim().equals("")) {
                //for counting start/end indexes
                int startingIndex = fulltext.indexOf(textItem);
                int endingIndex = startingIndex + textItem.length();

                if (startingIndex >= 0 && endingIndex >= 0) {
                    builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
                }
            }
        }

        return builder;
    }

    /**
     * 색상 변경 함수
     *
     * @param context
     * @param fulltext    풀 텍스트
     * @param textToColor 변경할 텍스트
     * @return
     */
    public static SpannableStringBuilder getSectionOfTextColor(Context context, @ColorRes int colorResId, String fulltext, String... textToColor) {
        SpannableStringBuilder builder = new SpannableStringBuilder(fulltext);

        for (String textItem : textToColor) {
            if (textItem.length() > 0 && !textItem.trim().equals("")) {
                //for counting start/end indexes
                int startingIndex = fulltext.indexOf(textItem);
                int endingIndex = startingIndex + textItem.length();

                if (startingIndex >= 0 && endingIndex >= 0) {
                    builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorResId)), startingIndex, endingIndex, 0);
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

    /**
     * 프로세스를 죽인다.
     */
    public static void killMyProcess() {
        Process.killProcess(Process.myPid());
    }
}
