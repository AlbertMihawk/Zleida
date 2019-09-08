package com.zleidadr.common;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiaoxuli on 16/1/20.
 */
public class ImageLocalLoader {

    private static ImageLocalLoader sImageLocalLoader;
    //    private HashMap<Integer, View> mHashmap = new HashMap<>();
    private ExecutorService pool = Executors.newFixedThreadPool(3);


    private ImageLocalLoader() {
    }

    public static synchronized ImageLocalLoader getInstance() {
        if (sImageLocalLoader == null) {
            sImageLocalLoader = new ImageLocalLoader();
        }
        return sImageLocalLoader;
    }

    public void setImage(ImageView imageView, int scaleWidth, String localImagePath, ImageLocalLoaderListener listener) {
        CompBitmapRunnable runnable = new CompBitmapRunnable();
        runnable.imageView = imageView;
        runnable.localImagePath = localImagePath;
        runnable.listener = listener;
        runnable.scaleWidth = scaleWidth;
        pool.execute(runnable);

    }

    public interface ImageLocalLoaderListener {
        void onSetListener(ImageView imageView, Bitmap bitmap);
    }

    class CompBitmapRunnable implements Runnable {
        ImageView imageView;
        String localImagePath;
        int scaleWidth;
        ImageLocalLoaderListener listener;
        private Bitmap bitmap;

        @Override
        public void run() {
            bitmap = CommonUtils.getBitmapByWidth(localImagePath, scaleWidth, 0);
            listener.onSetListener(imageView, bitmap);
        }
    }
}
