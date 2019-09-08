package com.zleidadr.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zleidadr.R;
import com.zleidadr.Zleida;
import com.zleidadr.api.Api;
import com.zleidadr.common.Logger;
import com.zleidadr.entity.Operator;
import com.zleidadr.manager.LoginManager;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getName();

    private static Boolean isExit = false;

    @Bind(R.id.et_username)
    EditText mEtUsername;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.tv_loading_text)
    TextView mTvLoadingText;
    @Bind(R.id.inc_loading)
    LinearLayout mIncLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Zleida.getInstance().addActivity(this);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvLoadingText.setText("登录中");
    }

    @OnClick(R.id.btn_login)
    public void login(View view) {
        mIncLoading.setVisibility(View.VISIBLE);
        Api.getInstance().login(
                mEtUsername.getText().toString(), mEtPassword.getText().toString(),
                new Api.Callback<Operator>() {
                    @Override
                    public void onApiSuccess(Operator result) {
                        mIncLoading.setVisibility(View.GONE);
                        Logger.d(TAG, "result: " + result.toString());
                        if (!LoginManager.getInstance().login(LoginActivity.this, result)) {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }

                    @Override
                    public void onApiFailed(String resultCode, String resultMsg) {
                        Logger.d(TAG, "resultCode: " + resultCode + "---resultMsg: " + resultMsg);
                        mIncLoading.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, resultMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
            return true;
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
            System.exit(0);
        }
    }

}
