package com.zleidadr.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.zleidadr.manager.LoginManager;

import java.io.InputStream;

/**
 * Created by xiaoxuli on 16/1/12.
 */
public class CommonUtils {

    /**
     * @param context
     * @param subDir
     * @return 目录结构: $FilesDir/$OperatorId/$LocalId/
     */
    public static String getReceivableDir(Context context, String subDir) {
        return context.getExternalCacheDir() + "/" +
                LoginManager.getInstance().getOperator(context).getOperatorId() + "/" + subDir + "/";
    }

    /**
     * 读取本地的json数据
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String readLocalJson(Context context, String fileName) {
        String resultString = "";
        try {
            InputStream inputStream = context.getResources().getAssets().open(fileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            resultString = new String(buffer, "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        return resultString;
    }

    public static Bitmap getBitmapByWidth(String localImagePath, int width,
                                          int addedScaling) {
        if (TextUtils.isEmpty(localImagePath)) {
            return null;
        }
        Bitmap temBitmap = null;

        try {
            BitmapFactory.Options outOptions = new BitmapFactory.Options();

            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;

            // 加载获取图片的宽高
            temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);

            int height = outOptions.outHeight;

            if (outOptions.outWidth > width) {
                // 根据宽设置缩放比例
                outOptions.inSampleSize = outOptions.outWidth / width + 1
                        + addedScaling;
                outOptions.outWidth = width;

                // 计算缩放后的高度
                height = outOptions.outHeight / outOptions.inSampleSize;
                outOptions.outHeight = height;
            }

            // 重新设置该属性为false，加载图片返回
            outOptions.inJustDecodeBounds = false;
            outOptions.inPurgeable = true;
            outOptions.inInputShareable = true;
            temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return temBitmap;
    }

    public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

//    //TODO 临时变量,后期用DB替换
//    public static int getLocalId(Context context) {
//        SharedPreferences sp =
//                context.getSharedPreferences("local_params", Context.MODE_PRIVATE);
//        int localId = sp.getInt("local_id", 1);
//        localId++;
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("local_id", localId);
//        editor.apply();
//        return localId;
//    }

    //TODO 临时更新数据,用DB替换
//    public static void removeReceivableReq(ReceivableReq currentReceivableReq, List<File> files, Integer[] nums) {
//        ArrayList<Resource> list = (ArrayList<Resource>) currentReceivableReq.getResourcesList();
//        Iterator<Resource> iter = list.iterator();
//        while (iter.hasNext()) {
//            Resource res = iter.next();
//            for (Integer num : nums) {
//                if (files.get(num).getName().equals(res.getResourceOriginal())) {
//                    iter.remove();
//                    break;
//                }
//            }
//        }
//    }
//
//    //TODO 临时更新数据,用DB替换
//    public static void removeReceivableReq(ReceivableReq currentReceivableReq, String fileName) {
//        ArrayList<Resource> list = (ArrayList<Resource>) currentReceivableReq.getResourcesList();
//        Iterator<Resource> iter = list.iterator();
//        while (iter.hasNext()) {
//            Resource res = iter.next();
//            if (fileName.equals(res.getResourceOriginal())) {
//                iter.remove();
//                break;
//            }
//        }
//    }
}
