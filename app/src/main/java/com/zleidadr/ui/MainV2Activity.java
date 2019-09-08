package com.zleidadr.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zleidadr.R;
import com.zleidadr.Zleida;
import com.zleidadr.api.Api;
import com.zleidadr.common.Constant;
import com.zleidadr.entity.AppointCount;
import com.zleidadr.entity.Banner;
import com.zleidadr.entity.Operator;
import com.zleidadr.manager.LoginManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;

import static com.zleidadr.ui.WebViewWithTitlebarActivity.BUNDLE_TITLE;

public class MainV2Activity extends Activity implements ViewPager.OnPageChangeListener, OnPageClickListener {
    private static final String TAG = MainV2Activity.class.getName();

    private static Boolean isExit = false;


    @Bind(R.id.tv_await)
    TextView mTvAwait;
    @Bind(R.id.tv_success)
    TextView mTvSuccess;
    @Bind(R.id.tv_doing)
    TextView mTvDoing;
    @Bind(R.id.tv_overdue)
    TextView mTvOverdue;
    @Bind(R.id.tv_total)
    TextView mTvTotal;
    @Bind(R.id.ll_appoint)
    LinearLayout mLlAppoint;
    @Bind(R.id.ll_draft)
    LinearLayout mLlDraft;
    @Bind(R.id.ll_receivable)
    LinearLayout mLlReceivable;
    @Bind(R.id.ll_stay)
    LinearLayout mLlStay;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    @Bind(R.id.iv_right)
    ImageView mIvRight;
    @Bind(R.id.iv_appoint)
    ImageView mIvAppoint;
    @Bind(R.id.tv_appoint)
    TextView mTvAppoint;
    @Bind(R.id.iv_draft)
    ImageView mIvDraft;
    @Bind(R.id.tv_draft)
    TextView mTvDraft;
    @Bind(R.id.iv_stay)
    ImageView mIvStay;
    @Bind(R.id.tv_stay)
    TextView mTvStay;
    @Bind(R.id.iv_receivable)
    ImageView mIvReceivable;
    @Bind(R.id.tv_receivable)
    TextView mTvReceivable;
    @Bind(R.id.iv_discovery)
    ImageView mIvDiscovery;
    @Bind(R.id.tv_discovery)
    TextView mTvDiscovery;
    @Bind(R.id.iv_quan1)
    ImageView mIvQuan1;
    @Bind(R.id.iv_quan2)
    ImageView mIvQuan2;
    @Bind(R.id.iv_quan3)
    ImageView mIvQuan3;
    @Bind(R.id.iv_quan4)
    ImageView mIvQuan4;
    @Bind(R.id.rl_quan)
    RelativeLayout mRlQuan;
    private InfiniteIndicator mAnimCircleIndicator;
    private ArrayList<Banner> mBanners;
    private Operator operator;
    private ArrayList<Page> pageViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Zleida.getInstance().addActivity(this);
        setContentView(R.layout.activity_main_v2);
        ButterKnife.bind(this);
//        Zleida.initSystemBar(MainV2Activity.this);
        //FIXME delete
//        Intent test = new Intent();
//        test.setClass(getApplication(), TestActivity.class);
//        startActivity(test);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!LoginManager.getInstance().isLogin(getApplication())) {
            Intent intent = new Intent();
            intent.setClass(getApplication(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        operator = LoginManager.getInstance().getOperator(this);

//        final TextView tv = (TextView) findViewById(R.id.temp);
//        tv.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                tv.setVisibility(View.VISIBLE);
//            }
//        }, 300);

        initView();
        loadApi();

    }

    private void loadIndicator() {
        IndicatorConfiguration configuration;

        if (mBanners.size() > 1) {
            configuration = new IndicatorConfiguration.Builder()
                    .imageLoader(new GlideLoader())
                    .isStopWhileTouch(true)
                    .onPageChangeListener(this)
                    .onPageClickListener(this)
                    .scrollDurationFactor(3.0)//滚速
                    .direction(IndicatorConfiguration.RIGHT)
                    .internal(5000)
                    .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                    .isDrawIndicator(true)
                    .build();
        } else if (mBanners.size() == 1) {
            configuration = new IndicatorConfiguration.Builder()
                    .imageLoader(new GlideLoader())
                    .isStopWhileTouch(true)
                    .onPageChangeListener(this)
                    .onPageClickListener(this)
                    .scrollDurationFactor(3.0)//滚速
                    .internal(5000)
                    .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                    .isDrawIndicator(true)
                    .build();
        } else {
            return;
        }

        mAnimCircleIndicator.init(configuration);
        mAnimCircleIndicator = (InfiniteIndicator) findViewById(R.id.defaultIndicator);
        mAnimCircleIndicator.notifyDataChange(pageViews);
        mAnimCircleIndicator.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAnimCircleIndicator != null) {
            mAnimCircleIndicator.stop();
        }
        mIvQuan1.clearAnimation();
        mIvQuan2.clearAnimation();
        mIvQuan3.clearAnimation();
        mIvQuan4.clearAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void initView() {
        mTvRight.setText("您好，" + operator.getOperatorName());
        mTvRight.setVisibility(View.VISIBLE);
        mIvRight.setImageResource(R.drawable.icon_settings);
        mIvRight.setVisibility(View.VISIBLE);

        mAnimCircleIndicator = (InfiniteIndicator) findViewById(R.id.defaultIndicator);
        Point point = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(point);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(point.x, ((int) (point.x / 720.0 * 290.0)));
        mAnimCircleIndicator.setLayoutParams(params);

        int duraiton = 600;
        int offset = 50;
        int degree1 = 40;
        int degree2 = 160;
        int degree3 = 260;
        int degree4 = 385;

        final RotateAnimation anim1 = new RotateAnimation(0, 40, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim1.setInterpolator(new DecelerateInterpolator());
        anim1.setStartOffset(offset);
        anim1.setFillAfter(true);
        anim1.setDuration(duraiton);
//        anim1.setDuration(degree1*3);

        final RotateAnimation anim2 = new RotateAnimation(0, 160, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim2.setInterpolator(new DecelerateInterpolator());
        anim2.setStartOffset(offset);
        anim2.setFillAfter(true);
        anim2.setDuration(duraiton);
//        anim2.setDuration(degree2*3);

        final RotateAnimation anim3 = new RotateAnimation(0, 260, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim3.setInterpolator(new DecelerateInterpolator());
        anim3.setStartOffset(offset);
        anim3.setFillAfter(true);
        anim3.setDuration(duraiton);
//        anim3.setDuration(degree3*3);

        final RotateAnimation anim4 = new RotateAnimation(0, 385, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim4.setInterpolator(new DecelerateInterpolator());
        anim4.setStartOffset(offset);
        anim4.setFillAfter(true);
        anim4.setDuration(duraiton);
//        anim4.setDuration(degree4*3);

        mIvQuan4.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIvQuan1.startAnimation(anim1);
                mIvQuan2.startAnimation(anim2);
                mIvQuan3.startAnimation(anim3);
                mIvQuan4.startAnimation(anim4);

            }
        }, 1000);


    }

    @OnClick({R.id.iv_right, R.id.ll_appoint, R.id.ll_draft, R.id.ll_stay, R.id.ll_receivable, R.id.ll_discovery})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_right:
                intent = new Intent(MainV2Activity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_appoint:
                intent = new Intent(MainV2Activity.this, MyAppointActivity.class);
                startActivity(intent);

                break;
            case R.id.ll_draft:
                intent = new Intent(MainV2Activity.this, DraftActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_stay:
                intent = new Intent(MainV2Activity.this, StayActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_receivable:
                intent = new Intent(MainV2Activity.this, ReceivableActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_discovery:
                intent = new Intent(this, WebViewWithTitlebarActivity.class);
                intent.putExtra(BUNDLE_TITLE, "发现");
                intent.putExtra(WebViewWithTitlebarActivity.BUNDLE_WEB_URL, Api.HOST + "/eventList");
                startActivity(intent);
                break;
        }
    }


    private void loadApi() {

        Api.getInstance().getBannerApi(new Api.Callback<ArrayList<Banner>>() {

            @Override
            public void onApiSuccess(ArrayList<Banner> result) {
                mBanners = result;
                pageViews = new ArrayList<>();
                for (Banner banner : result) {
//                    pageViews.add(new Page(" ", R.drawable.icon_banner, MainV2Activity.this));
//                    pageViews.add(new Page(" ", "http://www.moto2s.com/motofile/news1/201211610295263520.jpg", MainV2Activity.this));
                    String pic = Api.getImageUrl(banner.getBaseURL(), banner.getImg());
                    pageViews.add(new Page(" ", pic, MainV2Activity.this));
                }
                loadIndicator();
            }

            @Override
            public void onApiFailed(String resultCode, String resultMessage) {

            }
        });

        Api.getInstance().getProjectAppointCountApi(operator.getOperatorId(), new Api.Callback<List<AppointCount>>() {
            @Override
            public void onApiSuccess(List<AppointCount> result) {
                mTvAwait.setText(0 + "");
                mTvDoing.setText(0 + "");
                mTvSuccess.setText(0 + "");
                mTvOverdue.setText(0 + "");
                mTvTotal.setText(0 + "");
                if (result != null) {
                    int total = 0;
                    for (AppointCount count : result) {
                        total += Integer.valueOf(count.getCount());
                        switch (count.getStatusNum()) {
                            case Constant.APPOINT_STATUS_AWAIT:
                                mTvAwait.setText(count.getCount());
                                break;
                            case Constant.APPOINT_STATUS_DONE:
                                mTvDoing.setText(count.getCount());
                                break;
                            case Constant.APPOINT_STATUS_SUCCESS:
                                mTvSuccess.setText(count.getCount());
                                break;
                            case Constant.APPOINT_STATUS_OVERDUE:
                                mTvOverdue.setText(count.getCount());
                                break;
                        }
                    }
                    mTvTotal.setText(total + "");
                }
            }

            @Override
            public void onApiFailed(String resultCode, String resultMessage) {

            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            isExit = false; // 取消退出
                        }
                    }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            Zleida.getInstance().exit();
            // FIXME: 16/1/4 关闭方式
            //System.exit(0);
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageClick(int position, Page page) {
        Intent intent = new Intent(this, WebViewWithTitlebarActivity.class);
        intent.putExtra(BUNDLE_TITLE, mBanners.get(position).getTemplateName());
        intent.putExtra(WebViewWithTitlebarActivity.BUNDLE_WEB_URL, mBanners.get(position).getAccessURL());
        startActivity(intent);
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
