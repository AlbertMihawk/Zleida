//package com.zleidadr.ui;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.zleidadr.R;
//import com.zleidadr.Zleida;
//import com.zleidadr.api.Api;
//import com.zleidadr.common.Constant;
//import com.zleidadr.entity.AppointCount;
//import com.zleidadr.entity.Operator;
//import com.zleidadr.manager.LoginManager;
//
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class MainActivity extends Activity {
//    private static final String TAG = MainActivity.class.getName();
//
//    private static Boolean isExit = false;
//
//   @BindView(R.id.tv_left_small)
//    TextView mTvLeftSmall;
//   @BindView(R.id.iv_right)
//    ImageView mIvRight;
//   @BindView(R.id.tv_await)
//    TextView mTvAwait;
//   @BindView(R.id.tv_success)
//    TextView mTvSuccess;
//   @BindView(R.id.tv_doing)
//    TextView mTvDoing;
//   @BindView(R.id.tv_overdue)
//    TextView mTvOverdue;
//   @BindView(R.id.tv_total)
//    TextView mTvTotal;
//   @BindView(R.id.ll_appoint)
//    LinearLayout mLlAppoint;
//   @BindView(R.id.ll_draft)
//    LinearLayout mLlDraft;
//   @BindView(R.id.ll_receivable)
//    LinearLayout mLlReceivable;
//   @BindView(R.id.ll_stay)
//    LinearLayout mLlStay;
//
//    private Operator operator;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Zleida.getInstance().addActivity(this);
//        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
//        //FIXME delete
////        Intent test = new Intent();
////        test.setClass(getApplication(), TestActivity.class);
////        startActivity(test);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!LoginManager.getInstance().isLogin(getApplication())) {
//            Intent intent = new Intent();
//            intent.setClass(getApplication(), LoginActivity.class);
//            startActivity(intent);
//            return;
//        }
//        operator = LoginManager.getInstance().getOperator(this);
//        initView();
//        loadApi();
//    }
//
//    private void initView() {
//        mTvLeftSmall.setText("您好," + operator.getOperatorName());
//        mTvLeftSmall.setVisibility(View.VISIBLE);
//        mIvRight.setImageResource(R.drawable.icon_settings);
//        mIvRight.setVisibility(View.VISIBLE);
//        mIvRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivity(intent);
//            }
//        });
//        mLlAppoint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MyAppointActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        mLlDraft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, DraftActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        mLlReceivable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ReceivableActivity.class);
//                startActivity(intent);
//            }
//        });
//        mLlStay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, StayActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void loadApi() {
//        Api.getInstance().getProjectAppointCountApi(operator.getOperatorId(), new Api.Callback<List<AppointCount>>() {
//            @Override
//            public void onApiSuccess(List<AppointCount> result) {
//                mTvAwait.setText(0 + "");
//                mTvDoing.setText(0 + "");
//                mTvSuccess.setText(0 + "");
//                mTvOverdue.setText(0 + "");
//                mTvTotal.setText(0 + "");
//                if (result != null) {
//                    int total = 0;
//                    for (AppointCount count : result) {
//                        total += Integer.valueOf(count.getCount());
//                        switch (count.getStatusNum()) {
//                            case Constant.APPOINT_STATUS_AWAIT:
//                                mTvAwait.setText(count.getCount());
//                                break;
//                            case Constant.APPOINT_STATUS_DONE:
//                                mTvDoing.setText(count.getCount());
//                                break;
//                            case Constant.APPOINT_STATUS_SUCCESS:
//                                mTvSuccess.setText(count.getCount());
//                                break;
//                            case Constant.APPOINT_STATUS_OVERDUE:
//                                mTvOverdue.setText(count.getCount());
//                                break;
//                        }
//                    }
//                    mTvTotal.setText(total + "");
//                }
//            }
//
//            @Override
//            public void onApiFailed(String resultCode, String resultMessage) {
//
//            }
//        });
//    }
//
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            exitBy2Click();
//        }
//        return false;
//    }
//
//    /**
//     * 双击退出函数
//     */
//    private void exitBy2Click() {
//        Timer tExit = null;
//        if (isExit == false) {
//            isExit = true; // 准备退出
//            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//            tExit = new Timer();
//            tExit.schedule(
//                    new TimerTask() {
//                        @Override
//                        public void run() {
//                            isExit = false; // 取消退出
//                        }
//                    }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
//
//        } else {
//            Zleida.getInstance().exit();
//            // FIXME: 16/1/4 关闭方式
//            //System.exit(0);
//        }
//    }
//
//}
