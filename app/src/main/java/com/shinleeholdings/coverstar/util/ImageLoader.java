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

        RequestBuilder<Drawable> builder = Glide.with(MyApplication.getContext()).load(path);
        builder.into(target);
    }
}
