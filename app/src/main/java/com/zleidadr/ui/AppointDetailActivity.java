package com.zleidadr.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.zleidadr.R;
import com.zleidadr.api.Api;
import com.zleidadr.common.Constant;
import com.zleidadr.entity.AppointDetail;
import com.zleidadr.entity.ReceivableReq;
import com.zleidadr.manager.LoginManager;
import com.zleidadr.net.BitmapCache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppointDetailActivity extends Activity {

    private static final String TAG = AppointDetailActivity.class.getName();

    @Bind(R.id.tv_back)
    FrameLayout mTvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.pts_appoint_info)
    PagerTabStrip mPtsAppointInfo;
    @Bind(R.id.vp_appoint_info)
    ViewPager mVpAppointInfo;


    private List<View> mPagerList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();
    private LinearLayout mLl1;
    private LinearLayout mLl2;
    private LinearLayout mLl3;
    private PagerAdapter mPagerAdapter;
    private String mProjectId;
    private ViewHolder1 mViewHolder1;
    private ViewHolder2 mViewHolder2;
    private ViewHolder3 mViewHolder3;
    private String mLocalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint_detail);
        ButterKnife.bind(this);
        mProjectId = getIntent().getStringExtra(Constant.BUNDLE_STR_PROJECTID);
        initView();
        loadApi();
    }

    @OnClick(R.id.btn_save)
    public void visitBtn(Button button) {
        Intent intent = new Intent();
        intent.putExtra(Constant.BUNDLE_STR_LOCALID, mLocalId);
        intent.setClass(AppointDetailActivity.this, VisitActivity.class);
        startActivity(intent);
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
        mTvTitle.setText("催单详情页");
        LayoutInflater lf = getLayoutInflater().from(this);
        mLl1 = (LinearLayout) lf.inflate(R.layout.pager_base_info, null);
        mLl2 = (LinearLayout) lf.inflate(R.layout.pager_relation_info, null);
        mLl3 = (LinearLayout) lf.inflate(R.layout.pager_other_info, null);
        mViewHolder1 = new ViewHolder1(mLl1);
        mViewHolder2 = new ViewHolder2(mLl2);
        mViewHolder3 = new ViewHolder3(mLl3);
        mPagerList.add(mLl1);
        mPagerList.add(mLl2);
        mPagerList.add(mLl3);
        mTitleList.add("基本信息");
        mTitleList.add("相关信息");
        mTitleList.add("其他信息");
        mPtsAppointInfo.setTabIndicatorColorResource(R.color.deep_green);
        mPtsAppointInfo.setDrawFullUnderline(true);
        mPtsAppointInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mPagerList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mPagerList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mPagerList.get(position));
                return mPagerList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitleList.get(position);
            }


        };
        mVpAppointInfo.setAdapter(mPagerAdapter);
    }

    private void loadApi() {
        Api.getInstance().getProjectAppointDetailApi(LoginManager.getInstance().getOperator(this).getOperatorId(), mProjectId, new Api.Callback<AppointDetail>() {
            @Override
            public void onApiSuccess(AppointDetail result) {
                setViewPagerData(result);
                mPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onApiFailed(String resultCode, String resultMessage) {
                Toast.makeText(AppointDetailActivity.this, "数据读取异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setViewPagerData(AppointDetail result) {
        mViewHolder1.mTvLoaneeName.setText(result.getLoanee_name());
        mViewHolder1.mTvSex.setText(result.getLoanee_sex().equals(Constant.SEX_MALE) ? Constant.SEX_MALE_STR : Constant.SEX_FEMALE_STR);
        mViewHolder1.mTvIdcard.setText(result.getLoanee_id_card());
        mViewHolder1.mTvMobile.setText(result.getLoanee_mobile());
        mViewHolder1.mTvHomeAddress.setText(result.getLoanee_home_address());
        mViewHolder1.mTvHome.setText(result.getLoanee_home());
        mViewHolder1.mTvBusinessCity.setText(result.getLoanee_business_city());
        mViewHolder1.mTvAmount.setText(result.getAuthorize_amount() + "元");
        mViewHolder1.mTvUrgeDate.setText(result.getCan_urge_date() + "天");
        mViewHolder2.mTvAuthorizedAgency.setText(result.getAuthorized_agency());
        mViewHolder2.mTvOverdueDate.setText(result.getOverdue_date());
        mViewHolder2.mTvLoaneeCompany.setText(result.getLoanee_company());
        mViewHolder2.mTvLoaneeCompanyAddress.setText(result.getLoanee_company_address());
        mViewHolder2.mTvLoanDate.setText(result.getLoan_date());
        mViewHolder2.mTvRepaymentBank.setText(result.getRepayment_bank());
        mViewHolder2.mTvRepaymentBankId.setText(result.getRepayment_bank_id());
        mViewHolder2.mTvReceiptName.setText(result.getReceipt_name());
        mViewHolder2.mTvProjectCode.setText(result.getProject_code());
        mViewHolder3.mTvRemarks1.setText(result.getRemarks1());
        mViewHolder3.mTvRemarks2.setText(result.getRemarks2());
        mViewHolder3.mTvRemarks3.setText(result.getRemarks3());

        if (!TextUtils.isEmpty(result.getProject_files())) {
            final String[] images = result.getProject_files().split(",");
            if (images.length > 0) {
                mViewHolder3.mGvProjectImage.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return images.length;
                    }

                    @Override
                    public Object getItem(int position) {
                        return images[position];
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        if (convertView == null) {
                            convertView = new ImageView(getApplicationContext());
                            Point p = new Point();
                            getWindowManager().getDefaultDisplay().getSize(p);
                            GridView.LayoutParams params = new GridView.LayoutParams(p.x / 2, p.x * 3 / 8);
                            convertView.setLayoutParams(params);
                        }
//                        ((ImageView) convertView).setImageResource(R.drawable.photo_default);
                        ImageLoader.ImageListener listener = ImageLoader.getImageListener((ImageView) convertView, R.drawable.photo_default, R.drawable.photo_default);
                        ((ImageView) convertView).setImageBitmap(new ImageLoader(Volley.newRequestQueue(AppointDetailActivity.this), new BitmapCache())
//                                .get(Api.getImageUrl(images[position]), listener).getBitmap());
                                .get(images[position], listener).getBitmap());
                        return convertView;
                    }
                });
            }
        }
        //TODO 创建新的记录
        ReceivableReq receivableReq = new ReceivableReq();
        receivableReq.setBidId(0);
        receivableReq.setProjectId(Integer.valueOf(result.getProjectId()));
        receivableReq.setProjectCode(result.getProject_code());
        receivableReq.setReceiveType(5);
        receivableReq.setAuthorized_agency(result.getAuthorized_agency());
        receivableReq.setAuthorize_amount(result.getAuthorize_amount());
        receivableReq.setLoanee_name(result.getLoanee_name());
        receivableReq.setOverdue_date(result.getOverdue_date());
        receivableReq.setProjectAppointId(Integer.valueOf(result.getProjectAppointId()));
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String s = format1.format(new Date());
        receivableReq.setReceiveTime(s);
        receivableReq.setState(Constant.STATE_INIT);
        mLocalId = String.valueOf(receivableReq.save());
    }

    static class ViewHolder1 {
        @Bind(R.id.tv_loanee_name)
        TextView mTvLoaneeName;
        @Bind(R.id.tv_sex)
        TextView mTvSex;
        @Bind(R.id.tv_idcard)
        TextView mTvIdcard;
        @Bind(R.id.tv_mobile)
        TextView mTvMobile;
        @Bind(R.id.tv_home_address)
        TextView mTvHomeAddress;
        @Bind(R.id.tv_home)
        TextView mTvHome;
        @Bind(R.id.tv_business_city)
        TextView mTvBusinessCity;
        @Bind(R.id.tv_amount)
        TextView mTvAmount;
        @Bind(R.id.tv_urge_date)
        TextView mTvUrgeDate;

        ViewHolder1(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolder2 {
        @Bind(R.id.tv_authorized_agency)
        TextView mTvAuthorizedAgency;
        @Bind(R.id.tv_overdue_date)
        TextView mTvOverdueDate;
        @Bind(R.id.tv_loanee_company)
        TextView mTvLoaneeCompany;
        @Bind(R.id.tv_loanee_company_address)
        TextView mTvLoaneeCompanyAddress;
        @Bind(R.id.tv_loan_date)
        TextView mTvLoanDate;
        @Bind(R.id.tv_repayment_bank)
        TextView mTvRepaymentBank;
        @Bind(R.id.tv_repayment_bank_id)
        TextView mTvRepaymentBankId;
        @Bind(R.id.tv_receipt_name)
        TextView mTvReceiptName;
        @Bind(R.id.tv_project_code)
        TextView mTvProjectCode;

        ViewHolder2(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ViewHolder3 {
        @Bind(R.id.tv_remarks1)
        TextView mTvRemarks1;
        @Bind(R.id.tv_remarks2)
        TextView mTvRemarks2;
        @Bind(R.id.tv_remarks3)
        TextView mTvRemarks3;
        @Bind(R.id.gv_project_image)
        GridView mGvProjectImage;

        ViewHolder3(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
