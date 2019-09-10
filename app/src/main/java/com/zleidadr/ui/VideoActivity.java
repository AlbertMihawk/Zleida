package com.zleidadr.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.zleidadr.entity.ReceivableReq;
import com.zleidadr.entity.Resource;
import com.zleidadr.manager.VideoManager;
import com.zleidadr.sugarDb.DbManager;

import java.io.File;
import java.util.ArrayList;
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
        mFiles = VideoManager.getVideoFiles(VideoActivity.this, mLocalId);
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
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(p.x / 3, p.x * 5 / 12);
                holder.mIvVideo.setLayoutParams(params);

                File file = mFiles.get(position);
//                Uri uri = Uri.fromFile(file);
//                Cursor cursor = VideoActivity.this.getContentResolver().query(uri, new String[]{
//                        MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID}, null, null, null);
//                String[] proj = {MediaStore.Images.Media.DATA};
//
//                CursorLoader loader = new CursorLoader(VideoActivity.this, uri, proj, null, null, null);
//
//                cursor = loader.loadInBackground();
//
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//
//                cursor.moveToFirst();
//                String string = cursor.getString(column_index);
//
//                if (cursor != null && cursor.moveToNext()) {
//                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
//                    // 视频路径
//                    String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
//                    // ThumbnailUtils类2.2以上可用  Todo 获取视频缩略图
//                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MICRO_KIND);
                Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                holder.mIvVideo.setImageBitmap(videoThumbnail);
//                    holder.mIvVideo.setImageBitmap(bitmap);
//                }

//                //TODO 视频缩略图读取
//                ImageLocalLoader.getInstance().setImage(holder.mIvVideo, p.x / 3, mFiles.get(position).getAbsolutePath(), new ImageLocalLoader.ImageLocalLoaderListener() {
//                    @Override
//                    public void onSetListener(final ImageView imageView, final Bitmap bitmap) {
//                        VideoActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageView.setImageBitmap(bitmap);
//                            }
//                        });
//                    }
//                });

//
//                holder.mTvVideo.setText(mFiles.get(position).getName());

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
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    String type = "video/mp4";
                    Uri uri = Uri.fromFile(mFiles.get(position));
                    intent.setDataAndType(uri, type);
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
                Zleida.sCurrentVideoFile = VideoManager.takeVideo(VideoActivity.this, mLocalId);
//                VideoManager.takeVideo2(this);
                break;
            case R.id.iv_delete:
                deleteVideoFile();
                mNumSet.clear();
                mTvRight.setText("编辑");
                mIvDelete.setVisibility(View.INVISIBLE);
                mSelectIsValid = false;
                mFiles = VideoManager.getVideoFiles(VideoActivity.this, ReceivableReq.last(ReceivableReq.class)
                        .getId() + "");
                mAdapter.notifyDataSetChanged();
                break;
        }
    }


    private void deleteVideoFile() {
        Integer[] nums = new Integer[mNumSet.size()];
        mNumSet.toArray(nums);
        DbManager.removeResources(mReceivableReq, mFiles, nums);
        VideoManager.deleteVideoFile(nums, this, ReceivableReq.last(ReceivableReq.class)
                .getId() + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                // Video captured and saved to fileUri specified in the Intent
//                Uri data1 = data.getData();
//                //拿到视频保存地址
//                String s = data1.toString();
//                String[] split = s.split(":");
//                Message msg = Message.obtain();
//                msg.obj = split[1];
//                if (handler != null) {
//                    handler.sendMessage(msg);
//                }
//            } else if (resultCode == RESULT_CANCELED) {
//                // User cancelled the video capture
//            } else {
//                // Video capture failed, advise user
//            }
//        }


        if (requestCode == VideoManager.TAKE_VIDEO_RESULT) {
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
                Uri uri = data.getData();
//                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
//                if (cursor != null && cursor.moveToNext()) {
////                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID));
//                    // 视频路径
//                    String thumbnailPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
//                    resource.setResourceThumbnail(thumbnailPath);
//                }
                String path = uri.toString().split(":")[1];
                resource.setResourceOriginal(path);
                resource.setResourceType(Constant.RESOURCE_VIDEO);
                resource.save();
                list.add(resource);
                mReceivableReq.setResourcesList(list);
                Zleida.sCurrentVideoFile = null;
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
        //        @Bind(R.id.tv_video)
//        TextView mTvVideo;
        @Bind(R.id.iv_select_status)
        ImageView mIvSelectStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
