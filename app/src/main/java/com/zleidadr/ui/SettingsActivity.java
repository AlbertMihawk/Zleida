package com.zleidadr.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zleidadr.R;
import com.zleidadr.manager.DataCleanManager;
import com.zleidadr.manager.LoginManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

   @BindView(R.id.tv_back)
    FrameLayout mTvBack;
   @BindView(R.id.tv_title)
    TextView mTvTitle;
   @BindView(R.id.tv_total_cache)
    TextView mTvTotalCache;
   @BindView(R.id.tv_version_code)
    TextView mTvVersionCode;

    private AlertDialog mDialog;
    private AlertDialog.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        } else if (mDialog != null) {
            mDialog = null;
        }
    }

    private void initView() {
        mTvBack.setVisibility(View.VISIBLE);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText("设置");
        try {
            mTvTotalCache.setText(DataCleanManager.getTotalCacheSize(this));
            mTvVersionCode.setText(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBuilder = new AlertDialog.Builder(SettingsActivity.this);
    }

    @OnClick({R.id.ll_clean, R.id.ll_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_clean:
                mDialog = mBuilder.setMessage("确定清除缓存吗").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                        DataCleanManager.clearAllCache(SettingsActivity.this);
                        try {
                            mTvTotalCache.setText(DataCleanManager.getTotalCacheSize(SettingsActivity.this));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).create();
                mDialog.show();
                break;
            case R.id.ll_logout:
                mDialog = mBuilder.setMessage("确定退出登录吗?").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                        LoginManager.getInstance().logout(SettingsActivity.this);
                        Intent intent = new Intent();
                        intent.setClass(SettingsActivity.this, MainV2Activity.class);
                        startActivity(intent);
                    }
                }).create();
                mDialog.show();
                break;
        }
    }
}
