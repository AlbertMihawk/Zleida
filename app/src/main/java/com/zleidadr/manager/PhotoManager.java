package com.zleidadr.manager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.zleidadr.common.CommonUtils;
import com.zleidadr.common.Logger;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by xiaoxuli on 16/1/12.
 */
public class PhotoManager {
    public static final int TAKE_PHOTO_RESULT = 100;
    public static final String PRENAME = "pic_";
    public static final String EXTNAME = ".jpg";
    private static final String TAG = PhotoManager.class.getName();
    private static final int PHOTO_QUALITY = 0;//照片质量low:0 ~ high:1

    private PhotoManager() {
    }

    public static File takePhoto(Activity activity, String subDir) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, PHOTO_QUALITY);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 90);
        File dir = new File(CommonUtils.getReceivableDir(activity, subDir));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir + "/" + PRENAME +
                System.currentTimeMillis() + EXTNAME);
        Uri uri = Uri.fromFile(file);
        Logger.d(TAG, "uri: " + uri.getPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, TAKE_PHOTO_RESULT);
        return file;
    }

    public static LinkedList<File> getPhotoFiles(Activity activity, String subDir) {
        File dir = new File(CommonUtils.getReceivableDir(activity, subDir));
        LinkedList<File> list = null;
        if (dir.exists() && dir.isDirectory()) {
            list = new LinkedList<>();
            for (File file : dir.listFiles()) {
                if (file.getName().contains(PRENAME)) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public static void deletePhotoFile(Integer[] nums, Activity activity, String subDir) {
        LinkedList<File> list = getPhotoFiles(activity, subDir);
        for (Integer num : nums) {
            list.get(num).delete();
        }
    }
}
