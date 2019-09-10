package com.zleidadr.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zleidadr.R;
import com.zleidadr.api.Api;
import com.zleidadr.common.Constant;
import com.zleidadr.entity.Receivable;
import com.zleidadr.manager.LoginManager;
import com.zleidadr.manager.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReceivableActivity extends Activity {

    @Bind(R.id.tv_back)
    FrameLayout mTvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    @Bind(R.id.lv_receivable_list)
    PullToRefreshListView mLvReceivableList;

    private BaseAdapter mAdapter;
    private List<Receivable> mReceivableList = new ArrayList<>();
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receviable);
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
        mTvTitle.setText("反馈箱");
        mTvRight.setVisibility(View.INVISIBLE);
        mTvRight.setText("筛选");
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (mReceivableList == null) {
                    return 0;
                }
                return mReceivableList.size();
            }

            @Override
            public Object getItem(int position) {
                return mReceivableList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Receivable receivable = mReceivableList.get(position);
                ViewHolder holder;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_receivable, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                setAuditStatus(holder.mTvAuditStatus, receivable);
                holder.mTvProjectCode.setText(receivable.getProject_code());
                holder.mTvAddress.setText(receivable.getAddress());
                holder.mTvReceiveTime.setText(receivable.getReceive_time());
                if (receivable.getAuditStatus().equals("20")) {
                    holder.llRefuse.setVisibility(View.VISIBLE);
                    holder.tvRefuseReason.setText(receivable.getRefuseReason());
                } else {
                    holder.llRefuse.setVisibility(View.GONE);
                    holder.tvRefuseReason.setText("");
                }
                return convertView;
            }
        };
        if (NetworkManager.getInstance().isNetworkConnected(this)) {
            mLvReceivableList.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            mLvReceivableList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
        mLvReceivableList.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新数据");
        mLvReceivableList.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");
        mLvReceivableList.getLoadingLayoutProxy(true, false).setReleaseLabel("放开刷新数据");
        mLvReceivableList.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载数据");
        mLvReceivableList.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        mLvReceivableList.getLoadingLayoutProxy(false, true).setReleaseLabel("放开加载数据");
        mLvReceivableList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage = 1;
                if (NetworkManager.getInstance().isNetworkConnected(ReceivableActivity.this)) {
                    mLvReceivableList.setMode(PullToRefreshBase.Mode.BOTH);
                } else {
                    mLvReceivableList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
                loadApi();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage++;
                loadApi();
            }
        });
        mLvReceivableList.setAdapter(mAdapter);
    }

    private void setAuditStatus(TextView tvStatus, Receivable receivable) {
        switch (receivable.getAuditStatus() == null ? "" : receivable.getAuditStatus()) {
            case Constant.RECEIVABLE_STATUS_CHECK:
                tvStatus.setText(receivable.getAuditStatusName());
                tvStatus.setBackgroundResource(R.drawable.boder_1dp_green);
                tvStatus.setTextColor(getResources().getColor(R.color.text_green));
                break;
            case Constant.RECEIVABLE_STATUS_REFUSE:
                tvStatus.setText(receivable.getAuditStatusName());
                tvStatus.setBackgroundResource(R.drawable.boder_1dp_grey);
                tvStatus.setTextColor(getResources().getColor(R.color.text_grey));
                break;
            case Constant.RECEIVABLE_STATUS_PASS:
                tvStatus.setText(receivable.getAuditStatusName());
                tvStatus.setBackgroundResource(R.drawable.boder_1dp_red);
                tvStatus.setTextColor(getResources().getColor(R.color.text_red));
                break;
            default:
                tvStatus.setText("未知");
                tvStatus.setBackgroundResource(R.drawable.boder_1dp_grey);
                tvStatus.setTextColor(getResources().getColor(R.color.text_grey));
                break;
        }
    }

    private void loadApi() {
        Api.getInstance().getReceivableListApi(LoginManager.getInstance().getOperator(this).getOperatorId(), mPage, new Api.Callback<List<Receivable>>() {
            @Override
            public void onApiSuccess(final List<Receivable> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPage > 1) {
                            mReceivableList.addAll(result);
                        } else {
                            mReceivableList = result;
                        }
                        mAdapter.notifyDataSetChanged();
                        mLvReceivableList.onRefreshComplete();
                    }
                });
            }

            @Override
            public void onApiFailed(String resultCode, String resultMessage) {
                mLvReceivableList.onRefreshComplete();
                Toast.makeText(ReceivableActivity.this, "数据读取异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class ViewHolder {
        @Bind(R.id.tv_audit_status)
        TextView mTvAuditStatus;
        @Bind(R.id.tv_project_code)
        TextView mTvProjectCode;
        @Bind(R.id.tv_address)
        TextView mTvAddress;
        @Bind(R.id.tv_receive_time)
        TextView mTvReceiveTime;
        @Bind(R.id.ll_refuse)
        LinearLayout llRefuse;
        @Bind(R.id.tv_refuse_reason)
        TextView tvRefuseReason;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
