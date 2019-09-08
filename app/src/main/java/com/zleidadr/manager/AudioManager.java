package com.zleidadr.manager;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;

import com.github.lassana.recorder.AudioRecorder;
import com.github.lassana.recorder.AudioRecorderBuilder;
import com.zleidadr.common.CommonUtils;
import com.zleidadr.common.Logger;
import com.zleidadr.common.TimeClock;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiaoxuli on 16/1/12.
 */
public class AudioManager {

    public static final int LASTFILE = -1;
    public static final String PRENAME = "aud_";
    public static final String EXTNAME = ".3gp";
    public static final int RECORDER_PAUSE = 0;
    public static final int RECORDER_RECORD = 1;
    public static final int RECORDER_INIT = -1;
    //    public static final int PLAY_PAUSE = 2;
    public static final int PLAY_PLAY = 1;
    public static final int PLAY_READY = 0;
    public static final int PLAY_INIT = -1;
    private static final int RECORD_DURATION = 1000 * 60 * 20;
    private static final String TAG = AudioManager.class.getName();
    private static AudioManager mAudioManager = null;
    private static TimeClock mTimeClock;
    private Context mCtx;
    private String mSubDir;
    private AudioRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    private int mRecordStatus = RECORDER_INIT;
    private int mPlayStatus = PLAY_INIT;
    private String mRecordingFile;


    private AudioManager(Context context, String subDirectory) {
        this.mCtx = context;
        this.mSubDir = subDirectory;
        mTimeClock = new TimeClock();
    }

    public static synchronized void init(Context context, String subDir) {
        if (mAudioManager == null) {
            mAudioManager = new AudioManager(context, subDir);
        }
    }

    /**
     * pls run init(context) method before getInstance().
     *
     * @return
     */
    public static AudioManager getInstance() {
        return mAudioManager;
    }

    public TimeClock getTimeClock() {
        return mTimeClock;
    }

    public void startRecording(final OnAudioManagerListener listener) {

        File dir = new File(CommonUtils.getReceivableDir(mCtx, mSubDir));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mRecordingFile = dir.getPath() + "/" + PRENAME + System.currentTimeMillis() + EXTNAME;
        Logger.i(TAG, "file path: " + mRecordingFile);
        mMediaRecorder = AudioRecorderBuilder.with(mCtx)
                .fileName(mRecordingFile)
                .config(AudioRecorder.MediaRecorderConfig.DEFAULT)
                .loggable()
                .build();
        mMediaRecorder.start(new AudioRecorder.OnStartListener() {
            @Override
            public void onStarted() {
                mTimeClock.run();
                mRecordStatus = RECORDER_RECORD;
                listener.onSuccessListener();
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void pauseRecording(final OnAudioManagerListener listener) {
        mMediaRecorder.pause(new AudioRecorder.OnPauseListener() {
            @Override
            public void onPaused(String activeRecordFileName) {
                mTimeClock.pause();
                mRecordStatus = RECORDER_PAUSE;
                listener.onSuccessListener();
            }

            @Override
            public void onException(Exception e) {
                //TODO 异常处理
                mTimeClock.pause();
                e.printStackTrace();
            }
        });
    }

    public void resumeRecording(final OnAudioManagerListener listener) {
        mMediaRecorder.start(new AudioRecorder.OnStartListener() {
            @Override
            public void onStarted() {
                mTimeClock.run();
                mRecordStatus = RECORDER_RECORD;
                listener.onSuccessListener();
            }

            @Override
            public void onException(Exception e) {

            }
        });
    }

    public synchronized void stopRecording(final OnAudioManagerListener listener) {
        if (mMediaRecorder.isPaused()) {
            mMediaRecorder.start(new AudioRecorder.OnStartListener() {
                @Override
                public void onStarted() {
                    mMediaRecorder.cancel();
                    mTimeClock.stop();
                    mMediaRecorder = null;
                    mRecordStatus = RECORDER_INIT;
                    listener.onSuccessListener();
                }

                @Override
                public void onException(Exception e) {
                    mTimeClock.stop();
                }
            });

        } else {
            mMediaRecorder.pause(new AudioRecorder.OnPauseListener() {
                @Override
                public void onPaused(String activeRecordFileName) {
                    stopRecording(listener);
                }

                @Override
                public void onException(Exception e) {

                }
            });
        }
    }

    public synchronized void cancelRecording(final OnAudioManagerListener listener) {
        if (mMediaRecorder.isPaused()) {
            mMediaRecorder.start(new AudioRecorder.OnStartListener() {
                @Override
                public void onStarted() {
                    mMediaRecorder.cancel();
                    mTimeClock.stop();
                    mMediaRecorder = null;
                    mRecordStatus = RECORDER_INIT;
                    deleteLastAudioFile();
                    listener.onSuccessListener();
                }

                @Override
                public void onException(Exception e) {
                    mTimeClock.stop();
                }
            });
        } else {
            mMediaRecorder.cancel();
            mTimeClock.stop();
            mMediaRecorder = null;
            mRecordStatus = RECORDER_INIT;
            deleteLastAudioFile();
            listener.onSuccessListener();
        }
    }

    public synchronized void deleteLastAudioFile() {
        if (mRecordingFile != null) {
            List<File> list = getAudioFiles();
            for (File file : list) {
                if (mRecordingFile.contains(file.getName())) {
                    file.delete();
                    mRecordingFile = null;
                    return;
                }
            }
        }

    }

    /**
     * @param position getAudioFiles()的位置,-1为最新文件
     */
    public void loadAudio(int position) {
        mMediaPlayer = new MediaPlayer();
        try {
            File file;
            LinkedList<File> list = getAudioFiles();
            if (position == LASTFILE) {
                file = list.getLast();
            } else {
                file = list.get(position);
            }
            mMediaPlayer.setDataSource(file.getAbsolutePath());
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "MediaPlay.prepare() failed");
        }
        mPlayStatus = PLAY_READY;
    }

    public void loadAudio(String fileName) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(fileName);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "MediaPlay.prepare() failed");
        }
        mPlayStatus = PLAY_READY;
    }

