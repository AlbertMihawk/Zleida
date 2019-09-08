package com.zleidadr;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.location.service.LocationService;
import com.orm.SugarApp;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zleidadr.api.Api;
import com.zleidadr.entity.Dict;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by xiaoxuli on 16/1/4.
 */
public class Zleida extends SugarApp {

    public static final boolean DEBUG = true;
    private static final String TAG = Zleida.class.getName();
    public static Map<String, List<Dict>> sDictMap;
    //    public static int sWindowWidth;
//    public static int sWindowHeight;
    public static File sCurrentPhotoFile;
    public LocationService mLocationService;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationService = new LocationService(getApplicationContext());
        Api.init(getApplicationContext());
        Api.getInstance().getDictMapApi(new Api.Callback<Map<String, List<Dict>>>() {
            @Override
            public void onApiSuccess(Map<String, List<Dict>> result) {
                sDictMap = result;
            }

            @Override
            public void onApiFailed(String resultCode, String resultMessage) {
                //TODO 读取本地设置
            }
        });
    }

    //判断android 版本然后设置Systembar颜色
    public static void initSystemBar(Activity activity) {

        Window window = activity.getWindow();
        //4.4版本及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //5.0版本及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                   /* | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION*/);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/
                    /*| View.SYSTEM_UI_FLAG_LAYOUT_STABLE*/);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        //开启SystemBarTint
        tintManager.setStatusBarTintEnabled(true);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private List<Activity> mList = new LinkedList<>();
    private static Zleida instance;

    public Zleida() {
    }

    public synchronized static Zleida getInstance() {
        if (null == instance) {
            instance = new Zleida();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
