package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;

import java.util.Locale;

public class LanguageHelper {
    // https://medium.com/@gunhan/change-language-programmatically-in-android-69d4756c7d79

    public static final String LANGUAGE_KR = "kr";
    public static final String LANGUAGE_EN = "en";

    public static Context onAttach(Context context) {
        return setLocale(context, getSavedLanguage(context));
    }

    private static Context setLocale(Context context, String language) {
        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    private static String getSavedLanguage(Context context) {
        String language = SharedPreferenceHelper.getInstance(context).getStringPreference(SharedPreferenceHelper.LANGUAGE);
        if (TextUtils.isEmpty(language)) {
            language = LANGUAGE_KR;
        }

        return language;
    }

    public static void changeApplicationLanguage(Context context) {
        Locale locale = new Locale(getSavedLanguage(context));
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        context.getResources().updateConfiguration(configuration, resources.getDisplayMetrics());
        MyApplication.getContext().getResources().updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static void showLanguageSelectDialog(final Activity activity) {
        String[] language = new String[] {activity.getString(R.string.korean), activity.getString(R.string.english)};
//        DialogHelper.getInstance().showMaterialSelectDialog(activity, activity.getString(R.string.select_language), language, new MaterialDialog.ListCallback() {
//            @Override
//            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                String selectedLanguage = "";
//                if (which == 0) {
//                    // 한국어
//                    selectedLanguage = LANGUAGE_KR;
//                } else if (which == 1) {
//                    // 영어
//                    selectedLanguage = LANGUAGE_EN;
//                }
//
//                String savedLanguage = PreferenceHelper.getInstance(CoconutApplication.getContext()).getString(AppConstants.PREF_KEY.LANGUAGE);
//                if (TextUtils.isEmpty(savedLanguage) == false && savedLanguage.equals(selectedLanguage)) {
//                    return;
//                }
//
//                Bundle bundle = new Bundle();
//                bundle.putString(AppConstants.PREF_KEY.LANGUAGE, selectedLanguage);
//                PreferenceHelper.getInstance(CoconutApplication.getContext()).save(bundle);
//
//                Intent intent = activity.getIntent();
//                activity.finish();
//                CoconutApplication.getContext().startActivity(intent);
//            }
//        });
    }

}
