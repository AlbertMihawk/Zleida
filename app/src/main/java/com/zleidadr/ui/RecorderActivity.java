package com.zleidadr.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zleidadr.R;
import com.zleidadr.common.Constant;
import com.zleidadr.common.TimeClock;
import com.zleidadr.entity.Resource;
import com.zleidadr.manager.AudioManager;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecorderActivity extends LocationBaseActivity {

    @Bind(R.id.tv_back)
    FrameLayout mTvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_record_time)
    TextView mTvRecordTime;
    @Bind(R.id.iv_cancel)
    ImageView mIvCancel;
    @Bind(R.id.iv_record_stop)
    ImageView mIvRecordStop;
    @Bind(R.id.iv_pause_resume)
    ImageView mIvPauseResume;
    @Bind(R.id.iv_right)
    ImageView mIvRight;
    @Bind(R.id.fl_right)
    FrameLayout mFlRight;
    private AlertDialog mDialog;
    private AudioManager mAudioManager;
    private String mLocalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        ButterKnife.bind(this);
        mLocalId = getIntent().getStringExtra(Constant.BUNDLE_STR_LOCALID);
        AudioManager.init(RecorderActivity.this, mLocalId);
        mAudioManager = AudioManager.getInstance();
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        } else if (mDialog != null) {
            mDialog = null;
        }
    }

    private void stopRecorde() {
        if (mAudioManager.getRecordeStatus() == AudioManager.RECORDER_RECORD ||
                mAudioManager.getRecordeStatus() == AudioManager.RECORDER_PAUSE) {
            mAudioManager.stopRecording(new AudioManager.OnAudioManagerListener() {
                @Override
                public void onSuccessListener() {
                    mIvRecordStop.setImageResource(R.drawable.icon_record);
                    mIvPauseResume.setImageResource(R.drawable.icon_pause);
                    mIvCancel.setVisibility(View.INVISIBLE);
                    mIvPauseResume.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void initView() {
        mTvBack.setVisibility(View.VISIBLE);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecorde();
                onBackPressed();
            }
        });
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText("录音");
        mIvRight.setVisibility(View.VISIBLE);
        mIvRight.setImageResource(R.drawable.icon_list);
        mFlRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecorde();
                Intent intent = new Intent();
                intent.putExtra(Constant.BUNDLE_STR_LOCALID, mLocalId);
                intent.setClass(RecorderActivity.this, AudioActivity.class);
                startActivity(intent);
            }
        });
        mAudioManager.getTimeClock().setOnTimeClockListener(new TimeClock.OnTimeClockListener() {
            @Override
            public void onTimeChange(final int minute, final int second) {
                RecorderActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (minute == 20) {
                            mAudioManager.stopRecording(new AudioManager.OnAudioManagerListener() {
                                @Override
                                public void onSuccessListener() {
                                    mIvRecordStop.setImageResource(R.drawable.icon_record);
                                    mIvPauseResume.setImageResource(R.drawable.icon_pause);
                                    mIvCancel.setVisibility(View.INVISIBLE);
                                    mIvPauseResume.setVisibility(View.INVISIBLE);
                                    saveReceivableReq();
                                }
                            });
                        }
                        String timer = String.format("%02d", minute) + ":" + String.format("%02d", second);
                        mTvRecordTime.setText(timer);
                    }
                });
            }
        });
    }

    @OnClick({R.id.iv_cancel, R.id.iv_record_stop, R.id.iv_pause_resume})
    public void onClick(ImageView imageView) {
        switch (imageView.getId()) {
            case R.id.iv_cancel:
                AlertDialog.Builder builder = new AlertDialog.Builder(RecorderActivity.this);
                mDialog = builder.setMessage("确定要删除此录音吗?").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                        mAudioManager.cancelRecording(new AudioManager.OnAudioManagerListener() {
                            @Override
                            public void onSuccessListener() {
                                mIvRecordStop.setImageResource(R.drawable.icon_record);
                                mIvPauseResume.setImageResource(R.drawable.icon_pause);
                                mIvCancel.setVisibility(View.INVISIBLE);
                                mIvPauseResume.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }).create();
                mDialog.show();
                break;
            case R.id.iv_record_stop:
                if (mAudioManager.getRecordeStatus() == AudioManager.RECORDER_INIT) {
                    mAudioManager.startRecording(new AudioManager.OnAudioManagerListener() {
                        @Override
                        public void onSuccessListener() {
                            mIvCancel.setVisibility(View.VISIBLE);
                            mIvPauseResume.setVisibility(View.VISIBLE);
                            mIvPauseResume.setImageResource(R.drawable.icon_pause);
                            mIvRecordStop.setImageResource(R.drawable.icon_stop);
                        }
                    });
                } else if (mAudioManager.getRecordeStatus() == AudioManager.RECORDER_RECORD ||
                        mAudioManager.getRecordeStatus() == AudioManager.RECORDER_PAUSE) {
                    mAudioManager.stopRecording(new AudioManager.OnAudioManagerListener() {
                        @Override
                        public void onSuccessListener() {
                            mIvRecordStop.setImageResource(R.drawable.icon_record);
                            mIvPauseResume.setImageResource(R.drawable.icon_pause);
                            mIvCancel.setVisibility(View.INVISIBLE);
                            mIvPauseResume.setVisibility(View.INVISIBLE);
                            saveReceivableReq();
                        }
                    });
                }
                break;
            case R.id.iv_pause_resume:
                if (mAudioManager.getRecordeStatus() == AudioManager.RECORDER_RECORD) {
                    mAudioManager.pauseRecording(new AudioManager.OnAudioManagerListener() {
                        @Override
                        public void onSuccessListener() {
                            mIvPauseResume.setImageResource(R.drawable.icon_record_s);
                        }
                    });
                } else if (mAudioManager.getRecordeStatus() == AudioManager.RECORDER_PAUSE) {
                    mAudioManager.resumeRecording(new AudioManager.OnAudioManagerListener() {
                        @Override
                        public void onSuccessListener() {
                            mIvPauseResume.setImageResource(R.drawable.icon_pause);
                        }
                    });
                }
                break;
        }
    }

    private void saveReceivableReq() {
        Resource resource = new Resource();
        resource.setLocalId(mLocalId);
        resource.setLatitude(mBdLocation.getLatitude() + "");
        resource.setLongitude(mBdLocation.getLongitude() + "");
        resource.setLocation(mBdLocation.getAddrStr());
        resource.setProvince(mBdLocation.getProvince());
        resource.setCity(mBdLocation.getCity());
        resource.setDistrict(mBdLocation.getDistrict());
        resource.setStreet(mBdLocation.getStreet());
        if (TextUtils.isEmpty(resource.getLocation())) {
//            Toast.makeText(RecorderActivity.this, "定位信息获取失败", Toast.LENGTH_SHORT).show();
        }
        resource.setResourceOriginal(new File(mAudioManager.getRecordingFile()).getName());
        resource.setResourceType(Constant.RESOURCE_AUDIO);
        resource.save();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopRecorde();
        }
        return super.onKeyDown(keyCode, event);
    }
}

