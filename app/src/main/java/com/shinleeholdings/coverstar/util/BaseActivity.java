package com.shinleeholdings.coverstar.util;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

}