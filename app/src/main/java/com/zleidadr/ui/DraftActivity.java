package com.zleidadr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.zleidadr.common.CommonUtils;
import com.zleidadr.common.Constant;
import com.zleidadr.entity.ReceivableReq;
import com.zleidadr.entity.Resource;
import com.zleidadr.manager.DataCleanManager;
import com.zleidadr.sugarDb.DbManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DraftActivity extends Activity {

   @BindView(R.id.tv_back)
    FrameLayout mTvBack;
   @BindView(R.id.tv_title)
    TextView mTvTitle;
   @BindView(R.id.tv_right)
    TextView mTvRight;
   @BindView(R.id.lv_draft_list)
    ListView mLvDraftList;
   @BindView(R.id.btn_delete)
    Button mBtnDelete;


    private boolean mSelectIsValid = false;
    private BaseAdapter mAdapter;
    private List<ReceivableReq> mDraftList;
    private HashSet<Integer> mNumSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        mTvTitle.setText("草稿箱");
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText("编辑");
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectIsValid) {
                    mTvRight.setText("编辑");
                    mBtnDelete.setVisibility(View.GONE);
                } else {
                    mTvRight.setText("取消");
                    mBtnDelete.setVisibility(View.VISIBLE);
                }
                mSelectIsValid = !mSelectIsValid;
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (mDraftList == null) {
                    return 0;
                }
                return mDraftList.size();
            }

            @Override
            public Object getItem(int position) {
                return mDraftList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ReceivableReq receivableReq = mDraftList.get(position);
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
                if (mSelectIsValid) {
                    holder.mIvSelectIcon.setVisibility(View.VISIBLE);
                    if (mNumSet.contains(position)) {
                        holder.mIvSelectIcon.setImageResource(R.drawable.icon_selected);
                    } else {
                        holder.mIvSelectIcon.setImageResource(R.drawable.icon_unselected);
                    }
                } else {
                    holder.mIvSelectIcon.setVisibility(View.INVISIBLE);
                }
                return convertView;
            }
        };
        mLvDraftList.setAdapter(mAdapter);
        mLvDraftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectIsValid) {
                    if (mNumSet.contains(position)) {
                        mNumSet.remove(position);
                    } else {
                        mNumSet.add(position);
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    ReceivableReq receivableReq = mDraftList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra(Constant.BUNDLE_STR_LOCALID, receivableReq.getId() + "");
                    intent.setClass(DraftActivity.this, VisitActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @OnClick(R.id.btn_delete)
    public void onClick(View view) {
        Iterator<Integer> iterator = mNumSet.iterator();
        while (iterator.hasNext()) {
            ReceivableReq receivableReq = mDraftList.get(iterator.next());
            DataCleanManager.clearPath(CommonUtils.getReceivableDir(DraftActivity.this, receivableReq.getId() + ""));
            List<Resource> resources = Resource.find(Resource.class, "local_id = ?", receivableReq.getId() + "");
            for (Resource resource : resources) {
                resource.delete();
            }
            receivableReq.delete();
        }
        mNumSet.clear();
        loadDb();
        mTvRight.performClick();
    }

    private void loadDb() {
        mDraftList = DbManager.getReceivableReqListDb(Constant.STATE_DRAFT);
        mAdapter.notifyDataSetChanged();
    }

    static class ViewHolder {
       @BindView(R.id.fl_select)
        FrameLayout mFlSelect;
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

}
