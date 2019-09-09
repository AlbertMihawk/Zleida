package com.zleidadr.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zleidadr.R;
import com.zleidadr.Zleida;
import com.zleidadr.common.Constant;
import com.zleidadr.common.ImageLocalLoader;
import com.zleidadr.entity.ReceivableReq;
import com.zleidadr.entity.Resource;
import com.zleidadr.manager.LoginManager;
import com.zleidadr.manager.PhotoManager;
import com.zleidadr.sugarDb.DbManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoActivity extends LocationBaseActivity {
    public static Handler handler;
    private static final String TAG = VideoActivity.class.getName();

    @Bind(R.id.tv_back)
    FrameLayout mTvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_right)
    TextView mTvRight;
    @Bind(R.id.gv_video)
    GridView mGvVideo;
    @Bind(R.id.iv_delete)
    ImageView mIvDelete;

    private List<File> mFiles;
    private HashSet<Integer> mNumSet = new HashSet<>();
    private BaseAdapter mAdapter;
    private boolean mSelectIsValid = false;
    private String mLocalId;
    private ReceivableReq mReceivableReq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        mLocalId = getIntent().getStringExtra(Constant.BUNDLE_STR_LOCALID);
        if (!TextUtils.isEmpty(mLocalId)) {
            mReceivableReq = ReceivableReq.findById(ReceivableReq.class, Integer.valueOf(mLocalId));
            List<Resource> list = Resource.find(Resource.class, "local_id = ?", mReceivableReq.getId() + "");
            if (list == null) {
                mReceivableReq.setResourcesList(new ArrayList<Resource>());
            } else {
                mReceivableReq.setResourcesList(list);
            }
        } else {
            Toast.makeText(VideoActivity.this, "此条催收记录不存在", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
        initView();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFiles = PhotoManager.getPhotoFiles(VideoActivity.this, mLocalId);
        mAdapter.notifyDataSetChanged();

    }


    private boolean deleteUncountedFile(List<Resource> list, List<File> files) {
        boolean result = false;
        for (File file : files) {
            boolean exist = false;
            for (Resource resource : list) {
                if (file.getPath().contains(resource.getResourceOriginal())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                file.delete();
                result = true;
            }
        }
        return result;
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        mTvTitle.setText("录像");
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText("编辑");
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectIsValid) {
                    mTvRight.setText("编辑");
                    mIvDelete.setVisibility(View.INVISIBLE);
                } else {
                    mTvRight.setText("取消");
                    mIvDelete.setVisibility(View.VISIBLE);
                }
                mSelectIsValid = !mSelectIsValid;
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mFiles == null ? 0 : mFiles.size();
            }

            @Override
            public Object getItem(int position) {
                return mFiles.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_video, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                Point p = new Point();
                getWindowManager().getDefaultDisplay().getSize(p);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(p.x / 3, p.x * 3 / 12);

                ImageLocalLoader.getInstance().setImage(holder.mIvVideo, p.x / 3, mFiles.get(position).getAbsolutePath(), new ImageLocalLoader.ImageLocalLoaderListener() {
                    @Override
                    public void onSetListener(final ImageView imageView, final Bitmap bitmap) {
                        VideoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
                holder.mIvVideo.setLayoutParams(params);
                if (mSelectIsValid) {
                    holder.mIvSelectStatus.setVisibility(View.VISIBLE);
                    if (mNumSet.contains(position)) {
                        holder.mIvSelectStatus.setImageResource(R.drawable.icon_selected);
                    } else {
                        holder.mIvSelectStatus.setImageResource(R.drawable.icon_unselected);
                    }
                } else {
                    holder.mIvSelectStatus.setVisibility(View.INVISIBLE);
                }
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return super.getDropDownView(position, convertView, parent);
            }
        };
        mGvVideo.setAdapter(mAdapter);
        mGvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    Intent intent = new Intent();
                    intent.setClass(VideoActivity.this, PreviewActivity.class);
                    intent.putExtra("page", position);
                    intent.putExtra(Constant.BUNDLE_STR_LOCALID, mReceivableReq.getId() + "");
                    startActivity(intent);
                }
            }
        });
    }

    @OnClick({R.id.iv_take_video, R.id.iv_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_take_video:
                if (mFiles != null && mFiles.size() >= 5) {
                    Toast.makeText(this, "视频数量为1至5张", Toast.LENGTH_SHORT).show();
                    break;
                }
//                Zleida.sCurrentPhotoFile = PhotoManager.takePhoto(VideoActivity.this, mLocalId);
                takeVideo();
                break;
            case R.id.iv_delete:
                deletePhotoFile();
                mNumSet.clear();
                mTvRight.setText("编辑");
                mIvDelete.setVisibility(View.INVISIBLE);
                mSelectIsValid = false;
                mFiles = PhotoManager.getPhotoFiles(VideoActivity.this, ReceivableReq.last(ReceivableReq.class)
                        .getId() + "");
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void takeVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri = null;
        try {
            fileUri = Uri.fromFile(createMediaFile()); // create a file to save the video
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
        // start the Video Capture Intent
        startActivityForResult(intent, 1);
    }

    private File createMediaFile() throws IOException {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "CameraDemo");
        File mediaStorageDir = new File(getExternalCacheDir() + File.separator +
                LoginManager.getInstance().getOperator(this).getOperatorId() + File.separator
                + Environment.DIRECTORY_MOVIES, "CameraDemo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "VID_" + timeStamp;
        String suffix = ".mp4";
        File mediaFile = new File(mediaStorageDir + File.separator + imageFileName + suffix);
        return mediaFile;
    }

    private void deletePhotoFile() {
        Integer[] nums = new Integer[mNumSet.size()];
        mNumSet.toArray(nums);
        DbManager.removeResources(mReceivableReq, mFiles, nums);
        PhotoManager.deletePhotoFile(nums, this, ReceivableReq.last(ReceivableReq.class)
                .getId() + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Uri data1 = data.getData();
                //拿到视频保存地址
                String s = data1.toString();
                String[] split = s.split(":");
                Message msg = Message.obtain();
                msg.obj = split[1];
                if (handler != null) {
                    handler.sendMessage(msg);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }


        if (requestCode == PhotoManager.TAKE_PHOTO_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<Resource> list = (ArrayList<Resource>) mReceivableReq.getResourcesList();
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
//                    Toast.makeText(PhotoActivity.this, "定位信息获取失败", Toast.LENGTH_SHORT).show();
                }
                resource.setResourceOriginal(Zleida.sCurrentPhotoFile.getName());
                resource.setResourceType(Constant.RESOURCE_PHOTO);
                resource.save();
                list.add(resource);
                mReceivableReq.setResourcesList(list);
                Zleida.sCurrentPhotoFile = null;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    static class ViewHolder {
        @Bind(R.id.iv_video)
        ImageView mIvVideo;
        @Bind(R.id.iv_select_status)
        ImageView mIvSelectStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
