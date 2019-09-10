package com.zleidadr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zleidadr.R;
import com.zleidadr.api.Api;
import com.zleidadr.common.Constant;
import com.zleidadr.entity.Appoint;
import com.zleidadr.manager.LoginManager;
import com.zleidadr.manager.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaoxuli on 16/1/14.
 */
public class MyAppointActivity extends Activity {

    private static final String TAG = MyAppointActivity.class.getName();

    @Bind(R.id.tv_back)
    FrameLayout mTvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    @Bind(R.id.rlv_appoint_list)
    PullToRefreshListView mLvAppointList;

    private BaseAdapter mAdapter;
    private List<Appoint> mAppointList = new ArrayList<>();
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myappoint);
        ButterKnife.bind(this);
        initView();
        loadApi();
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
        mTvTitle.setText("我的外催单");
        mTvRight.setVisibility(View.INVISIBLE);
        mTvRight.setText("筛选");
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mLvAppointList.setAdapter(mAdapter);
        mLvAppointList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String projectId = mAppointList.get(position - 1).getProjectId();
                if (TextUtils.isEmpty(projectId)) {
                    Toast.makeText(MyAppointActivity.this, "委案项目不存在", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.setClass(MyAppointActivity.this, AppointDetailActivity.class);
                intent.putExtra(Constant.BUNDLE_STR_PROJECTID, projectId);
                startActivity(intent);
            }
        });
        mAdapter = new BaseAdapter() {

            private ViewHolder holder;

            @Override
            public int getCount() {
                return mAppointList.size();
            }

            @Override
            public Object getItem(int position) {
                return mAppointList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Appoint appoint = mAppointList.get(position);
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_appoint, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                setAppointStatus(holder.tvStatus, appoint);
                holder.tvAddress.setText(appoint.getLoanee_business_city());
                holder.tvAmount.setText(appoint.getAuthorize_amount() + "元");
                holder.tvEnddate.setText(appoint.getAppointEndDate());

                return convertView;
            }
        };
        if (NetworkManager.getInstance().isNetworkConnected(this)) {
            mLvAppointList.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            mLvAppointList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
        mLvAppointList.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新数据");
        mLvAppointList.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");
        mLvAppointList.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新数据");
        mLvAppointList.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载数据");
        mLvAppointList.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        mLvAppointList.getLoadingLayoutProxy(false, true).setReleaseLabel("放开加载数据");
        mLvAppointList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage = 1;
                if (NetworkManager.getInstance().isNetworkConnected(MyAppointActivity.this)) {
                    mLvAppointList.setMode(PullToRefreshBase.Mode.BOTH);
                } else {
                    mLvAppointList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
                loadApi();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage++;
                loadApi();
            }
        });
        mLvAppointList.setAdapter(mAdapter);
    }

    private void setAppointStatus(TextView tvStatus, Appoint appoint) {
        switch (appoint.getStatusNum()) {
            case Constant.APPOINT_STATUS_AWAIT:
                tvStatus.setText(appoint.getStatusName());
                tvStatus.setBackgroundResource(R.drawable.boder_1dp_green);
                tvStatus.setTextColor(getResources().getColor(R.color.text_green));
                break;
            case Constant.APPOINT_STATUS_DONE:
                tvStatus.setText(appoint.getStatusName() + appoint.getVisitTimes() + "次");
                tvStatus.setBackgroundResource(R.drawable.boder_1dp_grass);
                tvStatus.setTextColor(getResources().getColor(R.color.text_grass));
                break;
            case Constant.APPOINT_STATUS_SUCCESS:
                tvStatus.setText(appoint.getStatusName());
                tvStatus.setBackgroundResource(R.drawable.boder_1dp_red);
                tvStatus.setTextColor(getResources().getColor(R.color.text_red));
                break;
            case Constant.APPOINT_STATUS_OVERDUE:
                tvStatus.setText(appoint.getStatusName());
                tvStatus.setBackgroundResource(R.drawable.boder_1dp_grey);
                tvStatus.setTextColor(getResources().getColor(R.color.text_grey));
                break;
        }
    }

    private void loadApi() {
        Api.getInstance().getProjectAppointListApi(LoginManager.getInstance().getOperator(this).getOperatorId(), null, null, null, mPage, new Api
                .Callback<List<Appoint>>() {
            @Override
            public void onApiSuccess(final List<Appoint> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLvAppointList.onRefreshComplete();
                        if (mPage > 1) {
                            mAppointList.addAll(result);
                        } else {
                            mAppointList = result;
                        }
                        mAdapter.notifyDataSetChanged();

                    }
                });
            }

            @Override
            public void onApiFailed(String resultCode, String resultMessage) {
                mLvAppointList.onRefreshComplete();
                Toast.makeText(MyAppointActivity.this, "数据读取异常", Toast.LENGTH_SHORT).show();
            }
        });

    }

    static class ViewHolder {
        @Bind(R.id.tv_status)
        TextView tvStatus;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.tv_amount)
        TextView tvAmount;
        @Bind(R.id.tv_end_date)
        TextView tvEnddate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
