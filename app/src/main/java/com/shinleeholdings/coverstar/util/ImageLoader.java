package com.shinleeholdings.coverstar.util;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.shinleeholdings.coverstar.MyApplication;
import com.shinleeholdings.coverstar.R;

public class ImageLoader {

    public static void loadImage(ImageView target, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        RequestBuilder<Drawable> builder = Glide.with(MyApplication.getContext()).load(path);
        builder.into(target);
    }

    public static void loadUserImage(ImageView target, String path) {
        if (TextUtils.isEmpty(path)) {
            target.setImageResource(R.drawable.avator_woman);
            return;
        }

        RequestBuilder<Drawable> builder = Glide.with(MyApplication.getContext()).load(path);
        builder.error(R.drawable.avator_woman);
        builder.into(target);
    }
}
