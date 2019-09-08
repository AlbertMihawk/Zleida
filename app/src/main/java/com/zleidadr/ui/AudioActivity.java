package com.zleidadr.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zleidadr.R;
import com.zleidadr.entity.Resource;
import com.zleidadr.manager.AudioManager;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AudioActivity extends AppCompatActivity {

    @Bind(R.id.tv_back)
    FrameLayout mTvBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.lv_audio_list)
    ListView mLvAudioList;
    private BaseAdapter mAdpater;
    private List<File> mFiles;
    private int mCurrentPlay = -1;
    private AudioManager mAudioManager;
    private AlertDialog mDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);
        mAudioManager = AudioManager.getInstance();
        mFiles = mAudioManager.getAudioFiles();
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        } else if (mDialog != null) {
            mDialog = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAudioManager.getPlayStatus() == AudioManager.PLAY_PLAY) {
            mAudioManager.stopPlaying();
            mCurrentPlay = -1;
            mAdpater.notifyDataSetChanged();
        }
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
        mTvTitle.setText("语音信息");

        mAdpater = new BaseAdapter() {
            @Override
            public int getCount() {
                if (mFiles == null) {
                    return 0;
                }
                return mFiles.size();
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
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ViewHolder holder;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_audio, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.mTvAudioName.setText(mFiles.get(position).getName());
                if (mCurrentPlay == position) {
                    holder.mIvPlayStatus.setImageResource(R.drawable.icon_pause_s);
                } else {
                    holder.mIvPlayStatus.setImageResource(R.drawable.icon_play);
                }

                return convertView;
            }
        };
        mLvAudioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentPlay != position) {
                    mCurrentPlay = position;
                    if (mAudioManager.getPlayStatus() == AudioManager.PLAY_PLAY
                            || mAudioManager.getPlayStatus() == AudioManager.PLAY_READY) {
                        mAudioManager.stopPlaying();
                        mAudioManager.releasePlaying();
                    }
                    mAudioManager.loadAudio(mFiles.get(position).getAbsolutePath());
                    mAudioManager.startPlaying(new AudioManager.OnAudioManagerListener() {
                        @Override
                        public void onSuccessListener() {
                            mCurrentPlay = -1;
                            mAdpater.notifyDataSetChanged();
                        }
                    });
                } else {
                    if (mAudioManager.getPlayStatus() == AudioManager.PLAY_PLAY) {
                        mAudioManager.stopPlaying();
                        mCurrentPlay = -1;
                    }
                }
                mAdpater.notifyDataSetChanged();
            }
        });
        mLvAudioList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AudioActivity.this);
                mDialog = builder.setMessage("确定要删除此录音吗?").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Resource> list = Resource.find(Resource.class, "resource_original = ?",mFiles.get(position).getName());
                        for (Resource resource : list) {
                            resource.delete();
                        }
                        mAudioManager.deleteAudioFile(mFiles.get(position).getName());
                        mFiles.remove(position);
                        mAdpater.notifyDataSetChanged();
                        mDialog.dismiss();
                    }
                }).create();
                mDialog.show();
                return true;
            }
        });
        mLvAudioList.setAdapter(mAdpater);
    }

    static class ViewHolder {
        @Bind(R.id.tv_audio_name)
        TextView mTvAudioName;
        @Bind(R.id.iv_play_status)
        ImageView mIvPlayStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
