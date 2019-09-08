package com.zleidadr.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StayActivity extends AppCompatActivity {


   @BindView(R.id.tv_back)
    FrameLayout mTvBack;
   @BindView(R.id.tv_title)
    TextView mTvTitle;
   @BindView(R.id.tv_right)
    TextView mTvRight;
   @BindView(R.id.lv_stay_list)
    ListView mLvStayList;
   @BindView(R.id.btn_upload)
    Button mBtnUpload;

    private BaseAdapter mAdapter;
    private List<ReceivableReq> mStayList;
    private HashSet<Integer> mNumSet = new HashSet<>();
    private ExecutorService pool = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stay);
        ButterKnife.bind(this);
        initView();
        loadDb();
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
        mTvTitle.setText("提交箱");
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText("筛选");
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBtnUpload.setBackgroundResource(R.color.gray);
        mBtnUpload.setClickable(false);
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (mStayList == null) {
                    return 0;
                }
                return mStayList.size();
            }

            @Override
            public Object getItem(int position) {
                return mStayList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ReceivableReq receivableReq = mStayList.get(position);
                ViewHolder holder;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_project, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.mTvProjectCode.setText(receivableReq.getProjectCode());
                holder.mTvAddress.setText(receivableReq.getAddress());
                holder.mTvReceiveTime.setText(receivableReq.getReceiveTime());
                if (mNumSet.contains(position)) {
                    holder.mIvSelectIcon.setImageResource(R.drawable.icon_selected);
                } else {
                    holder.mIvSelectIcon.setImageResource(R.drawable.icon_unselected);
                }
                return convertView;
            }
        };
        mLvStayList.setAdapter(mAdapter);
        mLvStayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mNumSet.contains(position)) {
                    mNumSet.remove(position);
                } else {
                    mNumSet.add(position);
                }
                if (mNumSet.size() > 0) {
                    mBtnUpload.setBackgroundResource(R.color.green);
                    mBtnUpload.setClickable(true);
                } else {
                    mBtnUpload.setBackgroundResource(R.color.gray);
                    mBtnUpload.setClickable(false);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.btn_upload)
    public void onClick(View view) {
        Iterator<Integer> iterator = mNumSet.iterator();
        while (iterator.hasNext()) {
            ReceivableReq receivableReq = mStayList.get(iterator.next());
            List<Resource> resources = Resource.find(Resource.class, "local_id = ?", receivableReq.getId() + "");
            receivableReq.setResourcesList(resources);
            UploadRunnable runnable = new UploadRunnable();
            runnable.receivableReq = receivableReq;
            pool.execute(runnable);
        }
        mNumSet.clear();
        mBtnUpload.setBackgroundResource(R.color.gray);
        mBtnUpload.setClickable(false);
    }

    private void loadDb() {
        mStayList = DbManager.getReceivableReqListDb(Constant.STATE_STAY);
        mAdapter.notifyDataSetChanged();
    }

    static class ViewHolder {
       @BindView(R.id.fl_select)
        FrameLayout mLlSelect;
       @BindView(R.id.iv_select_icon)
        ImageView mIvSelectIcon;
       @BindView(R.id.tv_project_code)
        TextView mTvProjectCode;
       @BindView(R.id.tv_address)
        TextView mTvAddress;
       @BindView(R.id.tv_receive_time)
        TextView mTvReceiveTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    class UploadRunnable implements Runnable {
        public ReceivableReq receivableReq;

        @Override
        public void run() {
            Api.getInstance().saveReceivableApi(LoginManager.getInstance().getOperator(StayActivity.this).getOperatorId(),
                    receivableReq,
                    PhotoManager.getPhotoFiles(StayActivity.this, receivableReq.getId() + ""),
                    AudioManager.getAudioFiles(StayActivity.this, receivableReq.getId() + ""),
                    new Api.Callback<String>() {

                        @Override
                        public void onApiSuccess(String result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DataCleanManager.clearPath(CommonUtils.getReceivableDir(StayActivity.this, receivableReq.getId() + ""));
                                    DbManager.saveReceivableReqDb(receivableReq, Constant.STATE_END);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadDb();
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onApiFailed(String resultCode, final String resultMessage) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });

                        }
                    });
        }
    }

}
