package com.zleidadr.ui;

import android.app.Activity;
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

import com.zleidadr.R;
import com.zleidadr.api.Api;
import com.zleidadr.common.Constant;
import com.zleidadr.entity.Address;
import com.zleidadr.entity.ReceivableReq;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressActivity extends Activity {

    @BindView(R.id.tv_back)
    FrameLayout mTvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.lv_address)
    ListView mLvAddress;
    private BaseAdapter mAdpater;
    private List<Address> mAddressList = new ArrayList<>();
    private ReceivableReq mReceivableReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        String localId = getIntent().getStringExtra(Constant.BUNDLE_STR_LOCALID);
        if (!TextUtils.isEmpty(localId)) {
            mReceivableReq = ReceivableReq.findById(ReceivableReq.class, Integer.valueOf(localId));
//            mReceivableReq.setResourcesList(Resource.find(Resource.class, "local_id = ?", mReceivableReq.getId() + ""));
        } else {
            Toast.makeText(AddressActivity.this, "此条催收记录不存在", Toast.LENGTH_SHORT).show();
        }
        initView();
        loadApi();
    }

    private void loadApi() {
        Api.getInstance().getProjectAddressListApi(mReceivableReq.getProjectId() + "", new Api.Callback<List<Address>>() {
            @Override
            public void onApiSuccess(List<Address> result) {
                if (result != null) {
                    mAddressList = result;
                    mAdpater.notifyDataSetChanged();
                }
            }

            @Override
            public void onApiFailed(String resultCode, String resultMessage) {
                Toast.makeText(AddressActivity.this, "数据读取异常", Toast.LENGTH_SHORT).show();
            }
        });
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
        mTvTitle.setText("外访地址");
        mAdpater = new BaseAdapter() {
            @Override
            public int getCount() {
                if (mAddressList != null) {
                    return mAddressList.size();
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return mAddressList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_address, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                Address address = mAddressList.get(position);
                holder.tvAddressType.setText(address.getAddressType());
                holder.tvVisitDes.setText("已访" + address.getVisitTimes() + "次");
                holder.tvAddress.setText(address.getAddress());
                return convertView;
            }
        };
        mLvAddress.setAdapter(mAdpater);
        mLvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mReceivableReq.setAddress(mAddressList.get(position).getAddress());
                mReceivableReq.setProjectAddressId(Integer.valueOf(mAddressList.get(position).getProjectAddressId()));
                mReceivableReq.save();
                finish();
            }
        });
    }


    static class ViewHolder {
        @BindView(R.id.tv_address_type)
        TextView tvAddressType;
        @BindView(R.id.tv_visit_des)
        TextView tvVisitDes;
        @BindView(R.id.tv_address)
        TextView tvAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
