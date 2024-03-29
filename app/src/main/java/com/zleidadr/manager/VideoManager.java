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
public class VideoManager {
    private static final String TAG = VideoManager.class.getName();
    public static final int TAKE_VIDEO_RESULT = 200;
    public static final String PRENAME = "video_";
    public static final String EXTNAME = ".mp4";
    private static final int VIDEO_QUALITY_HIGH = 1;//照片质量low:0 ~ high:1
    private static final int VIDEO_QUALITY_LOW = 0;//照片质量low:0 ~ high:1

    private VideoManager() {
    }

    public static File takeVideo(Activity activity, String subDir) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, VIDEO_QUALITY_HIGH);
        // 录制视频最大时长15s
//        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
//        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 90);
        File dir = new File(CommonUtils.getReceivableDir(activity, subDir));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir + "/" + PRENAME +
                System.currentTimeMillis() + EXTNAME);
        Uri uri = Uri.fromFile(file);
        Logger.d(TAG, "uri: " + uri.getPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, TAKE_VIDEO_RESULT);
        return file;
    }

    public static void takeVideo2(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, VIDEO_QUALITY_LOW);
        // 录制视频最大时长15s
//        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
//        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 90);

        activity.startActivityForResult(intent, TAKE_VIDEO_RESULT);
    }

    public static LinkedList<File> getVideoFiles(Activity activity, String subDir) {
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


    public static void deleteVideoFile(Integer[] nums, Activity activity, String subDir) {
        LinkedList<File> list = getVideoFiles(activity, subDir);
        for (Integer num : nums) {
            list.get(num).delete();
        }
    }
}