    public void startPlaying(final OnAudioManagerListener listener) {
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayStatus = PLAY_READY;
                listener.onSuccessListener();
            }
        });
        mPlayStatus = PLAY_PLAY;
    }
//    public void pausePlaying() {
//        mMediaPlayer.pause();
//        mPlayStatus = PLAY_PAUSE;
//    }

    public void stopPlaying() {
        mMediaPlayer.stop();
        mPlayStatus = PLAY_READY;
    }

    public void releasePlaying() {
        mMediaPlayer.release();
        mPlayStatus = PLAY_INIT;
    }

    public void deleteAudioFile(Integer[] nums) {
        LinkedList<File> list = getAudioFiles();
        for (Integer num : nums) {
            list.get(num).delete();
        }
    }

    public void deleteAudioFile(String fileName) {
        LinkedList<File> list = getAudioFiles();
        for (File file : list) {
            if (fileName.equals(file.getName())) {
                file.delete();
                break;
            }
        }
    }

    public void release() {
        mMediaRecorder = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mTimeClock != null) {
            mTimeClock.stop();
            mTimeClock = null;
        }
        mMediaPlayer = null;
        mAudioManager = null;
    }

    public String getRecordingFile() {
        return mRecordingFile;
    }

    public int getRecordeStatus() {
        return mRecordStatus;
    }

    public int getPlayStatus() {
        return mPlayStatus;
    }

    public interface OnAudioManagerListener {
        void onSuccessListener();
    }

    public LinkedList<File> getAudioFiles() {
        File dir = new File(CommonUtils.getReceivableDir(mCtx, mSubDir));
        LinkedList<File> list = null;
        if (dir.exists() && dir.isDirectory()) {
            list = new LinkedList<>();
            for (File file : dir.listFiles()) {
                if (file.getName().contains(PRENAME)) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public static LinkedList<File> getAudioFiles(Activity activity, String subDir) {
        File dir = new File(CommonUtils.getReceivableDir(activity, subDir));
        LinkedList<File> list = null;
        if (dir.exists() && dir.isDirectory()) {
            list = new LinkedList<>();
            for (File file : dir.listFiles()) {
                if (file.getName().contains(PRENAME)) {
                    list.add(file);
                }
            }
        }
        return list;
    }
}
