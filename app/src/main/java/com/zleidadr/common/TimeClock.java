package com.zleidadr.common;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaoxuli on 16/1/12.
 */
public class TimeClock {

    private static final int PREPARE = 0;
    private static final int START = 1;
    private static final int PAUSE = 2;

    private TimerTask mTt;
    private Timer mTimer;
    private int mTcStatus = PREPARE;
    private int mSecond = 0;
    private int mMinute = 0;

    private OnTimeClockListener mOnTimeClockListener;


    /**
     * 按秒计时00:00
     */
    public TimeClock() {
    }

    public void setOnTimeClockListener(OnTimeClockListener listener) {
        mOnTimeClockListener = listener;
    }

    public void run() {
        if (mTcStatus == PREPARE || mTcStatus == PAUSE) {
            mTt = new MyTimerTask();
            mTimer = new Timer();
            mTimer.schedule(mTt, 1000, 1000);
        }
        mTcStatus = START;
    }

    public void pause() {
        if (mTcStatus == START) {
            if (mTimer != null) {
                mTimer.cancel();
            }
            if (mTt != null) {
                mTt.cancel();
            }
            mTimer = null;
            mTt = null;
            mTcStatus = PAUSE;
        }
    }

    public void stop() {
        if (mTcStatus == START) {
            if (mTimer != null) {
                mTimer.cancel();
            }
            if (mTt != null) {
                mTt.cancel();
            }
            mTimer = null;
            mTt = null;
        }
        mSecond = 0;
        mMinute = 0;
        if (mOnTimeClockListener != null) {
            mOnTimeClockListener.onTimeChange(mMinute, mSecond);
        }
        mTcStatus = PREPARE;
    }

    public interface OnTimeClockListener {
        void onTimeChange(int minute, int second);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            mSecond++;
            if (mSecond >= 60) {
                mSecond = 0;
                mMinute++;
            }
            if (mOnTimeClockListener != null) {
                mOnTimeClockListener.onTimeChange(mMinute, mSecond);
            }
        }
    }
}
