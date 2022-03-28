package com.shinleeholdings.coverstar.util;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.shinleeholdings.coverstar.MyApplication;

public class ImageLoader {

    public static void loadImage(ImageView target, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        String url = getFullImageUrl(path);
        if (TextUtils.isEmpty(url)) {
            return;
        }

        RequestBuilder<Drawable> builder = Glide.with(MyApplication.getContext()).load(url);
        builder.into(target);
    }

    public static String getFullImageUrl(String path) {
        // TODO 경로 설정
        return path;
    }
}
