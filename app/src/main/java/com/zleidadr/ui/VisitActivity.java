package com.zleidadr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zleidadr.R;
import com.zleidadr.api.Api;
import com.zleidadr.common.CommonUtils;
import com.zleidadr.common.Constant;
import com.zleidadr.entity.ReceivableReq;
import com.zleidadr.entity.Resource;
import com.zleidadr.manager.AudioManager;
import com.zleidadr.manager.DataCleanManager;
import com.zleidadr.manager.LoginManager;
import com.zleidadr.manager.PhotoManager;
import com.zleidadr.sugarDb.DbManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VisitActivity extends Activity {

   @BindView(R.id.tv_back)
    FrameLayout mTvBack;
   @BindView(R.id.tv_title)
    TextView mTvTitle;
   @BindView(R.id.tv_address)
    TextView mTvAddress;
   @BindView(R.id.tv_authorized_agency)
    TextView mTvAuthorizedAgency;
   @BindView(R.id.tv_loanee_name)
    TextView mTvLoaneeName;
   @BindView(R.id.tv_authorize_amount)
    TextView mTvAuthorizeAmount;
   @BindView(R.id.tv_overdue_date)
    TextView mTvOverdueDate;
   @BindView(R.id.tv_camera_status)
    TextView mTvCameraStatus;
   @BindView(R.id.tv_recorder_status)
    TextView mTvRecorderStatus;
   @BindView(R.id.tv_note_status)
    TextView mTvNoteStatus;
   @BindView(R.id.btn_submit_box)
    Button mBtnSubmit;
   @BindView(R.id.btn_upload)
    Button mBtnUpload;
   @BindView(R.id.inc_loading)
    LinearLayout mIncLoading;
   @BindView(R.id.tv_loading_text)
    TextView mTvLoadingText;

    private ReceivableReq mReceivableReq;
    private String mLocalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);
        ButterKnife.bind(this);
        mLocalId = getIntent().getStringExtra(Constant.BUNDLE_STR_LOCALID);
        if (!TextUtils.isEmpty(mLocalId)) {
            mReceivableReq = ReceivableReq.findById(ReceivableReq.class, Integer.valueOf(mLocalId));
            mReceivableReq.setResourcesList(Resource.find(Resource.class, "local_id = ?", mReceivableReq.getId() + ""));
        } else {
            Toast.makeText(VisitActivity.this, "此条催收记录不存在", Toast.LENGTH_SHORT).show();
        }
        AudioManager.getInstance().init(this, mLocalId);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(mLocalId)) {
            mReceivableReq = ReceivableReq.findById(ReceivableReq.class, Integer.valueOf(mLocalId));
            mReceivableReq.setResourcesList(Resource.find(Resource.class, "local_id = ?", mReceivableReq.getId() + ""));
        }
        checkUploadValide();
        String address;
        mTvAddress.setText((address = mReceivableReq.getAddress()) == null ? "" : address);
    }

    private void checkUploadValide() {
        if (checkAddress() & checkPhoto() & checkAudio() & checkNote()) {
            mBtnSubmit.setBackgroundResource(R.color.green);
            mBtnSubmit.setClickable(true);
            mBtnUpload.setBackgroundResource(R.color.green);
            mBtnUpload.setClickable(true);
        } else {
            mBtnSubmit.setBackgroundResource(R.color.gray);
            mBtnSubmit.setClickable(false);
            mBtnUpload.setBackgroundResource(R.color.gray);
            mBtnUpload.setClickable(false);
        }
    }

    private boolean checkAddress() {
        if (TextUtils.isEmpty(mReceivableReq.getAddress())) {
            return false;
        }
        return true;
    }

    private boolean checkPhoto() {
        if (mReceivableReq.getResourcesList() != null) {
            ArrayList<Resource> list = (ArrayList<Resource>) mReceivableReq.getResourcesList();
            int count = 0;
            for (Resource res : list) {
                if (res.getResourceType().equals(Constant.RESOURCE_PHOTO)) {
                    count++;
                }
            }
            if (count >= 3) {
                mTvCameraStatus.setText("共" + count + "张");
                return true;
            }else{
                 mTvCameraStatus.setText(count+"/3张");
            }
        }
        return false;
    }

    private boolean checkAudio() {
        if (mReceivableReq.getResourcesList() != null) {
            ArrayList<Resource> list = (ArrayList<Resource>) mReceivableReq.getResourcesList();
            for (Resource res : list) {
                if (res.getResourceType().equals(Constant.RESOURCE_AUDIO)) {
                    mTvRecorderStatus.setText("已录音");
                    return true;
                }
            }
        }
        mTvRecorderStatus.setText("未录音");
        return false;
    }

    private boolean checkNote() {
        if (!TextUtils.isEmpty(mReceivableReq.getAccessObject())
                && !TextUtils.isEmpty(mReceivableReq.getAccessResult())
                && !TextUtils.isEmpty(mReceivableReq.getAddressValidity())
                && !TextUtils.isEmpty(mReceivableReq.getDetail())) {
            mTvNoteStatus.setText("记录完成");
            return true;
        } else {
            mTvNoteStatus.setText("无外访记录");
        }
        return false;
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
        mTvTitle.setText("外访");
        mTvLoadingText.setText("上传中,请稍后...");
        mTvAuthorizedAgency.setText(mReceivableReq.getAuthorized_agency());
        mTvLoaneeName.setText(mReceivableReq.getLoanee_name());
        mTvAuthorizeAmount.setText(mReceivableReq.getAuthorize_amount());
        mTvOverdueDate.setText(mReceivableReq.getOverdue_date());
    }

    @OnClick({R.id.iv_address_perform, R.id.iv_camera_perform, R.id.iv_recorder_perform, R.id.iv_note_perform, R.id.btn_submit_box, R.id.btn_upload})
    public void performBtn(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.iv_address_perform:
                mReceivableReq.setState(Constant.STATE_DRAFT);
                mReceivableReq.save();
                intent.setClass(VisitActivity.this, AddressActivity.class);
                intent.putExtra(Constant.BUNDLE_STR_LOCALID, mReceivableReq.getId() + "");
                startActivity(intent);
                break;
            case R.id.iv_camera_perform:
                mReceivableReq.setState(Constant.STATE_DRAFT);
                mReceivableReq.save();
                intent.setClass(VisitActivity.this, PhotoActivity.class);
                intent.putExtra(Constant.BUNDLE_STR_LOCALID, mReceivableReq.getId() + "");
                startActivity(intent);
                break;
            case R.id.iv_recorder_perform:
                mReceivableReq.setState(Constant.STATE_DRAFT);
                mReceivableReq.save();
                intent.setClass(VisitActivity.this, RecorderActivity.class);
                intent.putExtra(Constant.BUNDLE_STR_LOCALID, mReceivableReq.getId() + "");
                startActivity(intent);
                break;
            case R.id.iv_note_perform:
                mReceivableReq.setState(Constant.STATE_DRAFT);
                mReceivableReq.save();
                intent.setClass(VisitActivity.this, NoteActivity.class);
                intent.putExtra(Constant.BUNDLE_STR_LOCALID, mReceivableReq.getId() + "");
                startActivity(intent);
                break;
            case R.id.btn_submit_box:
                DbManager.saveReceivableReqDb(mReceivableReq, Constant.STATE_STAY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(VisitActivity.this, MainV2Activity.class);
                startActivity(intent);
                break;
            case R.id.btn_upload:
                mIncLoading.setVisibility(View.VISIBLE);
                Api.getInstance().saveReceivableApi(LoginManager.getInstance().getOperator(VisitActivity.this).getOperatorId(),
                        mReceivableReq,
                        PhotoManager.getPhotoFiles(VisitActivity.this, mReceivableReq.getId()+""),
                        AudioManager.getInstance().getAudioFiles(),
                        new Api.Callback<String>() {

                            @Override
                            public void onApiSuccess(String result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(VisitActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                        mIncLoading.setVisibility(View.GONE);
                                        Intent intent = new Intent();
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.setClass(VisitActivity.this, MainV2Activity.class);
                                        startActivity(intent);
                                        //TODO 删除外访文件,修改外访记录数据Db
                                        DataCleanManager.clearPath(CommonUtils.getReceivableDir(VisitActivity.this, mReceivableReq.getId() + ""));
                                        DbManager.saveReceivableReqDb(mReceivableReq,Constant.STATE_END);
                                    }
                                });
                            }

                            @Override
                            public void onApiFailed(String resultCode, final String resultMessage) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(VisitActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
                                        mIncLoading.setVisibility(View.GONE);
                                    }
                                });

                            }
                        });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioManager.getInstance().release();
    }
}
