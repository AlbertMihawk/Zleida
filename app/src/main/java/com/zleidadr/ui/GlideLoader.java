package com.zleidadr.ui;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.toolbox.Volley;
import com.zleidadr.R;
import com.zleidadr.net.BitmapCache;

import cn.lightsky.infiniteindicator.ImageLoader;

/**
 * Created by xiaoxuli on 2017/1/12.
 */

public class GlideLoader implements ImageLoader {

    public void initLoader(Context context) {

    }

    @Override
    public void load(Context context, ImageView targetView, Object res) {

        targetView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        targetView.setImageResource(R.drawable.icon_banner_def);

        com.android.volley.toolbox.ImageLoader.ImageListener listener = com.android.volley.toolbox.ImageLoader.getImageListener(targetView, R.drawable.icon_banner_def, R.drawable.icon_banner_def);
        targetView.setImageBitmap(new com.android.volley.toolbox.ImageLoader(Volley.newRequestQueue(context), new BitmapCache())
                .get((String) res, listener).getBitmap());

//        if (res instanceof String) {
//            Glide.with(context)
//                    .load((String) res)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.icon_banner_def)
//                    .fitCenter()
//                    .crossFade()
//                    .into(targetView);
//        }
    }
}