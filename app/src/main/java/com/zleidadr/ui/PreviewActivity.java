package com.zleidadr.ui;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zleidadr.R;
import com.zleidadr.common.CommonUtils;
import com.zleidadr.common.Constant;
import com.zleidadr.common.ImageLocalLoader;
import com.zleidadr.common.Logger;
import com.zleidadr.manager.PhotoManager;

import java.io.File;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity {

    private static final String TAG = PreviewActivity.class.getName();

   @BindView(R.id.tv_back)
    FrameLayout mTvBack;
   @BindView(R.id.tv_title)
    TextView mTvTitle;
   @BindView(R.id.vp_photos)
    ViewPager mVpPhotos;
    private LinkedList<File> mFiles;
    private PagerAdapter mPagerAdapter;
    private int mPage;
    private String mLocalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        mLocalId = getIntent().getStringExtra(Constant.BUNDLE_STR_LOCALID);
        mPage = getIntent().getIntExtra("page", 0);
        mFiles = PhotoManager.getPhotoFiles(this, mLocalId);
        initView();
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
        mTvTitle.setText("照片预览");
        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return mFiles.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(PreviewActivity.this);
                container.addView(imageView);
                Point p = new Point();
                getWindowManager().getDefaultDisplay().getSize(p);
                ImageLocalLoader.getInstance().setImage(imageView, p.x, mFiles.get(position).getAbsolutePath(), new ImageLocalLoader.ImageLocalLoaderListener() {
                    @Override
                    public void onSetListener(final ImageView imageView, final Bitmap bitmap) {
                        PreviewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(CommonUtils.adjustPhotoRotation(bitmap, 90));
                            }
                        });
                    }
                });
                return imageView;
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);
                Logger.d(TAG, "position: " + position);
            }
        };

        mVpPhotos.setAdapter(mPagerAdapter);
        mVpPhotos.setCurrentItem(mPage);
    }

}
