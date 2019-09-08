package com.zleidadr.manager;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.zleidadr.common.CommonUtils;
import com.zleidadr.entity.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class VideoManager {

    public static LinkedList<File> getVideoFiles(Activity activity, String subDir) {
        File dir = new File(CommonUtils.getReceivableDir(activity, subDir));
        LinkedList<File> list = null;
        if (dir.exists() && dir.isDirectory()) {
            list = new LinkedList<>();
            for (File file : dir.listFiles()) {
//                if (file.getName().contains(PRENAME)) {
//                    list.add(file);
//                }
            }
        }
        return list;
    }

    /**
     * 获取本地所有的视频
     *
     * @return list
     */
    public static List<Material> getAllLocalVideos(Context context, int uid) {
        String[] projection = {
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        //全部图片
        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=? or "
                + MediaStore.Video.Media.MIME_TYPE + "=?";
        String[] whereArgs = {"video/mp4", "video/3gp", "video/aiv", "video/rmvb", "video/vob", "video/flv",
                "video/mkv", "video/mov", "video/mpg"};
        List<Material> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, where, whereArgs, MediaStore.Video.Media.DATE_ADDED + " DESC ");
        if (cursor == null) {
            return list;
        }
        try {
            while (cursor.moveToNext()) {
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                if (size < 600 * 1024 * 1024) {//<600M
                    Material materialBean = new Material();
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                    long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                    materialBean.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                    materialBean.setLogo(path);
                    materialBean.setFilePath(path);
                    materialBean.setChecked(false);
                    materialBean.setFileType(2);
                    materialBean.setUploadedSize(0);
                    materialBean.setTimeStamps(System.currentTimeMillis() + "");
//                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//                    format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
//                    String t = format.format(duration);
//                    materialBean.setTime(context.getString(R.string.video_len) + t);
                    materialBean.setFileSize(size);
                    list.add(materialBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }


    /**
     * 获取手机中所有视频的信息
     */
//    private void getAllVideoInfos(final Context context){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HashMap<String,List<MediaBean>> allPhotosTemp = new HashMap<>();//所有照片
//                Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                String[] proj = { MediaStore.Video.Thumbnails._ID
//                        , MediaStore.Video.Thumbnails.DATA
//                        ,MediaStore.Video.Media.DURATION
//                        ,MediaStore.Video.Media.SIZE
//                        ,MediaStore.Video.Media.DISPLAY_NAME
//                        ,MediaStore.Video.Media.DATE_MODIFIED};
//                Cursor mCursor = context.getContentResolver().query(mImageUri,
//                        proj,
//                        MediaStore.Video.Media.MIME_TYPE + "=?",
//                        new String[]{"video/mp4"},
//                        MediaStore.Video.Media.DATE_MODIFIED+" desc");
//                if(mCursor!=null){
//                    while (mCursor.moveToNext()) {
//                        // 获取视频的路径
//                        int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
//                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
//                        int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
//                        long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE))/1024; //单位kb
//                        if(size<0){
//                            //某些设备获取size<0，直接计算
//                            Log.e("dml","this video size < 0 " + path);
//                            size = new File(path).length()/1024;
//                        }
//                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
//                        long modifyTime = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));//暂未用到
//
//                        //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
//                        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
//                        String[] projection = { MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
//                        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
//                                , projection
//                                , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
//                                , new String[]{videoId+""}
//                                , null);
//                        String thumbPath = "";
//                        while (cursor.moveToNext()){
//                            thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
//                        }
//                        cursor.close();
//                        // 获取该视频的父路径名
//                        String dirPath = new File(path).getParentFile().getAbsolutePath();
//                        //存储对应关系
//                        if (allPhotosTemp.containsKey(dirPath)) {
//                            List<MediaBean> data = allPhotosTemp.get(dirPath);
//                            data.add(new MediaBean(MediaBean.Type.Video,path,thumbPath,duration,size,displayName));
//                            continue;
//                        } else {
//                            List<MediaBean> data = new ArrayList<>();
//                            data.add(new MediaBean(MediaBean.Type.Video,path,thumbPath,duration,size,displayName));
//                            allPhotosTemp.put(dirPath,data);
//                        }
//                    }
//                    mCursor.close();
//                }
//                //更新界面
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //...
//                    }
//                });
//            }
//        }).start();
//    }
}
